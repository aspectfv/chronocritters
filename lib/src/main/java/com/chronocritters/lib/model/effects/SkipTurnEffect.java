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
public class SkipTurnEffect extends Effect implements PersistentEffect {
    private int duration;

    @Override
    public void apply(EffectContext context) {
        BattleState battleState = context.getBattleState();
        PlayerState player = context.getPlayer();
        CritterState target = context.getTargetCritter();
        Ability ability = context.getSourceAbility();

        Optional<SkipTurnEffect> existingEffect = target.getActiveStatusEffects().stream()
            .filter(e -> e.getId().equals(this.getId()))
            .map(e -> (SkipTurnEffect) e)
            .findFirst();

        if (existingEffect.isPresent()) {
            existingEffect.get().setDuration(this.duration);
        } else {
            target.getActiveStatusEffects().add(this.createInstance());
        }

        String actionLog = String.format("%s's %s is afflicted with %s for %d turns!",
            player.getUsername(), target.getName(), ability.getName(), this.duration);

        battleState.getActionLogHistory().add(actionLog);
    }

    @Override
    public boolean onTick(EffectContext context) {
        BattleState battleState = context.getBattleState();
        CritterState target = context.getTargetCritter();
        
        this.duration--;

        if (this.duration >= 0) {
            String actionLog = String.format("%s is stunned and unable to move!", target.getName());
            battleState.getActionLogHistory().add(actionLog);
            return false;
        } else {
            String actionLog = String.format("The stun effect on %s wore off.", target.getName());
            battleState.getActionLogHistory().add(actionLog);
            return true;
        }
    }

    private Effect createInstance() {
        return SkipTurnEffect.builder()
                .id(this.id)
                .type(this.type)
                .duration(this.duration)
                .build();
    }
}
