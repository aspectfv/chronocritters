package com.chronocritters.lib.model;

import org.springframework.data.mongodb.core.mapping.Document;

import com.chronocritters.lib.context.EffectContext;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Document(collection = "effects")
public abstract class Effect {
    protected String id;
    protected EffectType type;

    public abstract ExecutionType getExecutionType();
    public abstract void apply(EffectContext context);
}
