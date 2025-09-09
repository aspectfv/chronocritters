package com.chronocritters.lib.interfaces;

import com.chronocritters.lib.context.AbilityExecutionContext;
import com.chronocritters.lib.model.Effect;
import com.chronocritters.lib.model.EffectType;

public interface EffectStrategy {
    EffectType getEffectType();
    void applyEffect(AbilityExecutionContext context, Effect effect);
}
