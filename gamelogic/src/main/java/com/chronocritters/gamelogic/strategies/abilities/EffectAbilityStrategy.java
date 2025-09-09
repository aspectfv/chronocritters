package com.chronocritters.gamelogic.strategies.abilities;

import java.util.List;
import org.springframework.stereotype.Component;

import com.chronocritters.lib.context.ExecuteAbilityContext;
import com.chronocritters.lib.interfaces.AbilityStrategy;
import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.AbilityType;
import com.chronocritters.lib.model.ActiveEffect;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.Effect;
import com.chronocritters.lib.model.PlayerState;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EffectAbilityStrategy implements AbilityStrategy {

    @Override
    public AbilityType getAbilityType() {
        return AbilityType.EFFECT;
    }

    @Override
    public void executeAbility(ExecuteAbilityContext context) {
        PlayerState opponent = context.getOpponent();
        CritterState opponentCritter = opponent.getActiveCritter();

        Ability ability = context.getAbility();
        List<Effect> effectList = ability.getEffects();

        effectList.forEach(effect -> {
            if (Math.random() < (effect.getChance() / 100.0)) {
                ActiveEffect newActiveEffect = effect.toActiveEffect();
                boolean renewed = false;
                for (ActiveEffect existing : opponentCritter.getActiveEffects()) {
                    if (existing.getId().equals(newActiveEffect.getId())) {
                        existing.setRemainingDuration(newActiveEffect.getRemainingDuration());
                        existing.setCurrentChance(newActiveEffect.getCurrentChance());
                        renewed = true;
                        context.getBattleState().getActionLogHistory().add(
                            String.format("%s renewed %s on %s!", ability.getName(), existing.getName(), opponentCritter.getName())
                        );
                        break;
                    }
                }
                if (!renewed) {
                    opponentCritter.getActiveEffects().add(newActiveEffect);
                    context.getBattleState().getActionLogHistory().add(
                        String.format("%s applied %s to %s!", ability.getName(), newActiveEffect.getName(), opponentCritter.getName())
                    );
                }
            } else {
                context.getBattleState().getActionLogHistory().add(
                    String.format("%s was resisted by %s!", effect.getName(), opponentCritter.getName())
                );
            }
        });
    }
}
