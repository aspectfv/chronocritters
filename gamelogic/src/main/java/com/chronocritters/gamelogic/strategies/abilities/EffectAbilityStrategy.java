package com.chronocritters.gamelogic.strategies.abilities;

import java.util.List;
import org.springframework.stereotype.Component;

import com.chronocritters.lib.context.ExecuteAbilityContext;
import com.chronocritters.lib.interfaces.AbilityStrategy;
import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.BattleOutcome;
import com.chronocritters.lib.model.AbilityType;
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
    public BattleOutcome executeAbility(ExecuteAbilityContext context) {
        CritterState activeCritter = context.getActiveCritter();
        
        PlayerState opponent = context.getOpponent();
        CritterState opponentCritter = opponent.getCritterByIndex(opponent.getActiveCritterIndex());

        Ability ability = activeCritter.getAbilities().get(0);
        List<Effect> effectList = ability.getEffects();

        effectList.stream()
            .map(Effect::toActiveEffect)
            .forEach(activeEffect -> opponentCritter.getActiveEffects().add(activeEffect));

        return BattleOutcome.CONTINUE;
    }
}
