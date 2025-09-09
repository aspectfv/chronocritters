package com.chronocritters.gamelogic.abilities;

import com.chronocritters.lib.context.AbilityExecutionContext;
import com.chronocritters.lib.interfaces.AbilityStrategy;
import com.chronocritters.lib.model.AbilityExecutionResult;
import com.chronocritters.lib.model.AbilityType;

public class EffectAbilityStrategy implements AbilityStrategy {
    @Override
    public AbilityType getAbilityType() {
        return AbilityType.EFFECT;
    }

    @Override
    public AbilityExecutionResult executeAbility(AbilityExecutionContext context) {
        return AbilityExecutionResult.CONTINUE;
    }
}
