package com.chronocritters.lib.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActiveEffect {
    private String id;
    private String name;
    private EffectType type;
    private int power;
    private int remainingDuration; // in turns
}
