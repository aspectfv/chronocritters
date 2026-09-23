package com.chronocritters.gamelogic.handler;

import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.BattleStats;
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

        currentPlayer.setHasTurn(false);
        nextPlayer.setHasTurn(true);
        battleState.setActivePlayerId(nextPlayer.getId());

        return true;
    }

    private boolean isStunned(PlayerState player) {
        return player.getActiveCritter().getActiveStatusEffects().stream()
                .anyMatch(SkipTurnEffect.class::isInstance);
    }
}
