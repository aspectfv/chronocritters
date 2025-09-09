package com.chronocritters.gamelogic.strategies.effects;

import org.springframework.stereotype.Component;

import com.chronocritters.lib.context.ApplyEffectContext;
import com.chronocritters.lib.interfaces.EffectStrategy;
import com.chronocritters.lib.model.ActiveEffect;
import com.chronocritters.lib.model.BattleOutcome;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.EffectType;

@Component
public class DamageOverTimeEffectStrategy implements EffectStrategy {

    @Override
    public EffectType getEffectType() {
        return EffectType.DAMAGE_OVER_TIME;
    }

    @Override
    public BattleOutcome applyActiveEffect(ApplyEffectContext context) {
        ActiveEffect effect = context.getEffect();
        CritterState targetCritter = context.getTargetCritter();

        int damage = effect.getPower();
        targetCritter.getStats().setCurrentHp(Math.max(0, targetCritter.getStats().getCurrentHp() - damage));

        effect.setRemainingDuration(effect.getRemainingDuration() - 1);

        String logEntry = String.format("%s took %d damage from %s effect.",
                targetCritter.getName(), damage, effect.getType().name());
        context.getBattleState().getActionLogHistory().add(logEntry);

        // Win/faint check
        if (targetCritter.getStats().getCurrentHp() == 0) {
            String faintLog = targetCritter.getName() + " fainted from " + effect.getType().name() + "!";
            context.getBattleState().getActionLogHistory().add(faintLog);

            // Check if all critters on the player's team are fainted
            boolean playerAllFainted = context.getPlayer().getRoster().stream()
                .allMatch(c -> c.getStats().getCurrentHp() <= 0);

            boolean opponentAllFainted = context.getOpponent().getRoster().stream()
                .allMatch(c -> c.getStats().getCurrentHp() <= 0);

            if (playerAllFainted) {
                return BattleOutcome.BATTLE_LOST;
            } else if (opponentAllFainted) {
                return BattleOutcome.BATTLE_WON;
            }
        }

        return BattleOutcome.CONTINUE;
    }
}
