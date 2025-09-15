package com.chronocritters.lib.util;

import com.chronocritters.lib.mapper.PlayerProtoMapper;
import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.battle.PlayerState;
import com.chronocritters.lib.model.domain.Ability;
import com.chronocritters.lib.model.domain.BaseStats;
import com.chronocritters.lib.model.domain.Critter;
import com.chronocritters.lib.model.effects.DamageEffect;
import com.chronocritters.lib.model.enums.CritterType;
import com.chronocritters.proto.player.PlayerProto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlayerProtoMapperTest {

    private PlayerProto.PlayerResponse validPlayerResponseProto;
    private Critter validCritterModel;

    @BeforeEach
    void setUp() {
        // --- Setup for Proto-to-Model tests ---
        PlayerProto.DamageEffectProto damageEffectProto = PlayerProto.DamageEffectProto.newBuilder().setDamage(10).build();
        PlayerProto.EffectProto damageProto = PlayerProto.EffectProto.newBuilder()
                .setId("effect-dmg")
                .setDescription("Deals damage")
                .setDamageEffect(damageEffectProto)
                .build();
        PlayerProto.AbilityProto abilityProto = PlayerProto.AbilityProto.newBuilder()
                .setId("ability-1")
                .setName("Tackle")
                .setDescription("A basic attack")
                .addEffects(damageProto)
                .build();
        PlayerProto.BaseStatsProto baseStatsProto = PlayerProto.BaseStatsProto.newBuilder()
                .setHealth(100)
                .setAttack(50)
                .setDefense(30)
                .build();
        PlayerProto.CritterProto critterProto = PlayerProto.CritterProto.newBuilder()
                .setId("critter-1")
                .setName("TestCritter")
                .setDescription("A critter for testing")
                .setType(PlayerProto.CritterTypeProto.FIRE)
                .setBaseStats(baseStatsProto)
                .addAbilities(abilityProto)
                .build();

        validPlayerResponseProto = PlayerProto.PlayerResponse.newBuilder()
                .setId("player-1")
                .setUsername("TestPlayer")
                .addRoster(critterProto)
                .build();

        // --- Setup for Model-to-Proto tests ---
        DamageEffect damageEffect = DamageEffect.builder().id("effect-dmg").description("Deals damage").damage(10).build();
        Ability ability = Ability.builder()
                .id("ability-1")
                .name("Tackle")
                .description("A basic attack")
                .effects(List.of(damageEffect))
                .build();
        BaseStats baseStats = BaseStats.builder().health(100).attack(50).defense(30).build();

        validCritterModel = Critter.builder()
                .id("critter-1")
                .name("TestCritter")
                .description("A critter for testing")
                .type(CritterType.FIRE)
                .baseStats(baseStats)
                .abilities(List.of(ability))
                .build();
    }

    @Nested
    @DisplayName("Protobuf to Model Conversion")
    class ProtoToModelTests {
        @Test
        @DisplayName("should correctly convert a valid PlayerResponseProto to PlayerState")
        void convertToPlayerState_withValidProto_shouldMapAllFields() {
            // When
            PlayerState result = PlayerProtoMapper.convertToPlayerState(validPlayerResponseProto);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("player-1");
            assertThat(result.getUsername()).isEqualTo("TestPlayer");
            assertThat(result.getActiveCritterIndex()).isEqualTo(0);
            assertThat(result.getRoster()).hasSize(1);

            CritterState critterState = result.getActiveCritter();
            assertThat(critterState.getId()).isEqualTo("critter-1");
            assertThat(critterState.getName()).isEqualTo("TestCritter");
            assertThat(critterState.getType()).isEqualTo(CritterType.FIRE);
            assertThat(critterState.isFainted()).isFalse();

            assertThat(critterState.getStats().getMaxHp()).isEqualTo(100);
            assertThat(critterState.getStats().getCurrentHp()).isEqualTo(100);
            assertThat(critterState.getStats().getCurrentAtk()).isEqualTo(50);
            assertThat(critterState.getStats().getCurrentDef()).isEqualTo(30);

            assertThat(critterState.getAbilities()).hasSize(1);
            Ability ability = critterState.getAbilities().get(0);
            assertThat(ability.getId()).isEqualTo("ability-1");
            assertThat(ability.getName()).isEqualTo("Tackle");
            assertThat(ability.getEffects()).hasSize(1);

            DamageEffect effect = (DamageEffect) ability.getEffects().get(0);
            assertThat(effect.getId()).isEqualTo("effect-dmg");
            assertThat(effect.getDamage()).isEqualTo(10);
        }

        @Test
        @DisplayName("should throw exception for null PlayerResponseProto")
        void convertToPlayerState_withNullProto_shouldThrowException() {
            assertThatThrownBy(() -> PlayerProtoMapper.convertToPlayerState(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("PlayerResponse cannot be null");
        }

        @Test
        @DisplayName("should throw exception for blank player ID")
        void convertToPlayerState_withBlankId_shouldThrowException() {
            PlayerProto.PlayerResponse invalidProto = validPlayerResponseProto.toBuilder().setId(" ").build();
            assertThatThrownBy(() -> PlayerProtoMapper.convertToPlayerState(invalidProto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Player ID from PlayerResponse cannot be blank");
        }
        
        @Test
        @DisplayName("should throw exception for empty roster")
        void convertToPlayerState_withEmptyRoster_shouldThrowException() {
            PlayerProto.PlayerResponse invalidProto = validPlayerResponseProto.toBuilder().clearRoster().build();
            assertThatThrownBy(() -> PlayerProtoMapper.convertToPlayerState(invalidProto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Roster list from PlayerResponse cannot be empty");
        }

        @Test
        @DisplayName("should throw exception for critter with invalid base stats")
        void convertToPlayerState_withInvalidCritterStats_shouldThrowException() {
             PlayerProto.BaseStatsProto invalidStats = PlayerProto.BaseStatsProto.newBuilder().setHealth(0).build();
             PlayerProto.CritterProto invalidCritter = validPlayerResponseProto.getRoster(0).toBuilder()
                     .setBaseStats(invalidStats)
                     .build();
             PlayerProto.PlayerResponse invalidProto = validPlayerResponseProto.toBuilder()
                     .setRoster(0, invalidCritter)
                     .build();

             assertThatThrownBy(() -> PlayerProtoMapper.convertToPlayerState(invalidProto))
                     .isInstanceOf(IllegalArgumentException.class)
                     .hasMessage("BaseStats health must be at least 1");
        }
    }

    @Nested
    @DisplayName("Model to Protobuf Conversion")
    class ModelToProtoTests {
        @Test
        @DisplayName("should correctly convert a valid Critter model to CritterProto")
        void convertCritterModelToProto_withValidModel_shouldMapAllFields() {
            // When
            PlayerProto.CritterProto result = PlayerProtoMapper.convertCritterModelToProto(validCritterModel);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("critter-1");
            assertThat(result.getName()).isEqualTo("TestCritter");
            assertThat(result.getDescription()).isEqualTo("A critter for testing");
            assertThat(result.getType()).isEqualTo(PlayerProto.CritterTypeProto.FIRE);

            assertThat(result.getBaseStats().getHealth()).isEqualTo(100);
            assertThat(result.getBaseStats().getAttack()).isEqualTo(50);
            assertThat(result.getBaseStats().getDefense()).isEqualTo(30);
            
            assertThat(result.getAbilitiesCount()).isEqualTo(1);
            PlayerProto.AbilityProto abilityProto = result.getAbilities(0);
            assertThat(abilityProto.getId()).isEqualTo("ability-1");
            assertThat(abilityProto.getName()).isEqualTo("Tackle");

            assertThat(abilityProto.getEffectsCount()).isEqualTo(1);
            PlayerProto.EffectProto effectProto = abilityProto.getEffects(0);
            assertThat(effectProto.getId()).isEqualTo("effect-dmg");
            assertThat(effectProto.hasDamageEffect()).isTrue();
            assertThat(effectProto.getDamageEffect().getDamage()).isEqualTo(10);
        }

        @Test
        @DisplayName("should throw exception for null Critter model")
        void convertCritterModelToProto_withNullModel_shouldThrowException() {
            assertThatThrownBy(() -> PlayerProtoMapper.convertCritterModelToProto(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Critter cannot be null");
        }

        @Test
        @DisplayName("should throw exception for critter with blank name")
        void convertCritterModelToProto_withBlankName_shouldThrowException() {
            Critter invalidCritter = Critter.builder()
                .id("id")
                .name(" ")
                .description("desc")
                .type(CritterType.FIRE)
                .baseStats(BaseStats.builder().build())
                .abilities(Collections.emptyList())
                .build();
            
            assertThatThrownBy(() -> PlayerProtoMapper.convertCritterModelToProto(invalidCritter))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Critter name cannot be blank");
        }
    }
}