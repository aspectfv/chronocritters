package com.chronocritters.lib.model.battle;

import java.util.Map;

import com.chronocritters.lib.model.effects.StatusEffect;

/**
 * What a rewind puts back: a critter's health, status condition and cooldowns
 * as they stood at the start of a turn. Stat stages, wards and the rest of the
 * effect list are left out on purpose, so a rewind reads as undoing the damage
 * and the ailment rather than as a reload of the whole board.
 */
public record CritterSnapshot(int currentHp, StatusEffect status, Map<String, Integer> abilityCooldowns) {

    public static CritterSnapshot of(CritterState critter) {
        return new CritterSnapshot(
                critter.getStats().getCurrentHp(),
                critter.getStatus() == null ? null : critter.getStatus().copy(),
                Map.copyOf(critter.getAbilityCooldowns()));
    }
}
