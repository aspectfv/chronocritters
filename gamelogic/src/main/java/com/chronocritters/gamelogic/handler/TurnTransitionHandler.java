package com.chronocritters.gamelogic.handler;

import com.chronocritters.lib.interfaces.EffectStrategy;
import com.chronocritters.lib.model.ActiveEffect;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.EffectType;
import com.chronocritters.lib.model.PlayerState;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class TurnTransitionHandler extends AbstractTurnActionHandler {
    private static final int TURN_DURATION_SECONDS = 30;
    private final Map<EffectType, EffectStrategy> effectStrategies;

    @Override
    public void handle(BattleState battleState) {
        PlayerState newCurrentPlayer = battleState.getOpponent();
        battleState.getPlayer().setHasTurn(false);
        newCurrentPlayer.setHasTurn(true);
        battleState.setActivePlayerId(newCurrentPlayer.getId());

        CritterState activeCritter = newCurrentPlayer.getActiveCritter();
        Optional<ActiveEffect> skipTurnEffect = activeCritter.getActiveEffects().stream()
                .filter(effect -> effect.getType() == EffectType.SKIP_TURN)
                .findFirst();

        if (skipTurnEffect.isPresent()) {
            double chance = skipTurnEffect.get().getPower() / 100.0;
            if (Math.random() < chance) {
                String skipLog = String.format("%s is skipped and unable to move!", activeCritter.getName());
                battleState.getActionLogHistory().add(skipLog);

                new EndOfTurnEffectsHandler(this.effectStrategies).handle(battleState);

                // recursive call to transition to the next turn
                this.handle(battleState);
                return;
            }
        }
        battleState.setTimeRemaining(TURN_DURATION_SECONDS);
    }
}