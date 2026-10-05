package com.chronocritters.lib.model.battle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * What the last action did, in numbers rather than prose. The action log already
 * says it in words; a floating damage number and a hit flash need the figures
 * themselves, and which side was hit to know which card to animate.
 *
 * The side is named by player rather than by critter. Critter ids come from the
 * shared catalogue, so in a mirror match both actives carry the same one.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TurnResult {
    private int turn;
    private String targetPlayerId;
    private int damage;

    /** 1.0 normal, above for a type advantage, below for a resisted hit. */
    private double effectiveness;
}
