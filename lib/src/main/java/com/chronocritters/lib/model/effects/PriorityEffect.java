package com.chronocritters.lib.model.effects;

import com.chronocritters.lib.interfaces.effects.IInstantEffect;
import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.BattleStats;
import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.battle.PlayerState;
import com.chronocritters.lib.model.domain.Ability;
import com.chronocritters.lib.model.domain.Effect;
import com.chronocritters.lib.model.domain.TurnActionEntry;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Puts the caster first in the next round, whatever its speed. A player picks
 * a move when their turn comes up, so a move cannot jump the round it is
 * picked in; it jumps the one after. For a critter that was already faster
 * this changes nothing, and for a slower one it means acting twice in a row.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class PriorityEffect extends Effect implements IInstantEffect {

    @Override
    public void apply(BattleState battleState) {
        PlayerState player = battleState.getPlayer();
        CritterState caster = player.getActiveCritter();
        Ability ability = caster.getAbilityById(player.getLastSelectedAbilityId());

        caster.setPriority(true);

        BattleStats battleStats = battleState.getBattleStats();

        TurnActionEntry turnAction = TurnActionEntry.builder()
                .playerId(player.getId())
                .playerHasTurn(true)
                .turn(battleStats.getTurnCount())
                .turnActionLog(String.format("%s used %s - moves first next round", caster.getName(), ability.getName()))
                .build();
        battleStats.getTurnActionHistory().add(turnAction);

        battleState.getActionLogHistory().add(String.format("%s's %s seizes the initiative and will move first next round!",
            player.getUsername(), caster.getName()));
    }
}
