package com.chronocritters.gamelogic.strategies.effects;

import org.springframework.stereotype.Component;

import com.chronocritters.lib.context.ApplyEffectContext;
import com.chronocritters.lib.interfaces.EffectStrategy;
import com.chronocritters.lib.model.ActiveEffect;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.EffectType;

@Component
public class DamageOverTimeEffectStrategy implements EffectStrategy {

    @Override
    public EffectType getEffectType() {
        return EffectType.DAMAGE_OVER_TIME;
    }

    @Override
    public void applyActiveEffect(ApplyEffectContext context) {
        ActiveEffect effect = context.getEffect();
        CritterState targetCritter = context.getTargetCritter();

        int damage = effect.getPower();
        targetCritter.getStats().setCurrentHp(Math.max(0, targetCritter.getStats().getCurrentHp() - damage));

        String logEntry = String.format("%s took %d damage from %s effect.",
                targetCritter.getName(), damage, effect.getType().name());
        context.getBattleState().getActionLogHistory().add(logEntry);
    }
}