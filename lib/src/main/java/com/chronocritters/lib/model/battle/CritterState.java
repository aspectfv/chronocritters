package com.chronocritters.lib.model.battle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chronocritters.lib.model.domain.Ability;
import com.chronocritters.lib.model.domain.Effect;
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

    public Ability getAbilityById(String abilityId) {
        return abilities.stream()
            .filter(ability -> ability.getId().equals(abilityId))
            .findFirst()
            .orElse(null);
    }
}
