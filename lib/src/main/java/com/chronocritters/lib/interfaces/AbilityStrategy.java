package com.chronocritters.lib.interfaces;

import com.chronocritters.lib.context.AbilityExecutionContext;
import com.chronocritters.lib.model.AbilityExecutionResult;
import com.chronocritters.lib.model.AbilityType;

public interface AbilityStrategy {
    AbilityType getAbilityType();
    AbilityExecutionResult executeAbility(AbilityExecutionContext context);
}