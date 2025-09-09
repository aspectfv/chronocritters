package com.chronocritters.gamelogic.strategies.abilities;

import org.springframework.stereotype.Component;

import com.chronocritters.lib.context.ExecuteAbilityContext;
import com.chronocritters.lib.interfaces.AbilityStrategy;
import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.AbilityType;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.CurrentStats;
import com.chronocritters.lib.model.PlayerState;

@Component
public class DefenseAbilityStrategy implements AbilityStrategy {
    @Override
    public AbilityType getAbilityType() {
        return AbilityType.DEFENSE;
    }

    @Override
    public void executeAbility(ExecuteAbilityContext context) {
        BattleState currentBattle = context.getBattleState();
        PlayerState player = context.getPlayer();
        CritterState activeCritter = context.getActiveCritter();
        Ability ability = context.getAbility();
        
        CurrentStats critterStats = activeCritter.getStats();
        
        int defenseBoost = ability.getPower();
        int newDefense = critterStats.getCurrentDef() + defenseBoost;
        critterStats.setCurrentDef(newDefense);
        
        String actionLog = String.format("%s's %s used %s! %s's defense increased by %d (now %d).",
            player.getUsername(),
            activeCritter.getName(),
            ability.getName(),
            activeCritter.getName(),
            defenseBoost,
            newDefense);
        
        currentBattle.getActionLogHistory().add(actionLog);
    }
    
}
