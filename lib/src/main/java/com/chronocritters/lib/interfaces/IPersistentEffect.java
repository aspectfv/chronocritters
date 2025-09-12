package com.chronocritters.lib.interfaces;

import com.chronocritters.lib.context.EffectContext;

public interface IPersistentEffect {
    boolean onTick(EffectContext context);
}
