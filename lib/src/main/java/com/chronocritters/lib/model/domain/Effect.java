package com.chronocritters.lib.model.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.chronocritters.lib.model.effects.CleanseEffect;
import com.chronocritters.lib.model.effects.DamageEffect;
import com.chronocritters.lib.model.effects.DamageOverTimeEffect;
import com.chronocritters.lib.model.effects.HealEffect;
import com.chronocritters.lib.model.effects.LifestealEffect;
import com.chronocritters.lib.model.effects.ShieldEffect;
import com.chronocritters.lib.model.effects.SkipTurnEffect;
import com.chronocritters.lib.model.effects.StatModifierEffect;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Document(collection = "effects")
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "_type"
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = DamageEffect.class, name = "DamageEffect"),
  @JsonSubTypes.Type(value = DamageOverTimeEffect.class, name = "DamageOverTimeEffect"),
  @JsonSubTypes.Type(value = SkipTurnEffect.class, name = "SkipTurnEffect"),
  @JsonSubTypes.Type(value = StatModifierEffect.class, name = "StatModifierEffect"),
  @JsonSubTypes.Type(value = HealEffect.class, name = "HealEffect"),
  @JsonSubTypes.Type(value = LifestealEffect.class, name = "LifestealEffect"),
  @JsonSubTypes.Type(value = ShieldEffect.class, name = "ShieldEffect"),
  @JsonSubTypes.Type(value = CleanseEffect.class, name = "CleanseEffect"),
})
public abstract class Effect {
    @NotBlank(message = "Effect ID cannot be blank")
    protected String id;
    
    @NotBlank(message = "Effect description cannot be blank")
    protected String description;

    protected String casterId;
}