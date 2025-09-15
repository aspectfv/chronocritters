package com.chronocritters.gamelogic.handler;

import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.battle.PlayerState;
import com.chronocritters.lib.model.domain.Ability;

import lombok.RequiredArgsConstructor;

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
        player.setLastSelectedAbilityId(abilityId);

        ability.execute(battleState);

        handleNext(battleState);
    }
}