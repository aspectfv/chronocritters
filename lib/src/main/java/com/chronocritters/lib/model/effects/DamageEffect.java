package com.chronocritters.lib.model.effects;

import com.chronocritters.lib.context.EffectContext;
import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.Effect;
import com.chronocritters.lib.model.PlayerState;
import com.chronocritters.lib.util.TypeAdvantageUtil;

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
public class DamageEffect extends Effect {
    private int damage;

    public void apply(EffectContext context) {
        BattleState battleState = context.getBattleState();
        PlayerState player = context.getPlayer();
        PlayerState opponent = context.getOpponent();
        CritterState caster = context.getCasterCritter();
        CritterState target = context.getTargetCritter();
        Ability ability = context.getSourceAbility();

        int atk = caster.getStats().getCurrentAtk();
        int def = target.getStats().getCurrentDef();
        double typeMultiplier = TypeAdvantageUtil.getMultiplier(caster.getType(), target.getType());

        double baseDamage = damage * Math.min(3.0, atk / (double) def);
        
        int finalDamage = (int) Math.round(baseDamage * typeMultiplier);

        finalDamage = Math.max(0, finalDamage);

        int newHealth = Math.max(0, target.getStats().getCurrentHp() - finalDamage);
        target.getStats().setCurrentHp(newHealth);

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
