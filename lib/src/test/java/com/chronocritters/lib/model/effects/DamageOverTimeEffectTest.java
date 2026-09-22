package com.chronocritters.lib.model.effects;

import static com.chronocritters.lib.testsupport.BattleFixtures.PLAYER_ONE_ID;
import static com.chronocritters.lib.testsupport.BattleFixtures.PLAYER_TWO_ID;
import static com.chronocritters.lib.testsupport.BattleFixtures.ability;
import static com.chronocritters.lib.testsupport.BattleFixtures.battle;
import static com.chronocritters.lib.testsupport.BattleFixtures.critter;
import static com.chronocritters.lib.testsupport.BattleFixtures.player;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.domain.Ability;
import com.chronocritters.lib.model.enums.CritterType;

class DamageOverTimeEffectTest {

    private static final String ABILITY_ID = "eff-poison";
    private static final int DURATION = 3;
    private static final int DAMAGE_PER_TURN = 2;

    private BattleState battleState;

    @BeforeEach
    void setUp() {
        Ability ability = ability(ABILITY_ID, dotEffect());

        CritterState caster = critter("caster", CritterType.TOXIC, 10, 3, 3, ability);
        CritterState target = critter("target", CritterType.TOXIC, 20, 3, 3);

        battleState = battle(player(PLAYER_ONE_ID, caster), player(PLAYER_TWO_ID, target));
        battleState.getPlayer().setLastSelectedAbilityId(ABILITY_ID);
    }

    private DamageOverTimeEffect dotEffect() {
        return DamageOverTimeEffect.builder()
                .id("eff-damageovertime")
                .description("poisons")
                .damagePerTurn(DAMAGE_PER_TURN)
                .duration(DURATION)
                .build();
    }

    private DamageOverTimeEffect appliedEffect() {
        return (DamageOverTimeEffect) battleState.getOpponent().getActiveCritter().getActiveStatusEffects().get(0);
    }

    @Test
    @DisplayName("attaches a copy of the effect to the target, leaving the ability's own effect untouched")
    void attachesIndependentCopyToTarget() {
        DamageOverTimeEffect abilityEffect = (DamageOverTimeEffect) battleState.getPlayer()
                .getActiveCritter().getAbilityById(ABILITY_ID).getEffects().get(0);

        abilityEffect.onApply(battleState);

        assertThat(battleState.getOpponent().getActiveCritter().getActiveStatusEffects()).hasSize(1);
        assertThat(appliedEffect()).isNotSameAs(abilityEffect);
        assertThat(appliedEffect().getCasterId()).isEqualTo("caster");
    }

    @Test
    @DisplayName("ticks exactly as many times as its duration")
    void ticksOncePerTurnOfDuration() {
        CritterState target = battleState.getOpponent().getActiveCritter();
        dotEffect().onApply(battleState);
        DamageOverTimeEffect applied = appliedEffect();

        assertThat(applied.onTick(battleState, target)).isFalse();
        assertThat(applied.onTick(battleState, target)).isFalse();
        assertThat(applied.onTick(battleState, target)).as("expires on the last tick").isTrue();

        assertThat(target.getStats().getCurrentHp()).isEqualTo(20 - (DURATION * DAMAGE_PER_TURN));
    }

    @Test
    @DisplayName("credits every tick to the caster, not to the poisoned player")
    void creditsTicksToCaster() {
        CritterState target = battleState.getOpponent().getActiveCritter();
        dotEffect().onApply(battleState);

        appliedEffect().onTick(battleState, target);

        assertThat(battleState.getBattleStats().getPlayersDamageDealt())
                .containsEntry("caster", DAMAGE_PER_TURN);
    }

    @Test
    @DisplayName("re-applying refreshes the duration instead of stacking a second effect")
    void reapplyingRefreshesDuration() {
        CritterState target = battleState.getOpponent().getActiveCritter();
        dotEffect().onApply(battleState);
        appliedEffect().onTick(battleState, target);
        assertThat(appliedEffect().getDuration()).isEqualTo(DURATION - 1);

        dotEffect().onApply(battleState);

        assertThat(battleState.getOpponent().getActiveCritter().getActiveStatusEffects()).hasSize(1);
        assertThat(appliedEffect().getDuration()).isEqualTo(DURATION);
    }

    @Test
    @DisplayName("never drives health below zero")
    void clampsHealthAtZero() {
        CritterState target = battleState.getOpponent().getActiveCritter();
        target.getStats().setCurrentHp(1);
        dotEffect().onApply(battleState);

        appliedEffect().onTick(battleState, target);

        assertThat(target.getStats().getCurrentHp()).isZero();
    }
}
