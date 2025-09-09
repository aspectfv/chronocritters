package com.chronocritters.gamelogic.handler;

import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.PlayerState;

public class TurnTransitionHandler extends AbstractTurnActionHandler {
    private static final int TURN_DURATION_SECONDS = 30;

    @Override
    public void handle(BattleState battleState) {
        PlayerState currentPlayer = battleState.getPlayer();
        PlayerState opponent = battleState.getOpponent();

        currentPlayer.setHasTurn(false);
        opponent.setHasTurn(true);
        battleState.setActivePlayerId(opponent.getId());
        battleState.setTimeRemaining(TURN_DURATION_SECONDS);
    }
}