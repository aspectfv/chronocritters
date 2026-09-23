package com.chronocritters.lib.model.battle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * What the last action did, in numbers rather than prose. The action log already
 * says it in words; a floating damage number and a hit flash need the figures
 * themselves, and the critter ids to know which card to animate.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TurnResult {
    private int turn;
    private String casterCritterId;
    private String targetCritterId;
    private int damage;

    /** 1.0 normal, above for a type advantage, below for a resisted hit. */
    private double effectiveness;
}
