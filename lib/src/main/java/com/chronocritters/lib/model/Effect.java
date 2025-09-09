package com.chronocritters.lib.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Effect {
    private String id;
    private String name;
    private EffectType type;
    private int power;
}
