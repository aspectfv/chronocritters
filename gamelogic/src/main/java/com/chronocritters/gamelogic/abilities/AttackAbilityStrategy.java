package com.chronocritters.gamelogic.abilities;

import com.chronocritters.lib.context.AbilityExecutionContext;
import com.chronocritters.lib.interfaces.AbilityStrategy;
import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.AbilityExecutionResult;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.CurrentStats;
import com.chronocritters.lib.model.PlayerState;

public class AttackAbilityStrategy implements AbilityStrategy {
    @Override
    public AbilityExecutionResult executeAbility(AbilityExecutionContext context) {
        BattleState currentBattle = context.getBattleState();
        PlayerState player = context.getPlayer();
        PlayerState opponent = context.getOpponent();
        CritterState activeCritter = context.getActiveCritter();
        Ability ability = context.getAbility();
        
        int abilityDamage = ability.getPower();
            
        CritterState opponentActiveCritter = opponent.getCritterByIndex(opponent.getActiveCritterIndex());
        CurrentStats opponentCritterStats = opponentActiveCritter.getStats();
        int activeCritterAttack = activeCritter.getStats().getCurrentAtk();
        int opponentCritterDefense = opponentCritterStats.getCurrentDef();

        int finalDamage = (int) Math.max(0, abilityDamage * (activeCritterAttack / (double)(activeCritterAttack + opponentCritterDefense)));

        int newHealth = Math.max(0, opponentCritterStats.getCurrentHp() - finalDamage);
        opponentCritterStats.setCurrentHp(newHealth);

        String actionLog = String.format("%s's %s used %s for %d damage! %s's %s now has %d health.",
            player.getUsername(),
            activeCritter.getName(),
            ability.getName(),
            finalDamage,
            opponent.getUsername(),
            opponentActiveCritter.getName(),
            newHealth);

        currentBattle.getActionLogHistory().add(actionLog);
        
        if (newHealth == 0) {
            int nextCritterIndex = opponent.getActiveCritterIndex() + 1;
            
            if (nextCritterIndex < opponent.getRoster().size()) {
                CritterState nextCritter = opponent.getCritterByIndex(nextCritterIndex);
                opponent.setActiveCritterIndex(nextCritterIndex);

                String faintLog = opponentActiveCritter.getName() + " fainted! " + 
                    opponent.getUsername() + " sent out " + nextCritter.getName() + "!";
                currentBattle.getActionLogHistory().add(faintLog);
            } else {
                currentBattle.setActivePlayerId(null);
                
                String winLog = opponentActiveCritter.getName() + " fainted! " + 
                    player.getUsername() + " wins the battle!";
                currentBattle.getActionLogHistory().add(winLog);

                return AbilityExecutionResult.BATTLE_WON;
            }
        }

        return AbilityExecutionResult.CONTINUE;
    }
}