package com.chronocritters.gamelogic.handler;

import static com.chronocritters.lib.testsupport.BattleFixtures.PLAYER_ONE_ID;
import static com.chronocritters.lib.testsupport.BattleFixtures.PLAYER_TWO_ID;
import static com.chronocritters.lib.testsupport.BattleFixtures.battle;
import static com.chronocritters.lib.testsupport.BattleFixtures.critter;
import static com.chronocritters.lib.testsupport.BattleFixtures.player;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.effects.SkipTurnEffect;
import com.chronocritters.lib.model.enums.CritterType;

class TurnTransitionHandlerTest {

    private BattleState battleState;
    private TurnTransitionHandler handler;

    @BeforeEach
    void setUp() {
        battleState = battle(
                player(PLAYER_ONE_ID, critter("one", CritterType.FIRE, 10, 3, 3)),
                player(PLAYER_TWO_ID, critter("two", CritterType.WATER, 10, 3, 3)));
        handler = new TurnTransitionHandler();
    }

    private void stun(CritterState critter) {
        critter.getActiveStatusEffects().add(
                SkipTurnEffect.builder().id("eff-skipturn").description("stuns").duration(2).build());
    }

    @Test
    @DisplayName("hands the turn to the opponent and resets the clock")
    void passesTurnToOpponent() {
        battleState.setTimeRemaining(3);

        handler.handle(battleState);

        assertThat(battleState.getActivePlayerId()).isEqualTo(PLAYER_TWO_ID);
        assertThat(battleState.getPlayerOne().getHasTurn()).isFalse();
        assertThat(battleState.getPlayerTwo().getHasTurn()).isTrue();
        assertThat(battleState.getTimeRemaining()).isEqualTo(30);
    }

    @Test
    @DisplayName("counts the turn that just ended")
    void incrementsTurnCount() {
        int before = battleState.getBattleStats().getTurnCount();

        handler.handle(battleState);

        assertThat(battleState.getBattleStats().getTurnCount()).isEqualTo(before + 1);
    }

    @Test
    @DisplayName("skips a stunned player so the turn returns to their opponent")
    void skipsAStunnedPlayer() {
        stun(battleState.getPlayerTwo().getActiveCritter());

        handler.handle(battleState);

        assertThat(battleState.getActivePlayerId()).isEqualTo(PLAYER_ONE_ID);
        assertThat(battleState.getPlayerOne().getHasTurn()).isTrue();
    }

    @Test
    @DisplayName("terminates when both actives are stunned instead of recursing forever")
    void terminatesWhenBothPlayersAreStunned() {
        stun(battleState.getPlayerOne().getActiveCritter());
        stun(battleState.getPlayerTwo().getActiveCritter());
        int before = battleState.getBattleStats().getTurnCount();

        handler.handle(battleState);

        // Both are stunned, so the turn lands anyway and the clock runs; the
        // stuns tick down and the standoff resolves on a later turn.
        assertThat(battleState.getActivePlayerId()).isNotNull();
        assertThat(battleState.getBattleStats().getTurnCount()).isLessThanOrEqualTo(before + 2);
        assertThat(battleState.getTimeRemaining()).isEqualTo(30);
    }

    @Test
    @DisplayName("does nothing once the battle has no active player")
    void doesNothingWithoutAnActivePlayer() {
        battleState.setActivePlayerId(null);
        int before = battleState.getBattleStats().getTurnCount();

        handler.handle(battleState);

        assertThat(battleState.getActivePlayerId()).isNull();
        assertThat(battleState.getBattleStats().getTurnCount()).isEqualTo(before);
    }
}
