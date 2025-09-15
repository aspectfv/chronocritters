package com.chronocritters.lib.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerStats {
    @Default 
    private int wins = 0;

    @Default
    private int losses = 0;

    @Default
    private int level = 1;

    @Default
    private long experience = 0;
}
