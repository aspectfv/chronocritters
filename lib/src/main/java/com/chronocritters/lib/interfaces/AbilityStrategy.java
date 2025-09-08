package com.chronocritters.lib.interfaces;

import com.chronocritters.lib.context.AbilityExecutionContext;
import com.chronocritters.lib.model.AbilityExecutionResult;

public interface AbilityStrategy {
    AbilityExecutionResult executeAbility(AbilityExecutionContext context);
}