package com.chronocritters.lib.model.effects;

import org.springframework.data.mongodb.core.mapping.Document;

import com.chronocritters.lib.context.EffectContext;
import com.chronocritters.lib.model.EffectType;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Document(collection = "effects")
public abstract class Effect {
    protected String id;
    protected EffectType type;

    public abstract ExecutionType getExecutionType();
    public abstract void apply(EffectContext context);
}
