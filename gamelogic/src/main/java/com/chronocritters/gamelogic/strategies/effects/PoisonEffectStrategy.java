package com.chronocritters.gamelogic.strategies.effects;

import org.springframework.stereotype.Component;

import com.chronocritters.lib.context.AbilityExecutionContext;
import com.chronocritters.lib.interfaces.EffectStrategy;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.Effect;
import com.chronocritters.lib.model.EffectType;
import com.chronocritters.lib.model.PlayerState;

@Component
public class PoisonEffectStrategy implements EffectStrategy {

    @Override
    public EffectType getEffectType() {
        return EffectType.POISON;
    }

    @Override
    public void applyEffect(AbilityExecutionContext context, Effect effect) {
        PlayerState opponent = context.getOpponent();
        CritterState targetCritter = opponent.getCritterByIndex(opponent.getActiveCritterIndex());

        int poisonDamage = effect.getPower();
        int newHp = Math.max(0, targetCritter.getStats().getCurrentHp() - poisonDamage);
        targetCritter.getStats().setCurrentHp(newHp);

        targetCritter.getActiveEffects().stream()
            .filter(activeEffect -> activeEffect.getId().equals(effect.getId()))
            .forEach(activeEffect -> activeEffect.setRemainingDuration(activeEffect.getRemainingDuration() - 1));

        context.getBattleState().getActionLogHistory().add(
            String.format("%s takes %d poison damage! (%d HP left)", targetCritter.getName(), poisonDamage, newHp)
        );
    }
}
