package com.chronocritters.lib.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrentStats {
    private int maxHp;
    private int currentHp;
    private int currentAtk;
    private int currentDef;
}
