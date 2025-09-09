package com.chronocritters.gamelogic.strategies.abilities;

import java.util.List;
import org.springframework.stereotype.Component;

import com.chronocritters.lib.context.AbilityExecutionContext;
import com.chronocritters.lib.interfaces.AbilityStrategy;
import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.AbilityExecutionResult;
import com.chronocritters.lib.model.AbilityType;
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
    public AbilityExecutionResult executeAbility(AbilityExecutionContext context) {
        PlayerState player = context.getPlayer();
        PlayerState opponent = context.getOpponent();
        Ability ability = player.getCritterByIndex(player.getActiveCritterIndex()).getAbilities().get(0);
        List<Effect> effectList = ability.getEffects();
        effectList.stream()
            .map(Effect::toActiveEffect)
            .forEach(activeEffect -> opponent.getCritterByIndex(opponent.getActiveCritterIndex()).getActiveEffects().add(activeEffect));

        // Add action log, etc.
        return AbilityExecutionResult.CONTINUE;
    }
}
