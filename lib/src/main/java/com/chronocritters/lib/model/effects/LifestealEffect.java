package com.chronocritters.lib.model.effects;

import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.CritterState;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * A hit that gives part of what it took back to the caster. It is a damage
 * effect first, so the drain is a share of the damage that reached health,
 * after type and after any ward.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class LifestealEffect extends DamageEffect {
    @Min(value = 1, message = "Drain percent must be at least 1")
    @Max(value = 100, message = "Drain percent cannot exceed 100")
    private int drainPercent;

    @Override
    public void apply(BattleState battleState) {
        super.apply(battleState);

        CritterState caster = battleState.getPlayer().getActiveCritter();
        int dealt = battleState.getLastTurnResult().getDamage();
        int healed = caster.heal((int) Math.round(dealt * drainPercent / 100.0));

        battleState.getActionLogHistory().add(String.format("%s drains %d health from the blow! %s now has %d health.",
            caster.getName(), healed, caster.getName(), caster.getStats().getCurrentHp()));
    }
}
