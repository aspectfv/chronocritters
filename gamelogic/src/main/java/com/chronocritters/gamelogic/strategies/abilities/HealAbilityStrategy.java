package com.chronocritters.gamelogic.strategies.abilities;

import org.springframework.stereotype.Component;

import com.chronocritters.lib.context.AbilityExecutionContext;
import com.chronocritters.lib.interfaces.AbilityStrategy;
import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.AbilityExecutionResult;
import com.chronocritters.lib.model.AbilityType;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.CurrentStats;
import com.chronocritters.lib.model.PlayerState;

@Component
public class HealAbilityStrategy implements AbilityStrategy {
    @Override
    public AbilityType getAbilityType() {
        return AbilityType.HEAL;
    }
    
    @Override
    public AbilityExecutionResult executeAbility(AbilityExecutionContext context) {
        BattleState currentBattle = context.getBattleState();
        PlayerState player = context.getPlayer();
        CritterState activeCritter = context.getActiveCritter();
        Ability ability = context.getAbility();

        CurrentStats critterStats = activeCritter.getStats();

        int heal = ability.getPower();
        int newHealth = Math.min(critterStats.getMaxHp(), critterStats.getCurrentHp() + heal);
        critterStats.setCurrentHp(newHealth);

        String actionLog = String.format("%s's %s used %s! %s healed for %d (now %d health).",
            player.getUsername(),
            activeCritter.getName(),
            ability.getName(),
            activeCritter.getName(),
            heal,
            newHealth);

        currentBattle.getActionLogHistory().add(actionLog);

        return AbilityExecutionResult.CONTINUE;
    }
    
}
