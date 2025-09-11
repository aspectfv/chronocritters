package com.chronocritters.lib.model.effects;

import com.chronocritters.lib.context.EffectContext;
import com.chronocritters.lib.context.EffectContextType;
import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.Effect;
import com.chronocritters.lib.model.ExecutionType;
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

    @Override
    public ExecutionType getExecutionType() {
        return ExecutionType.INSTANT;
    }

    public void apply(EffectContext context) {
        BattleState battleState = (BattleState) context.getData().get(EffectContextType.BATTLE_STATE);
        if (battleState == null) throw new IllegalArgumentException("BattleState not found in context");

        PlayerState player = (PlayerState) context.getData().get(EffectContextType.PLAYER);
        if (player == null) throw new IllegalArgumentException("Player not found in context");

        PlayerState opponent = (PlayerState) context.getData().get(EffectContextType.OPPONENT);
        if (opponent == null) throw new IllegalArgumentException("Opponent not found in context");

        CritterState caster = (CritterState) context.getData().get(EffectContextType.CASTER_CRITTER);
        if (caster == null) throw new IllegalArgumentException("Caster critter not found in context");

        CritterState target = (CritterState) context.getData().get(EffectContextType.TARGET_CRITTER);
        if (target == null) throw new IllegalArgumentException("Target critter not found in context");

        Ability ability = (Ability) context.getData().get(EffectContextType.ABILITY);
        if (ability == null) throw new IllegalArgumentException("Ability not found in context");

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
