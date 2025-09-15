package com.chronocritters.lib.model.domain;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseStats {
    @Min(value = 0, message = "Health must not be negative")
    private int health;

    @Min(value = 0, message = "Attack must not be negative")
    private int attack;

    @Min(value = 0, message = "Defense must not be negative")
    private int defense;

    @Default
    private int level = 1;

    @Default 
    private long experience = 0;
}
