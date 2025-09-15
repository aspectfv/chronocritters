package com.chronocritters.lib.model.effects;

import java.util.Optional;

import com.chronocritters.lib.interfaces.IPersistentEffect;
import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.Effect;
import com.chronocritters.lib.model.PlayerState;

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
public class DamageOverTimeEffect extends Effect implements IPersistentEffect {
    @Min(value = 0, message = "Damage per turn must not be negative")
    private int damagePerTurn;

    @Min(value = 1, message = "Duration must be at least 1 turn")
    private int duration;

    @Override
    public void onApply(BattleState battleState) {
        PlayerState player = battleState.getPlayer();
        PlayerState opponent = battleState.getOpponent();
        CritterState caster = player.getActiveCritter();
        CritterState target = opponent.getActiveCritter();
        Ability ability = caster.getAbilityById(player.getLastSelectedAbilityId());

        Optional<DamageOverTimeEffect> existingEffect = target.getActiveStatusEffects().stream()
            .filter(e -> e.getId().equals(this.getId()))
            .map(e -> (DamageOverTimeEffect) e)
            .findFirst();

        if (existingEffect.isPresent()) {
            existingEffect.get().setDuration(this.duration);
        } else {
            this.casterId = caster.getId();
            target.getActiveStatusEffects().add(this.createInstance());
        }

        String actionLog = String.format("%s's %s is afflicted with %s from %s's %s for %d turns, taking %d damage per turn!",
            opponent.getUsername(), target.getName(), ability.getName(), player.getUsername(), caster.getName(),
            this.duration, this.damagePerTurn);

        battleState.getActionLogHistory().add(actionLog);
    }

    @Override
    public boolean onTick(BattleState battleState, CritterState target) {
        this.duration--;

        if (this.duration >= 0) {
            target.getStats().setCurrentHp(Math.max(0, target.getStats().getCurrentHp() - this.damagePerTurn));
            battleState.getPlayersDamageDealt().put(this.casterId, 
                battleState.getPlayersDamageDealt().getOrDefault(this.casterId, 0) + this.damagePerTurn);
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
                .damagePerTurn(this.damagePerTurn)
                .duration(this.duration)
                .casterId(this.casterId)
                .build();
    }
}
