package com.chronocritters.lib.model.effects;

import com.chronocritters.lib.interfaces.IInstantEffect;
import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.BattleStats;
import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.battle.PlayerState;
import com.chronocritters.lib.model.domain.Ability;
import com.chronocritters.lib.model.domain.Effect;
import com.chronocritters.lib.model.domain.TurnActionEntry;
import com.chronocritters.lib.util.TypeAdvantageUtil;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class DamageEffect extends Effect implements IInstantEffect {
    @Min(value = 0, message = "Damage cannot be negative")
    private int damage;

    public void apply(BattleState battleState) {
        PlayerState player = battleState.getPlayer();
        PlayerState opponent = battleState.getOpponent();
        CritterState caster = player.getActiveCritter();
        CritterState target = opponent.getActiveCritter();
        Ability ability = player.getActiveCritter().getAbilityById(player.getLastSelectedAbilityId());

        int atk = caster.getStats().getCurrentAtk();
        int def = target.getStats().getCurrentDef();
        double typeMultiplier = TypeAdvantageUtil.getMultiplier(caster.getType(), target.getType());

        double baseDamage = damage * Math.min(3.0, atk / (double) def);
        
        int finalDamage = (int) Math.round(baseDamage * typeMultiplier);

        finalDamage = Math.max(0, finalDamage);

        int newHealth = Math.max(0, target.getStats().getCurrentHp() - finalDamage);
        target.getStats().setCurrentHp(newHealth);

        BattleStats battleStats = battleState.getBattleStats();

        battleStats.getPlayersDamageDealt().put(player.getId(),
            battleStats.getPlayersDamageDealt().getOrDefault(player.getId(), 0) + finalDamage);

        String turnActionLog = String.format("%s used %s - %s damage",
            caster.getName(), ability.getName(), finalDamage);

        TurnActionEntry turnAction = TurnActionEntry.builder()
                .playerId(player.getId())
                .playerHasTurn(true)
                .turn(battleStats.getTurnCount())
                .turnActionLog(turnActionLog)
                .build();
        battleStats.getTurnActionHistory().add(turnAction);

        String actionLog = String.format("%s's %s used %s for %d damage! %s's %s now has %d health.",
            player.getUsername(), caster.getName(), ability.getName(), finalDamage,
            opponent.getUsername(), target.getName(), newHealth);

        if (typeMultiplier > 1) {
            actionLog += " It's super effective!";
        } else if (typeMultiplier < 1) {
            actionLog += " It's not very effective...";
        }

        battleState.getActionLogHistory().add(actionLog);
    }
}
