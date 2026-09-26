package com.chronocritters.lib.model.battle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.chronocritters.lib.model.domain.Ability;
import com.chronocritters.lib.model.domain.Effect;
import com.chronocritters.lib.model.effects.ShieldEffect;
import com.chronocritters.lib.model.enums.CritterType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CritterState {
    private String id;
    private String name;
    private CritterType type;
    private CurrentStats stats;
    private List<Ability> abilities;

    @Default
    private List<Effect> activeStatusEffects = new ArrayList<>();

    /**
     * Turns left on each ability that is cooling down, by ability id. An ability
     * that is ready is absent rather than held at zero.
     */
    @Default
    private Map<String, Integer> abilityCooldowns = new HashMap<>();

    @Default
    private boolean fainted = false;

    public boolean isCoolingDown(String abilityId) {
        return abilityCooldowns.containsKey(abilityId);
    }

    /**
     * Takes a hit through any ward first, dropping a ward it empties. Returns
     * the damage that reached health.
     */
    public int takeDamage(int damage) {
        int remaining = damage;
        Iterator<Effect> iterator = activeStatusEffects.iterator();
        while (iterator.hasNext() && remaining > 0) {
            if (iterator.next() instanceof ShieldEffect shield) {
                remaining -= shield.absorb(remaining);
                if (shield.getAmount() == 0) iterator.remove();
            }
        }
        stats.setCurrentHp(Math.max(0, stats.getCurrentHp() - remaining));
        return remaining;
    }

    /** Restores health up to the maximum. Returns how much was actually restored. */
    public int heal(int amount) {
        int healed = Math.min(amount, stats.getMaxHp() - stats.getCurrentHp());
        stats.setCurrentHp(stats.getCurrentHp() + healed);
        return healed;
    }

    public Ability getAbilityById(String abilityId) {
        return abilities.stream()
            .filter(ability -> ability.getId().equals(abilityId))
            .findFirst()
            .orElse(null);
    }
}
