package com.chronocritters.lib.model.effects;

import com.chronocritters.lib.interfaces.effects.IInstantEffect;
import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.BattleStats;
import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.battle.PlayerState;
import com.chronocritters.lib.model.domain.Ability;
import com.chronocritters.lib.model.domain.Effect;
import com.chronocritters.lib.model.domain.TurnActionEntry;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/** Restores a share of the caster's maximum health. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class HealEffect extends Effect implements IInstantEffect {
    @Min(value = 1, message = "Heal percent must be at least 1")
    @Max(value = 100, message = "Heal percent cannot exceed 100")
    private int healPercent;

    @Override
    public void apply(BattleState battleState) {
        PlayerState player = battleState.getPlayer();
        CritterState caster = player.getActiveCritter();
        Ability ability = caster.getAbilityById(player.getLastSelectedAbilityId());

        int healed = caster.heal((int) Math.round(caster.getStats().getMaxHp() * healPercent / 100.0));

        BattleStats battleStats = battleState.getBattleStats();

        String turnActionLog = String.format("%s used %s - %s healed",
            caster.getName(), ability.getName(), healed);

        TurnActionEntry turnAction = TurnActionEntry.builder()
                .playerId(player.getId())
                .playerHasTurn(true)
                .turn(battleStats.getTurnCount())
                .turnActionLog(turnActionLog)
                .build();
        battleStats.getTurnActionHistory().add(turnAction);

        String actionLog = String.format("%s's %s used %s and recovered %d health! %s now has %d health.",
            player.getUsername(), caster.getName(), ability.getName(), healed,
            caster.getName(), caster.getStats().getCurrentHp());

        battleState.getActionLogHistory().add(actionLog);
    }
}
