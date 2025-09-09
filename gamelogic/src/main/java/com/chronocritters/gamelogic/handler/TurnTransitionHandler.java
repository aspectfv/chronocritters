package com.chronocritters.gamelogic.handler;

import com.chronocritters.lib.model.ActiveEffect;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.EffectType;
import com.chronocritters.lib.model.PlayerState;
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
        Optional<ActiveEffect> skipTurnEffect = activeCritter.getActiveEffects().stream()
                .filter(effect -> effect.getType() == EffectType.SKIP_TURN)
                .findFirst();

        if (skipTurnEffect.isPresent()) {
            ActiveEffect stunEffect = skipTurnEffect.get();
            double chance = stunEffect.getCurrentChance() / 100.0;

            if (Math.random() < chance) {
                String skipLog = String.format("%s is stunned and unable to move!", activeCritter.getName());
                battleState.getActionLogHistory().add(skipLog);

                stunEffect.setRemainingDuration(stunEffect.getRemainingDuration() - 1);
                if (stunEffect.getRemainingDuration() <= 0) {
                    activeCritter.getActiveEffects().remove(stunEffect);
                    String effectEndLog = String.format("The %s on %s wore off.", stunEffect.getName(), activeCritter.getName());
                    battleState.getActionLogHistory().add(effectEndLog);
                }

                this.handle(battleState);
                return;
            } else {
                String resistLog = String.format("%s resisted the stun effect and can move this turn!", activeCritter.getName());
                battleState.getActionLogHistory().add(resistLog);
            }
        }
        
        battleState.setTimeRemaining(TURN_DURATION_SECONDS);
    }
}