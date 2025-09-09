package com.chronocritters.lib.interfaces;

import com.chronocritters.lib.context.ExecuteAbilityContext;
import com.chronocritters.lib.model.BattleOutcome;
import com.chronocritters.lib.model.AbilityType;

public interface AbilityStrategy {
    AbilityType getAbilityType();
    BattleOutcome executeAbility(ExecuteAbilityContext context);
}