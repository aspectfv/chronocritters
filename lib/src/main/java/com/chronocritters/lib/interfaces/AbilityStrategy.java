package com.chronocritters.lib.interfaces;

import com.chronocritters.lib.context.AbilityExecutionContext;

public interface AbilityStrategy {
    void executeAbility(AbilityExecutionContext context);
}