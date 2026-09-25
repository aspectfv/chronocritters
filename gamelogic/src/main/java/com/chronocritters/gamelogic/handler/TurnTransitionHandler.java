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
        String owedBy = battleState.getAwaitingSwitchPlayerId();

        // The battle pauses on a replacement choice. Handing the turn on lets the
        // other player act, and their move resolves against the critter that has
        // already fainted: a stun landing on a corpse, damage credited to a
        // critter on its way off the field.
        if (owedBy != null) {
            giveTurnTo(battleState, battleState.getPlayerById(owedBy));
            battleState.setTimeRemaining(battleState.getTurnDuration());
            return;
        }

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

        giveTurnTo(battleState, nextPlayer);

        return true;
    }

    private void giveTurnTo(BattleState battleState, PlayerState player) {
        if (player == null) return;

        battleState.getPlayerOne().setHasTurn(player == battleState.getPlayerOne());
        battleState.getPlayerTwo().setHasTurn(player == battleState.getPlayerTwo());
        battleState.setActivePlayerId(player.getId());
    }

    private boolean isStunned(PlayerState player) {
        return player.getActiveCritter().getActiveStatusEffects().stream()
                .anyMatch(SkipTurnEffect.class::isInstance);
    }
}
