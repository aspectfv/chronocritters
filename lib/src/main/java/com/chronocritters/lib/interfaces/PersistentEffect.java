package com.chronocritters.lib.interfaces;

import com.chronocritters.lib.context.EffectContext;
import com.chronocritters.lib.model.CritterState;

public interface PersistentEffect {
    boolean onTick(EffectContext context, CritterState target);
}
