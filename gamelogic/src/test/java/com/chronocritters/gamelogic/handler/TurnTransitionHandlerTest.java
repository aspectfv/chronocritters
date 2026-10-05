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
import com.chronocritters.lib.model.effects.StatusEffect;
import com.chronocritters.lib.model.enums.CritterType;
import com.chronocritters.lib.model.enums.StatusCondition;

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

    private void sleep(CritterState critter, int turns) {
        critter.setStatus(StatusEffect.builder().id("eff-sleep").description("sleeps")
                .condition(StatusCondition.SLEEP).duration(turns).build());
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
    @DisplayName("a sleep on the fainted critter does not hand the turn back to its attacker")
    void aSleepOnTheFaintedCritterDoesNotSkipTheReplacement() {
        // The knocked-out critter keeps the sleep that was on it. Reading that
        // as "this player is asleep, skip them" handed the turn straight back to
        // the attacker, who then played another move into the empty slot.
        sleep(battleState.getPlayerTwo().getActiveCritter(), 2);
        battleState.getAwaitingSwitchPlayerIds().add(PLAYER_TWO_ID);

        handler.handle(battleState);

        assertThat(battleState.getActivePlayerId()).isEqualTo(PLAYER_TWO_ID);
    }

    @Test
    @DisplayName("skips a sleeping player, spending one turn of the sleep, so the turn returns to their opponent")
    void skipsASleepingPlayer() {
        CritterState sleeper = battleState.getPlayerTwo().getActiveCritter();
        sleep(sleeper, 2);

        handler.handle(battleState);

        assertThat(battleState.getActivePlayerId()).isEqualTo(PLAYER_ONE_ID);
        assertThat(battleState.getPlayerOne().getHasTurn()).isTrue();
        assertThat(sleeper.getStatus().getDuration()).isEqualTo(1);
    }

    @Test
    @DisplayName("a sleep skips exactly as many of its owner's turns as its duration, then wakes them immune")
    void sleepSkipsItsDurationThenWakes() {
        CritterState sleeper = battleState.getPlayerTwo().getActiveCritter();
        sleep(sleeper, 2);

        handler.handle(battleState);
        handler.handle(battleState);

        assertThat(sleeper.getStatus()).isNull();
        assertThat(sleeper.getStatusImmunity()).isEqualTo(StatusCondition.SLEEP);
        assertThat(battleState.getActivePlayerId()).as("the opponent moves while the sleeper wakes").isEqualTo(PLAYER_ONE_ID);

        handler.handle(battleState);

        assertThat(battleState.getActivePlayerId()).isEqualTo(PLAYER_TWO_ID);
    }

    @Test
    @DisplayName("two sleeping actives pass turns between them until one wakes, with no cap on the loop")
    void terminatesWhenBothActivesAreAsleep() {
        CritterState one = battleState.getPlayerOne().getActiveCritter();
        CritterState two = battleState.getPlayerTwo().getActiveCritter();
        sleep(one, 3);
        sleep(two, 5);
        int before = battleState.getBattleStats().getTurnCount();

        handler.handle(battleState);

        // Two skips each round until player one's three are spent on its third
        // skip; player two then skips a fourth time and player one moves.
        assertThat(battleState.getActivePlayerId()).isEqualTo(PLAYER_ONE_ID);
        assertThat(one.getStatus()).isNull();
        assertThat(two.getStatus().getDuration()).isEqualTo(1);
        assertThat(battleState.getBattleStats().getTurnCount()).isEqualTo(before + 1 + 3 + 4);
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
