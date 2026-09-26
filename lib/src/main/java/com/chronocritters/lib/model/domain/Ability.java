package com.chronocritters.lib.model.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.chronocritters.lib.interfaces.effects.IInstantEffect;
import com.chronocritters.lib.interfaces.effects.IPersistentEffect;
import com.chronocritters.lib.model.battle.BattleState;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    /**
     * How long the ability is unavailable after it is used. The count ticks down
     * when the turn arrives back at its owner, so it has already lost one by the
     * time they could use it again: a stored 3 costs them two of their own turns.
     * Zero means the ability is always available.
     */
    @Min(value = 0, message = "Cooldown must not be negative")
    private int cooldownTurns;

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