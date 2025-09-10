package com.chronocritters.lib.model.effects;

import org.springframework.data.mongodb.core.mapping.Document;

import com.chronocritters.lib.context.EffectContext;
import com.chronocritters.lib.context.EffectContextType;
import com.chronocritters.lib.model.CritterState;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Document(collection = "effects")
public class DamageEffect extends Effect {
    private int damage;

    public void apply(EffectContext context) {
        CritterState caster = (CritterState) context.getData().get(EffectContextType.CASTER_CRITTER);
        if (caster == null) throw new IllegalArgumentException("Caster critter not found in context");
        CritterState target = (CritterState) context.getData().get(EffectContextType.TARGET_CRITTER);
        if (target == null) throw new IllegalArgumentException("Target critter not found in context");

        int baseDamage = (int) Math.max(0, damage * (caster.getStats().getCurrentAtk() / (double)(caster.getStats().getCurrentDef() + target.getStats().getCurrentDef())));
    }
}
