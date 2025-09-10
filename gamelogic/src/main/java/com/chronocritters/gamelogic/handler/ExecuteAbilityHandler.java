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

    @Override
    public void handle(BattleState battleState) {
        PlayerState player = battleState.getPlayer();
        CritterState activeCritter = player.getActiveCritter();

        Ability ability = activeCritter.getAbilityById(abilityId);
        if (ability == null) {
            throw new IllegalArgumentException("Invalid ability ID: " + abilityId);
        }

        ability.execute(battleState);

        handleNext(battleState);
    }
}