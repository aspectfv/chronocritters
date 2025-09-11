package com.chronocritters.lib.model;

import org.springframework.data.mongodb.core.mapping.Document;

import com.chronocritters.lib.context.EffectContext;
import com.chronocritters.lib.model.effects.DamageEffect;
import com.chronocritters.lib.model.effects.DamageOverTimeEffect;
import com.chronocritters.lib.model.effects.SkipTurnEffect;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Document(collection = "effects")
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME, 
  include = JsonTypeInfo.As.PROPERTY, 
  property = "type"
)
@JsonSubTypes({ 
  @JsonSubTypes.Type(value = DamageEffect.class, name = "DAMAGE"), 
  @JsonSubTypes.Type(value = DamageOverTimeEffect.class, name = "DAMAGE_OVER_TIME"),
  @JsonSubTypes.Type(value = SkipTurnEffect.class, name = "SKIP_TURN")
})
public abstract class Effect {
    protected String id;
    protected EffectType type;

    public abstract void apply(EffectContext context);
}
