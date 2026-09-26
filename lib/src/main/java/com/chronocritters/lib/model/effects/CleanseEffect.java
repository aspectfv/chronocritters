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
 * Clears every ailment on the caster. A ward is the caster's own doing and is
 * the one thing left standing.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class CleanseEffect extends Effect implements IInstantEffect {

    @Override
    public void apply(BattleState battleState) {
        PlayerState player = battleState.getPlayer();
        CritterState caster = player.getActiveCritter();
        Ability ability = caster.getAbilityById(player.getLastSelectedAbilityId());

        boolean cleared = caster.getActiveStatusEffects().removeIf(effect -> !(effect instanceof ShieldEffect));

        BattleStats battleStats = battleState.getBattleStats();

        String turnActionLog = String.format("%s used %s - %s",
            caster.getName(), ability.getName(), cleared ? "cleansed" : "nothing to cleanse");

        TurnActionEntry turnAction = TurnActionEntry.builder()
                .playerId(player.getId())
                .playerHasTurn(true)
                .turn(battleStats.getTurnCount())
                .turnActionLog(turnActionLog)
                .build();
        battleStats.getTurnActionHistory().add(turnAction);

        String actionLog = cleared
            ? String.format("%s's %s used %s and shook off every ailment!", player.getUsername(), caster.getName(), ability.getName())
            : String.format("%s's %s used %s, but there was nothing to shake off.", player.getUsername(), caster.getName(), ability.getName());

        battleState.getActionLogHistory().add(actionLog);
    }
}
