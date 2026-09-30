package com.chronocritters.lib.model.effects;

import com.chronocritters.lib.interfaces.effects.IPersistentEffect;
import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.BattleStats;
import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.battle.PlayerState;
import com.chronocritters.lib.model.domain.Ability;
import com.chronocritters.lib.model.domain.Effect;
import com.chronocritters.lib.model.domain.TurnActionEntry;
import com.chronocritters.lib.model.enums.StatusCondition;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Puts a status condition on the opposing critter. A critter holds one status
 * at a time, in its own slot rather than in the effect list, so a second one
 * fails instead of stacking. One that has just ended cannot land again until
 * the next turn's effects have ticked.
 *
 * Burn, poison and paralysis run on the same countdown as every other
 * persistent effect, which ticks on both players' turns. Sleep does not tick:
 * its duration is the number of its owner's turns it skips, spent one at a
 * time as each of those turns is passed over.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class StatusEffect extends Effect implements IPersistentEffect {
    @NotNull(message = "Condition cannot be null")
    private StatusCondition condition;

    /** Ticks it lasts, or for sleep, the owner's turns it skips. */
    @Min(value = 1, message = "Duration must be at least 1 turn")
    private int duration;

    /**
     * Damage on each tick of a burn. Poison deals it on the first tick and
     * this much more on every tick after. Paralysis and sleep ignore it.
     */
    @Min(value = 0, message = "Potency must not be negative")
    private int potency;

    /** Ticks it has been held, which is what poison ramps on. */
    private int turnsHeld;

    @Override
    public void onApply(BattleState battleState) {
        PlayerState player = battleState.getPlayer();
        PlayerState opponent = battleState.getOpponent();
        CritterState caster = player.getActiveCritter();
        CritterState target = opponent.getActiveCritter();
        Ability ability = caster.getAbilityById(player.getLastSelectedAbilityId());

        String actionLog;
        boolean landed = false;
        if (target.getStatus() != null) {
            actionLog = String.format("%s's %s is already %s, so %s has no effect on it.",
                opponent.getUsername(), target.getName(), target.getStatus().getCondition().adjective(), ability.getName());
        } else if (target.getStatusImmunity() == condition) {
            actionLog = String.format("%s's %s has only just stopped being %s, so %s has no effect on it.",
                opponent.getUsername(), target.getName(), condition.adjective(), ability.getName());
        } else {
            this.casterId = caster.getId();
            target.setStatus(this.copy());
            landed = true;
            actionLog = String.format("%s's %s is afflicted with %s from %s's %s and is now %s!",
                opponent.getUsername(), target.getName(), ability.getName(), player.getUsername(), caster.getName(),
                condition.adjective());
        }

        BattleStats battleStats = battleState.getBattleStats();

        String turnActionLog = String.format("%s used %s - %s", caster.getName(), ability.getName(),
            landed ? condition.adjective() : "no effect");

        TurnActionEntry turnAction = TurnActionEntry.builder()
                .playerId(player.getId())
                .playerHasTurn(true)
                .turn(battleStats.getTurnCount())
                .turnActionLog(turnActionLog)
                .build();
        battleStats.getTurnActionHistory().add(turnAction);

        battleState.getActionLogHistory().add(actionLog);
    }

    @Override
    public boolean onTick(BattleState battleState, CritterState target) {
        if (condition == StatusCondition.SLEEP) return false;

        this.turnsHeld++;
        int damage = switch (condition) {
            case BURN -> potency;
            case POISON -> potency * turnsHeld;
            default -> 0;
        };

        if (damage > 0) {
            int dealt = target.takeDamage(damage);
            BattleStats battleStats = battleState.getBattleStats();
            battleStats.getPlayersDamageDealt().put(this.casterId,
                battleStats.getPlayersDamageDealt().getOrDefault(this.casterId, 0) + dealt);
        }

        this.duration--;

        String actionLog = damage > 0
            ? String.format("%s is %s and takes %d damage! %d Turns remaining.", target.getName(), condition.adjective(), damage, this.duration)
            : String.format("%s is %s. %d Turns remaining.", target.getName(), condition.adjective(), this.duration);
        battleState.getActionLogHistory().add(actionLog);

        return expireIfSpent(battleState, target);
    }

    /** Spends one of the sleeping owner's turns. Returns true once the last one is spent. */
    public boolean sleepThroughTurn(BattleState battleState, CritterState owner) {
        this.duration--;
        battleState.getActionLogHistory().add(
            String.format("%s is fast asleep and loses the turn! %d Turns remaining.", owner.getName(), this.duration));

        return expireIfSpent(battleState, owner);
    }

    private boolean expireIfSpent(BattleState battleState, CritterState target) {
        if (this.duration > 0) return false;

        battleState.getActionLogHistory().add(String.format("%s is no longer %s.", target.getName(), condition.adjective()));
        return true;
    }

    /**
     * An independent copy, so the one on a critter never shares its countdown
     * with the ability it came from or with a snapshot taken of it.
     */
    public StatusEffect copy() {
        return StatusEffect.builder()
                .id(this.id)
                .description(this.description)
                .condition(this.condition)
                .duration(this.duration)
                .potency(this.potency)
                .turnsHeld(this.turnsHeld)
                .casterId(this.casterId)
                .build();
    }
}
