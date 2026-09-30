package com.chronocritters.gamelogic.handler;

import java.util.concurrent.ThreadLocalRandom;

import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.BattleStats;
import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.battle.PlayerState;
import com.chronocritters.lib.model.enums.StatusCondition;

import lombok.RequiredArgsConstructor;

/**
 * Ends the turn that was just taken and decides who moves next.
 *
 * Play runs in rounds. Each player acts once a round, and the faster active
 * critter acts first, so a player whose critter outpaces the opponent's can
 * act twice in a row across the boundary: last in one round, first in the
 * next. A player still picks their move when their turn comes up, so the
 * order is fixed at the start of a round from the speeds on the field then.
 */
@RequiredArgsConstructor
public class TurnTransitionHandler extends AbstractTurnActionHandler {

    @Override
    public void handle(BattleState battleState) {
        if (battleState.getPlayer() == null || battleState.getOpponent() == null) return;

        endTurn(battleState);
        passTurn(battleState);
        skipSleepers(battleState);
    }

    /**
     * Orders the round that was held back for a replacement. Call once the last
     * fainted critter has been replaced; it does nothing if no round is waiting.
     */
    public static void resumeRound(BattleState battleState) {
        if (!battleState.isRoundPending()) return;

        battleState.setRoundPending(false);
        startRound(battleState);
        skipSleepers(battleState);
    }

    /**
     * Gives the turn to whoever moves first this round and queues the other.
     *
     * A priority move beats speed, and speed beats a tie. A tie goes to the
     * player who did not take the last turn, which keeps an even matchup
     * alternating. With no last turn, at the start of a battle, a coin decides
     * it rather than the order the players joined the queue in.
     */
    public static void startRound(BattleState battleState) {
        PlayerState playerOne = battleState.getPlayerOne();
        PlayerState playerTwo = battleState.getPlayerTwo();
        CritterState critterOne = playerOne.getActiveCritter();
        CritterState critterTwo = playerTwo.getActiveCritter();

        PlayerState first;
        String reason = null;
        if (critterOne.isPriority() != critterTwo.isPriority()) {
            first = critterOne.isPriority() ? playerOne : playerTwo;
            reason = "seized the initiative";
        } else if (critterOne.effectiveSpeed() != critterTwo.effectiveSpeed()) {
            first = critterOne.effectiveSpeed() > critterTwo.effectiveSpeed() ? playerOne : playerTwo;
            reason = "is faster";
        } else if (battleState.getActivePlayerId() != null) {
            first = battleState.getPlayerById(battleState.getActivePlayerId()) == playerOne ? playerTwo : playerOne;
        } else {
            first = ThreadLocalRandom.current().nextBoolean() ? playerOne : playerTwo;
        }

        critterOne.setPriority(false);
        critterTwo.setPriority(false);

        // Moving twice in a row is the one case a player would not expect from
        // taking turns, so it is the one the log explains.
        if (reason != null && first.getId().equals(battleState.getActivePlayerId())) {
            battleState.getActionLogHistory().add(String.format("%s %s and moves again!",
                first.getActiveCritter().getName(), reason));
        }

        PlayerState second = first == playerOne ? playerTwo : playerOne;
        giveTurnTo(battleState, first);
        battleState.setNextInRound(second.getId());
    }

    private static void endTurn(BattleState battleState) {
        BattleStats battleStats = battleState.getBattleStats();
        battleStats.setTurnCount(battleStats.getTurnCount() + 1);

        tickCooldowns(battleState.getPlayer().getActiveCritter());
    }

    /**
     * Hands the turn to the other player if they have yet to act this round,
     * and otherwise starts the next one.
     *
     * A critter that faints before it acts loses its place in the round, so a
     * replacement does not come in and move straight away. It waits for the
     * next round's order like anything else, which is what makes a slow switch
     * in take a hit before it can answer.
     */
    private static void passTurn(BattleState battleState) {
        String next = battleState.getNextInRound();
        battleState.setNextInRound(null);

        if (next != null && !battleState.awaitsSwitchFrom(next)) {
            giveTurnTo(battleState, battleState.getPlayerById(next));
            return;
        }

        if (battleState.isAwaitingSwitch()) {
            battleState.setRoundPending(true);
            return;
        }

        startRound(battleState);
    }

    /**
     * A sleeping critter's owner loses the turn, and each lost turn is one off
     * the sleep. The loop therefore ends on its own, even with both actives
     * asleep: it cannot pass over more turns than the two of them have left to
     * sleep between them.
     *
     * A player who owes a replacement is still holding the critter that just
     * fainted, and a sleep on it is not a sleep on whoever they send out.
     */
    private static void skipSleepers(BattleState battleState) {
        while (!battleState.isRoundPending()
                && !battleState.awaitsSwitchFrom(battleState.getActivePlayerId())
                && sleepsThroughTurn(battleState, battleState.getPlayer())) {
            endTurn(battleState);
            passTurn(battleState);
        }

        battleState.setTimeRemaining(battleState.getTurnDuration());
    }

    private static void giveTurnTo(BattleState battleState, PlayerState player) {
        battleState.getPlayerOne().setHasTurn(player == battleState.getPlayerOne());
        battleState.getPlayerTwo().setHasTurn(player == battleState.getPlayerTwo());
        battleState.setActivePlayerId(player.getId());
    }

    /**
     * Cooldowns belong to the critter on the field, and they run down at the end
     * of its own turn rather than on every turn in the battle. Counting on the
     * way out means the number a player is shown is the number of their own turns
     * they still have to wait.
     */
    private static void tickCooldowns(CritterState critter) {
        if (critter == null) return;

        critter.getAbilityCooldowns().replaceAll((abilityId, turnsLeft) -> turnsLeft - 1);
        critter.getAbilityCooldowns().values().removeIf(turnsLeft -> turnsLeft <= 0);
    }

    /** Spends the turn that has just arrived if the player's active is asleep. */
    private static boolean sleepsThroughTurn(BattleState battleState, PlayerState player) {
        CritterState critter = player.getActiveCritter();
        if (!critter.hasStatus(StatusCondition.SLEEP)) return false;

        if (critter.getStatus().sleepThroughTurn(battleState, critter)) {
            critter.endStatus();
        }
        return true;
    }
}
