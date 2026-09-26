package com.chronocritters.lib.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.chronocritters.lib.model.domain.Ability;
import com.chronocritters.lib.model.effects.DamageEffect;
import com.chronocritters.proto.player.PlayerProto.AbilityProto;

class AbilityMapperTest {

    private static Ability ability(int cooldownTurns) {
        return Ability.builder()
                .id("atk-test")
                .name("Test Strike")
                .description("for the mapper")
                .effects(List.of(DamageEffect.builder().id("eff-damage").description("hurts").damage(4).build()))
                .cooldownTurns(cooldownTurns)
                .build();
    }

    @Test
    @DisplayName("carries a cooldown over the wire, so gamelogic can hold the move back")
    void roundTripsTheCooldown() {
        AbilityProto proto = AbilityMapper.toProto(ability(3));

        assertThat(proto.getCooldownTurns()).isEqualTo(3);
        assertThat(AbilityMapper.toModel(proto).getCooldownTurns()).isEqualTo(3);
    }

    @Test
    @DisplayName("reads an ability with no cooldown as always available")
    void treatsAMissingCooldownAsZero() {
        AbilityProto withoutCooldown = AbilityProto.newBuilder(AbilityMapper.toProto(ability(0))).build();

        assertThat(AbilityMapper.toModel(withoutCooldown).getCooldownTurns()).isZero();
    }
}
