package com.chronocritters.lib.model.battle;

import java.util.ArrayList;
import java.util.List;

import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.CritterType;
import com.chronocritters.lib.model.Effect;

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

    @Default
    private boolean fainted = false;

    public Ability getAbilityById(String abilityId) {
        return abilities.stream()
            .filter(ability -> ability.getId().equals(abilityId))
            .findFirst()
            .orElse(null);
    }
}
