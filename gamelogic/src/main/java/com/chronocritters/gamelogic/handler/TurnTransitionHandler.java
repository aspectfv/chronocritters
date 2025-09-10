package com.chronocritters.gamelogic.handler;

import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.EffectType;
import com.chronocritters.lib.model.PlayerState;
import com.chronocritters.lib.model.effects.Effect;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class TurnTransitionHandler extends AbstractTurnActionHandler {
    private static final int TURN_DURATION_SECONDS = 30;

    @Override
    public void handle(BattleState battleState) {
        PlayerState newCurrentPlayer = battleState.getOpponent();
        battleState.getPlayer().setHasTurn(false);
        newCurrentPlayer.setHasTurn(true);
        battleState.setActivePlayerId(newCurrentPlayer.getId());

        CritterState activeCritter = newCurrentPlayer.getActiveCritter();
        
        Optional<Effect> skipTurnEffect = activeCritter.getActiveStatusEffects().stream()
                .filter(effect -> effect.getType() == EffectType.SKIP_TURN)
                .findFirst();

        if (skipTurnEffect.isPresent()) {
            String skipLog = String.format("%s is stunned and unable to move!", activeCritter.getName());
            battleState.getActionLogHistory().add(skipLog);

            this.handle(battleState);
            return;
        }

        battleState.setTimeRemaining(TURN_DURATION_SECONDS);
    }
}