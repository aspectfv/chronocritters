package com.chronocritters.gamelogic.handler;

import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.BattleStats;
import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.battle.PlayerState;
import com.chronocritters.lib.model.enums.StatusCondition;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TurnTransitionHandler extends AbstractTurnActionHandler {

    @Override
    public void handle(BattleState battleState) {
        if (!advanceTurn(battleState)) return;

        // A sleeping critter's owner loses the turn, and each lost turn is one
        // off the sleep. The loop therefore ends on its own, even with both
        // actives asleep: it cannot pass over more turns than the two of them
        // have left to sleep between them.
        //
        // A player who owes a replacement is still holding the critter that
        // just fainted, and a sleep on it is not a sleep on whoever they send
        // out. Reading it as one handed the turn back to their attacker, who
        // then played another move into the empty slot.
        while (!battleState.awaitsSwitchFrom(battleState.getActivePlayerId())
                && sleepsThroughTurn(battleState, battleState.getPlayer())) {
            advanceTurn(battleState);
        }

        battleState.setTimeRemaining(battleState.getTurnDuration());
    }

    private boolean advanceTurn(BattleState battleState) {
        PlayerState currentPlayer = battleState.getPlayer();
        PlayerState nextPlayer = battleState.getOpponent();

        if (currentPlayer == null || nextPlayer == null) return false;

        BattleStats battleStats = battleState.getBattleStats();
        battleStats.setTurnCount(battleStats.getTurnCount() + 1);

        tickCooldowns(currentPlayer.getActiveCritter());
        giveTurnTo(battleState, nextPlayer);

        return true;
    }

    private void giveTurnTo(BattleState battleState, PlayerState player) {
        if (player == null) return;

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
    private void tickCooldowns(CritterState critter) {
        if (critter == null) return;

        critter.getAbilityCooldowns().replaceAll((abilityId, turnsLeft) -> turnsLeft - 1);
        critter.getAbilityCooldowns().values().removeIf(turnsLeft -> turnsLeft <= 0);
    }

    /** Spends the turn that has just arrived if the player's active is asleep. */
    private boolean sleepsThroughTurn(BattleState battleState, PlayerState player) {
        CritterState critter = player.getActiveCritter();
        if (!critter.hasStatus(StatusCondition.SLEEP)) return false;

        if (critter.getStatus().sleepThroughTurn(battleState, critter)) {
            critter.endStatus();
        }
        return true;
    }
}
