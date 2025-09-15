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

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class SkipTurnEffect extends Effect implements IPersistentEffect {
    @Min(value = 1, message = "Duration must be at least 1 turn")
    private int duration;

    @Override
    public void onApply(BattleState battleState) {
        PlayerState player = battleState.getPlayer();
        PlayerState opponent = battleState.getOpponent();
        CritterState caster = player.getActiveCritter();
        CritterState target = opponent.getActiveCritter();
        Ability ability = caster.getAbilityById(player.getLastSelectedAbilityId());

        Optional<SkipTurnEffect> existingEffect = target.getActiveStatusEffects().stream()
            .filter(e -> e.getId().equals(this.getId()))
            .map(e -> (SkipTurnEffect) e)
            .findFirst();

        if (existingEffect.isPresent()) {
            existingEffect.get().setDuration(this.duration);
        } else {
            target.getActiveStatusEffects().add(this.createInstance());
        }

        BattleStats battleStats = battleState.getBattleStats();

        String turnActionLog = String.format("%s used %s - skipping %s turns",
            caster.getName(), ability.getName(), duration);

        TurnActionEntry turnAction = TurnActionEntry.builder()
                .playerId(player.getId())
                .playerHasTurn(true)
                .turn(battleStats.getTurnCount())
                .turnActionLog(turnActionLog)
                .build();
        battleStats.getTurnActionHistory().add(turnAction);

        String actionLog = String.format("%s's %s is afflicted with %s for %d turns!",
            player.getUsername(), target.getName(), ability.getName(), this.duration);

        battleState.getActionLogHistory().add(actionLog);
    }

    @Override
    public boolean onTick(BattleState battleState, CritterState target) {
        this.duration--;

        if (this.duration >= 0) {
            String actionLog = String.format("%s is stunned and unable to move! %d Turns remaining.", target.getName(), this.duration);
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
                .duration(this.duration)
                .build();
    }
}
