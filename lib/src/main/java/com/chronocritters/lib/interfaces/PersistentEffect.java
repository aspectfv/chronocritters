package com.chronocritters.lib.interfaces;

import com.chronocritters.lib.context.EffectContext;

public interface PersistentEffect {
    boolean onTick(EffectContext context);
}
