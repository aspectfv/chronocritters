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

class SkipTurnEffectTest {

    private static final String ABILITY_ID = "eff-stun";
    private static final int DURATION = 2;

    private BattleState battleState;

    @BeforeEach
    void setUp() {
        Ability ability = ability(ABILITY_ID, stunEffect());

        CritterState caster = critter("caster", CritterType.KINETIC, 10, 3, 3, ability);
        CritterState target = critter("target", CritterType.KINETIC, 10, 3, 3);

        battleState = battle(player(PLAYER_ONE_ID, caster), player(PLAYER_TWO_ID, target));
        battleState.getPlayer().setLastSelectedAbilityId(ABILITY_ID);
    }

    private SkipTurnEffect stunEffect() {
        return SkipTurnEffect.builder().id("eff-skipturn").description("stuns").duration(DURATION).build();
    }

    private SkipTurnEffect appliedEffect() {
        return (SkipTurnEffect) battleState.getOpponent().getActiveCritter().getActiveStatusEffects().get(0);
    }

    @Test
    @DisplayName("stuns the opposing active critter")
    void stunsTheTarget() {
        stunEffect().onApply(battleState);

        assertThat(battleState.getOpponent().getActiveCritter().getActiveStatusEffects())
                .singleElement()
                .isInstanceOf(SkipTurnEffect.class);
    }

    @Test
    @DisplayName("expires after its duration has ticked down")
    void expiresAfterDuration() {
        CritterState target = battleState.getOpponent().getActiveCritter();
        stunEffect().onApply(battleState);
        SkipTurnEffect applied = appliedEffect();

        assertThat(applied.onTick(battleState, target)).isFalse();
        assertThat(applied.onTick(battleState, target)).isTrue();
    }

    @Test
    @DisplayName("re-applying refreshes the duration instead of stacking")
    void reapplyingRefreshesDuration() {
        CritterState target = battleState.getOpponent().getActiveCritter();
        stunEffect().onApply(battleState);
        appliedEffect().onTick(battleState, target);

        stunEffect().onApply(battleState);

        assertThat(battleState.getOpponent().getActiveCritter().getActiveStatusEffects()).hasSize(1);
        assertThat(appliedEffect().getDuration()).isEqualTo(DURATION);
    }
}
