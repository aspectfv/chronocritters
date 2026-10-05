package com.chronocritters.lib.model.effects;

import com.chronocritters.lib.interfaces.effects.IInstantEffect;
import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.BattleStats;
import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.battle.PlayerState;
import com.chronocritters.lib.model.domain.Ability;
import com.chronocritters.lib.model.domain.Effect;
import com.chronocritters.lib.model.domain.TurnActionEntry;
import com.chronocritters.lib.model.enums.Stat;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Moves a stat by stages. Positive stages raise the caster's own stat and
 * negative stages lower the opposing critter's, so the sign says who it is for.
 * Stages hold until the critter leaves the field.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class StatModifierEffect extends Effect implements IInstantEffect {
    @NotNull(message = "Stat cannot be null")
    private Stat stat;

    private int stages;

    @Override
    public void apply(BattleState battleState) {
        PlayerState player = battleState.getPlayer();
        PlayerState opponent = battleState.getOpponent();
        CritterState caster = player.getActiveCritter();
        Ability ability = caster.getAbilityById(player.getLastSelectedAbilityId());

        boolean raises = stages > 0;
        PlayerState owner = raises ? player : opponent;
        CritterState affected = owner.getActiveCritter();
        String statName = stat.name().toLowerCase();

        int moved = affected.getStats().shiftStage(stat, stages);

        BattleStats battleStats = battleState.getBattleStats();

        String turnActionLog = String.format("%s used %s - %s %+d",
            caster.getName(), ability.getName(), statName, moved);

        TurnActionEntry turnAction = TurnActionEntry.builder()
                .playerId(player.getId())
                .playerHasTurn(true)
                .turn(battleStats.getTurnCount())
                .turnActionLog(turnActionLog)
                .build();
        battleStats.getTurnActionHistory().add(turnAction);

        String actionLog;
        if (moved == 0) {
            actionLog = String.format("%s's %s's %s can't go any %s!",
                owner.getUsername(), affected.getName(), statName, raises ? "higher" : "lower");
        } else {
            actionLog = String.format("%s's %s's %s %s by %d!",
                owner.getUsername(), affected.getName(), statName, raises ? "rose" : "fell", Math.abs(moved));
        }

        battleState.getActionLogHistory().add(actionLog);
    }
}
