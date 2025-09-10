package com.chronocritters.lib.model.effects;

import com.chronocritters.lib.context.EffectContext;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class DamageOverTimeEffect extends Effect {
    private int damagePerTurn;
    private int duration;

    @Override
    public void apply(EffectContext context) {
    }
}
