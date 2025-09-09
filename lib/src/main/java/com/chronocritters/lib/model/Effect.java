package com.chronocritters.lib.model;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "effects")
public class Effect {
    private String id;
    private String name;
    private EffectType type;
    private int power;
    private int duration; // in turns
    private int chance; // percent chance of application

    public ActiveEffect toActiveEffect() {
        return ActiveEffect.builder()
            .id(this.id)
            .name(this.name)
            .type(this.type)
            .power(this.power)
            .remainingDuration(this.duration)
            .currentChance(this.chance)
            .build();
    }
}
