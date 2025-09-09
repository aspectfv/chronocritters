package com.chronocritters.gamelogic.handler;

import com.chronocritters.lib.context.ExecuteAbilityContext;
import com.chronocritters.lib.interfaces.AbilityStrategy;
import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.AbilityType;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.PlayerState;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class ExecuteAbilityHandler extends AbstractTurnActionHandler {
    private final String abilityId;
    private final Map<AbilityType, AbilityStrategy> abilityStrategies;

    @Override
    public void handle(BattleState battleState) {
        PlayerState player = battleState.getPlayer();
        PlayerState opponent = battleState.getOpponent();
        CritterState activeCritter = player.getActiveCritter();

        Ability ability = activeCritter.getAbilityById(abilityId);
        if (ability == null) {
            throw new IllegalArgumentException("Invalid ability ID: " + abilityId);
        }

        AbilityStrategy strategy = abilityStrategies.get(ability.getType());
        if (strategy == null) {
            throw new IllegalStateException("No strategy found for ability type: " + ability.getType());
        }

        ExecuteAbilityContext context = ExecuteAbilityContext.builder()
                .battleState(battleState)
                .player(player)
                .opponent(opponent)
                .activeCritter(activeCritter)
                .ability(ability)
                .build();

        strategy.executeAbility(context);

        handleNext(battleState);
    }
}