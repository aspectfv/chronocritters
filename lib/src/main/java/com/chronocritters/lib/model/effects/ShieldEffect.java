package com.chronocritters.lib.model.effects;

import java.util.Optional;

import com.chronocritters.lib.interfaces.effects.IPersistentEffect;
import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.BattleStats;
import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.battle.PlayerState;
import com.chronocritters.lib.model.domain.Ability;
import com.chronocritters.lib.model.domain.Effect;
import com.chronocritters.lib.model.domain.TurnActionEntry;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * A ward on the caster that soaks damage before health is touched. It goes
 * when it is used up or when its turns run out, whichever comes first.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ShieldEffect extends Effect implements IPersistentEffect {
    @Min(value = 1, message = "Shield amount must be at least 1")
    private int amount;

    @Min(value = 1, message = "Duration must be at least 1 turn")
    private int duration;

    @Override
    public void onApply(BattleState battleState) {
        PlayerState player = battleState.getPlayer();
        CritterState caster = player.getActiveCritter();
        Ability ability = caster.getAbilityById(player.getLastSelectedAbilityId());

        Optional<ShieldEffect> existingEffect = caster.getActiveStatusEffects().stream()
            .filter(e -> e.getId().equals(this.getId()))
            .map(e -> (ShieldEffect) e)
            .findFirst();

        if (existingEffect.isPresent()) {
            existingEffect.get().setAmount(this.amount);
            existingEffect.get().setDuration(this.duration);
        } else {
            this.casterId = caster.getId();
            caster.getActiveStatusEffects().add(this.createInstance());
        }

        BattleStats battleStats = battleState.getBattleStats();

        String turnActionLog = String.format("%s used %s - warded for %d",
            caster.getName(), ability.getName(), amount);

        TurnActionEntry turnAction = TurnActionEntry.builder()
                .playerId(player.getId())
                .playerHasTurn(true)
                .turn(battleStats.getTurnCount())
                .turnActionLog(turnActionLog)
                .build();
        battleStats.getTurnActionHistory().add(turnAction);

        String actionLog = String.format("%s's %s is warded by %s against %d damage for %d turns!",
            player.getUsername(), caster.getName(), ability.getName(), this.amount, this.duration);

        battleState.getActionLogHistory().add(actionLog);
    }

    @Override
    public boolean onTick(BattleState battleState, CritterState target) {
        this.duration--;

        if (this.duration <= 0) {
            battleState.getActionLogHistory().add(String.format("The ward around %s fades.", target.getName()));
            return true;
        }

        battleState.getActionLogHistory().add(
            String.format("%s is warded against %d more damage. %d Turns remaining.", target.getName(), this.amount, this.duration));
        return false;
    }

    /** Soaks as much of the hit as is left in the ward. Returns how much it took. */
    public int absorb(int damage) {
        int absorbed = Math.min(this.amount, damage);
        this.amount -= absorbed;
        return absorbed;
    }

    private Effect createInstance() {
        return ShieldEffect.builder()
                .id(this.id)
                .amount(this.amount)
                .duration(this.duration)
                .casterId(this.casterId)
                .build();
    }
}
