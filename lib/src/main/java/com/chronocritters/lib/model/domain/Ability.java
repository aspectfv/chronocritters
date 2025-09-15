package com.chronocritters.lib.model.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.chronocritters.lib.interfaces.effects.IInstantEffect;
import com.chronocritters.lib.interfaces.effects.IPersistentEffect;
import com.chronocritters.lib.model.battle.BattleState;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "abilities")
public class Ability {
    @NotBlank(message = "Ability ID cannot be blank")
    private String id;

    @NotBlank(message = "Ability name cannot be blank")
    private String name;

    @NotBlank(message = "Ability description cannot be blank")
    private String description;

    @NotNull(message = "Effects list cannot be null")
    private List<Effect> effects;

    public void execute(BattleState battleState) {
        for (Effect effect : effects) {
            if (effect instanceof IInstantEffect instantEffect) {
                instantEffect.apply(battleState);
            }
            if (effect instanceof IPersistentEffect persistentEffect) {
                persistentEffect.onApply(battleState);
            }
        }
    }
}