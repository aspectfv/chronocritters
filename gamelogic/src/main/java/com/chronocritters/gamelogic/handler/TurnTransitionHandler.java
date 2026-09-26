package com.chronocritters.gamelogic.handler;

import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.BattleStats;
import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.battle.PlayerState;
import com.chronocritters.lib.model.effects.SkipTurnEffect;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TurnTransitionHandler extends AbstractTurnActionHandler {

    /**
     * A stunned player loses their turn, but with both actives stunned there is
     * nobody left to hand the turn to. Capping the skips lets the turn land and
     * the clock run; TurnEffectsHandler ticks the stuns down so the standoff
     * resolves on its own.
     */
    private static final int MAX_CONSECUTIVE_SKIPS = 2;

    @Override
    public void handle(BattleState battleState) {
        for (int skips = 0; skips < MAX_CONSECUTIVE_SKIPS; skips++) {
            if (!advanceTurn(battleState)) return;

            // A player who owes a replacement is still holding the critter that
            // just fainted, and a stun on it is not a stun on whoever they send
            // out. Reading it as one handed the turn back to their attacker,
            // who then played another move into the empty slot.
            if (battleState.awaitsSwitchFrom(battleState.getActivePlayerId())) break;

            if (!isStunned(battleState.getPlayer())) break;
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

    private boolean isStunned(PlayerState player) {
        return player.getActiveCritter().getActiveStatusEffects().stream()
                .anyMatch(SkipTurnEffect.class::isInstance);
    }
}
