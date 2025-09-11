package com.chronocritters.lib.model.effects;

import com.chronocritters.lib.context.EffectContext;
import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.Effect;
import com.chronocritters.lib.model.PlayerState;

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

        int baseDamage = (int) Math.max(0, damage * (caster.getStats().getCurrentAtk() / (double)(caster.getStats().getCurrentDef() + target.getStats().getCurrentDef())));
        int finalDamage = (int) Math.max(1, baseDamage  /** typeMultiplier */);

        int newHealth = Math.max(0, target.getStats().getCurrentHp() - finalDamage);
        target.getStats().setCurrentHp(newHealth);

        String actionLog = String.format("%s's %s used %s for %d damage! %s's %s now has %d health.",
            player.getUsername(), caster.getName(), ability.getName(), finalDamage,
            opponent.getUsername(), target.getName(), newHealth);

        battleState.getActionLogHistory().add(actionLog);
    }
}
