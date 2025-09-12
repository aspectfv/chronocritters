package com.chronocritters.gamelogic.handler;

import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.PlayerState;
import com.chronocritters.lib.model.effects.SkipTurnEffect;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class TurnTransitionHandler extends AbstractTurnActionHandler {
    private static final int TURN_DURATION_SECONDS = 30;

    @Override
    public void handle(BattleState battleState) {
        PlayerState currentPlayer = battleState.getPlayer();
        PlayerState nextPlayer = battleState.getOpponent();

        if (currentPlayer == null || nextPlayer == null) return;

        currentPlayer.setHasTurn(false);
        nextPlayer.setHasTurn(true);
        battleState.setActivePlayerId(nextPlayer.getId());
        
        boolean shouldSkipTurn = nextPlayer.getActiveCritter().getActiveStatusEffects().stream()
                .anyMatch(effect -> effect instanceof SkipTurnEffect);

        if (shouldSkipTurn) {
            this.handle(battleState);
            return;
        }

        battleState.setTimeRemaining(TURN_DURATION_SECONDS);
    }
}