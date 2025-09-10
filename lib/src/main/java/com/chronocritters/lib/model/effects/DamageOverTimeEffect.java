package com.chronocritters.lib.model.effects;

import java.util.Optional;

import com.chronocritters.lib.context.EffectContext;
import com.chronocritters.lib.context.EffectContextType;
import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.PlayerState;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class DamageOverTimeEffect extends Effect {
    private int damagePerTurn;
    private int duration;

    @Override
    public ExecutionType getExecutionType() {
        return ExecutionType.PERSISTENT;
    }

    @Override
    public void apply(EffectContext context) {
        BattleState battleState = (BattleState) context.getData().get(EffectContextType.BATTLE_STATE);
        if (battleState == null) throw new IllegalArgumentException("BattleState not found in context");  

        CritterState target = (CritterState) context.getData().get(com.chronocritters.lib.context.EffectContextType.TARGET_CRITTER);
        if (target == null) throw new IllegalArgumentException("Target critter not found in context");
        
        Ability ability = (Ability) context.getData().get(com.chronocritters.lib.context.EffectContextType.ABILITY);

        if (ability != null) {
            handleApplication(context, battleState, ability, target);
        } else {
            handleTurnTick(context, battleState, target);
        }

    }

    private void handleApplication(EffectContext context, BattleState battleState, Ability ability, CritterState target) {
        PlayerState player = (PlayerState) context.getData().get(EffectContextType.PLAYER);
        if (player == null) throw new IllegalArgumentException("Player not found in context");

        PlayerState opponent = (PlayerState) context.getData().get(EffectContextType.OPPONENT);
        if (opponent == null) throw new IllegalArgumentException("Opponent not found in context");

        CritterState caster = (CritterState) context.getData().get(EffectContextType.CASTER_CRITTER);
        if (caster == null) throw new IllegalArgumentException("Caster critter not found in context");

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

    private void handleTurnTick(EffectContext context, BattleState battleState, CritterState target) {
        target.getStats().setCurrentHp(Math.max(0, target.getStats().getCurrentHp() - this.damagePerTurn));
        battleState.getActionLogHistory().add(String.format("%s takes %d damage!", target.getName(), this.damagePerTurn));
        this.duration--;
    }

    public Effect createInstance() {
        return DamageOverTimeEffect.builder()
                .id(this.id)
                .type(this.type)
                .damagePerTurn(this.damagePerTurn)
                .duration(this.duration)
                .build();
    }
}
