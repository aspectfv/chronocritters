package com.chronocritters.lib.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.chronocritters.lib.model.battle.PlayerState;
import com.chronocritters.lib.model.domain.BaseStats;
import com.chronocritters.lib.model.domain.Critter;
import com.chronocritters.lib.model.effects.DamageEffect;
import com.chronocritters.lib.model.enums.CritterType;
import com.chronocritters.proto.player.PlayerProto.PlayerResponse;

class PlayerMapperTest {

    private Critter critter(String id, CritterType type, int health, int attack, int defense) {
        return Critter.builder()
                .id(id)
                .name(id)
                .description(id)
                .type(type)
                .baseStats(BaseStats.builder().health(health).attack(attack).defense(defense).build())
                .abilities(List.of(com.chronocritters.lib.model.domain.Ability.builder()
                        .id("atk-" + id)
                        .name("Attack " + id)
                        .description("hits")
                        .effects(List.of(DamageEffect.builder().id("eff-damage").description("hurts").damage(1).build()))
                        .build()))
                .build();
    }

    private PlayerResponse response(Critter... roster) {
        PlayerResponse.Builder builder = PlayerResponse.newBuilder().setId("p1").setUsername("BlueOak");
        for (Critter critter : roster) {
            builder.addRoster(CritterMapper.toProto(critter));
        }
        return builder.build();
    }

    @Test
    @DisplayName("maps a player's roster into full-health battle critters")
    void mapsRosterToFullHealthBattleState() {
        PlayerState playerState = PlayerMapper.toPlayerState(response(critter("aqualing", CritterType.WATER, 5, 3, 4)));

        assertThat(playerState.getId()).isEqualTo("p1");
        assertThat(playerState.getUsername()).isEqualTo("BlueOak");
        assertThat(playerState.getActiveCritterIndex()).isZero();
        assertThat(playerState.getRoster()).singleElement().satisfies(critter -> {
            assertThat(critter.getId()).isEqualTo("aqualing");
            assertThat(critter.getType()).isEqualTo(CritterType.WATER);
            assertThat(critter.getStats().getMaxHp()).isEqualTo(5);
            assertThat(critter.getStats().getCurrentHp()).isEqualTo(5);
            assertThat(critter.getStats().getCurrentAtk()).isEqualTo(3);
            assertThat(critter.getStats().getCurrentDef()).isEqualTo(4);
            assertThat(critter.isFainted()).isFalse();
        });
    }

    @Test
    @DisplayName("rejects a player with no critters rather than starting an unplayable battle")
    void rejectsEmptyRoster() {
        assertThatThrownBy(() -> PlayerMapper.toPlayerState(response()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Roster list");
    }

    @Test
    @DisplayName("rejects a null or unidentified player")
    void rejectsMissingIdentity() {
        assertThatThrownBy(() -> PlayerMapper.toPlayerState(null))
                .isInstanceOf(IllegalArgumentException.class);

        PlayerResponse withoutId = PlayerResponse.newBuilder()
                .setUsername("BlueOak")
                .addRoster(CritterMapper.toProto(critter("aqualing", CritterType.WATER, 5, 3, 4)))
                .build();

        assertThatThrownBy(() -> PlayerMapper.toPlayerState(withoutId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Player ID");
    }

    @Test
    @DisplayName("survives a round trip through proto without losing critter data")
    void roundTripsThroughProto() {
        Critter original = critter("strikon", CritterType.KINETIC, 5, 5, 2);

        Critter mapped = CritterMapper.toModel(CritterMapper.toProto(original));

        assertThat(mapped.getId()).isEqualTo(original.getId());
        assertThat(mapped.getName()).isEqualTo(original.getName());
        assertThat(mapped.getType()).isEqualTo(original.getType());
        assertThat(mapped.getBaseStats().getHealth()).isEqualTo(original.getBaseStats().getHealth());
        assertThat(mapped.getAbilities()).hasSize(1);
        assertThat(mapped.getAbilities().get(0).getEffects()).hasSize(1);
    }
}
