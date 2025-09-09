package com.chronocritters.gamelogic.strategies.effects;

import org.springframework.stereotype.Component;

import com.chronocritters.lib.context.AbilityExecutionContext;
import com.chronocritters.lib.interfaces.EffectStrategy;
import com.chronocritters.lib.model.Effect;
import com.chronocritters.lib.model.EffectType;

@Component
public class PoisonEffectStrategy implements EffectStrategy {

    @Override
    public EffectType getEffectType() {
        return EffectType.POISON;
    }

    @Override
    public void applyEffect(AbilityExecutionContext context, Effect effect) {
        // Implement poison effect logic here
    }
}
