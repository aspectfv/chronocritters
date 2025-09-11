package com.chronocritters.lib.model.effects;

import java.util.Optional;

import com.chronocritters.lib.context.EffectContext;
import com.chronocritters.lib.interfaces.PersistentEffect;
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
public class DamageOverTimeEffect extends Effect implements PersistentEffect {
    private int damagePerTurn;
    private int duration;

    @Override
    public void apply(EffectContext context) {
        BattleState battleState = context.getBattleState();
        PlayerState player = context.getPlayer();
        PlayerState opponent = context.getOpponent();
        CritterState caster = context.getCasterCritter();
        CritterState target = context.getTargetCritter();
        Ability ability = context.getSourceAbility();

        Optional<DamageOverTimeEffect> existingEffect = target.getActiveStatusEffects().stream()
            .filter(e -> e.getId().equals(this.getId()))
            .map(e -> (DamageOverTimeEffect) e)
            .findFirst();

        if (existingEffect.isPresent()) {
            existingEffect.get().setDuration(this.duration);
        } else {
            target.getActiveStatusEffects().add(this.createInstance());
        }

        String actionLog = String.format("%s's %s is afflicted with %s from %s's %s for %d turns, taking %d damage per turn!",
            opponent.getUsername(), target.getName(), ability.getName(), player.getUsername(), caster.getName(),
            this.duration, this.damagePerTurn);

        battleState.getActionLogHistory().add(actionLog);
    }

    @Override
    public boolean onTick(EffectContext context) {
        BattleState battleState = context.getBattleState();
        CritterState target = context.getTargetCritter();

        this.duration--;

        if (this.duration >= 0) {
            target.getStats().setCurrentHp(Math.max(0, target.getStats().getCurrentHp() - this.damagePerTurn));
            String actionLog = String.format("%s takes %d damage! %d Turns remaining.", target.getName(), this.damagePerTurn, this.duration);
            battleState.getActionLogHistory().add(actionLog);
            return false;
        } else {
            String actionLog = String.format("%s is no longer affected by damage over time.", target.getName());
            battleState.getActionLogHistory().add(actionLog);
            return true;
        }
    }

    private Effect createInstance() {
        return DamageOverTimeEffect.builder()
                .id(this.id)
                .type(this.type)
                .damagePerTurn(this.damagePerTurn)
                .duration(this.duration)
                .build();
    }
}
