package com.chronocritters.lib.interfaces;

import com.chronocritters.lib.context.ApplyEffectContext;
import com.chronocritters.lib.model.BattleOutcome;
import com.chronocritters.lib.model.EffectType;

public interface EffectStrategy {
    EffectType getEffectType();
    BattleOutcome applyActiveEffect(ApplyEffectContext context);
}
