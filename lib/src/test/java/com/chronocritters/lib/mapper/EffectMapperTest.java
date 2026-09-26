package com.chronocritters.lib.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.chronocritters.lib.model.domain.Effect;
import com.chronocritters.lib.model.effects.CleanseEffect;
import com.chronocritters.lib.model.effects.DamageEffect;
import com.chronocritters.lib.model.effects.DamageOverTimeEffect;
import com.chronocritters.lib.model.effects.HealEffect;
import com.chronocritters.lib.model.effects.LifestealEffect;
import com.chronocritters.lib.model.effects.ShieldEffect;
import com.chronocritters.lib.model.effects.SkipTurnEffect;
import com.chronocritters.lib.model.effects.StatModifierEffect;
import com.chronocritters.lib.model.enums.Stat;
import com.chronocritters.proto.player.PlayerProto.EffectProto;
import com.chronocritters.proto.player.PlayerProto.StatModifierEffectProto;

class EffectMapperTest {

    static Stream<Effect> everyEffect() {
        return Stream.of(
                DamageEffect.builder().id("eff-strike").description("hits").damage(4).build(),
                DamageOverTimeEffect.builder().id("eff-burn").description("burns").damagePerTurn(2).duration(3).build(),
                SkipTurnEffect.builder().id("eff-stun").description("stuns").duration(2).build(),
                StatModifierEffect.builder().id("eff-kindle").description("kindles").stat(Stat.ATTACK).stages(1).build(),
                StatModifierEffect.builder().id("eff-erode").description("erodes").stat(Stat.DEFENSE).stages(-1).build(),
                HealEffect.builder().id("eff-mend").description("mends").healPercent(40).build(),
                LifestealEffect.builder().id("eff-drain").description("drains").damage(3).drainPercent(50).build(),
                ShieldEffect.builder().id("eff-ward").description("wards").amount(5).duration(3).build(),
                CleanseEffect.builder().id("eff-cleanse").description("cleanses").build());
    }

    @ParameterizedTest
    @MethodSource("everyEffect")
    @DisplayName("every effect survives the trip to proto and back as the same class with the same numbers")
    void roundTrips(Effect effect) {
        EffectProto proto = EffectMapper.toProto(effect);

        assertThat(EffectMapper.toModel(proto)).isEqualTo(effect);
    }

    @ParameterizedTest
    @MethodSource("everyEffect")
    @DisplayName("a lifesteal is carried as its own case, not as the plain damage it extends")
    void keepsTheSubtype(Effect effect) {
        EffectProto proto = EffectMapper.toProto(effect);

        assertThat(EffectMapper.toModel(proto).getClass()).isEqualTo(effect.getClass());
    }

    @org.junit.jupiter.api.Test
    @DisplayName("refuses a stat shift of zero stages or of no stat")
    void refusesAnEmptyShift() {
        EffectProto noStages = EffectProto.newBuilder().setId("eff-x").setDescription("x")
                .setStatModifierEffect(StatModifierEffectProto.newBuilder().setStages(0)).build();
        EffectProto noStat = EffectProto.newBuilder().setId("eff-x").setDescription("x")
                .setStatModifierEffect(StatModifierEffectProto.newBuilder().setStages(1)).build();

        assertThatThrownBy(() -> EffectMapper.toModel(noStages)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> EffectMapper.toModel(noStat)).isInstanceOf(IllegalArgumentException.class);
    }
}
