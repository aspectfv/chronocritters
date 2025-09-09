package com.chronocritters.gamelogic.strategies.abilities;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.chronocritters.lib.context.AbilityExecutionContext;
import com.chronocritters.lib.interfaces.AbilityStrategy;
import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.AbilityExecutionResult;
import com.chronocritters.lib.model.AbilityType;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.CritterType;
import com.chronocritters.lib.model.CurrentStats;
import com.chronocritters.lib.model.PlayerState;

@Component
public class AttackAbilityStrategy implements AbilityStrategy {
    private static final Map<CritterType, Set<CritterType>> typeAdvantages = new EnumMap<>(CritterType.class);

    static {
        typeAdvantages.put(CritterType.FIRE, Set.of(CritterType.GRASS, CritterType.METAL));
        typeAdvantages.put(CritterType.WATER, Set.of(CritterType.FIRE));
        typeAdvantages.put(CritterType.GRASS, Set.of(CritterType.WATER));
        typeAdvantages.put(CritterType.ELECTRIC, Set.of(CritterType.WATER));
        typeAdvantages.put(CritterType.METAL, Set.of(CritterType.ELECTRIC));
    }
    
    @Override
    public AbilityType getAbilityType() {
        return AbilityType.ATTACK;
    }

    @Override
    public AbilityExecutionResult executeAbility(AbilityExecutionContext context) {
        BattleState currentBattle = context.getBattleState();
        PlayerState player = context.getPlayer();
        PlayerState opponent = context.getOpponent();
        CritterState activeCritter = context.getActiveCritter();
        Ability ability = context.getAbility();
        
        int abilityPower = ability.getPower();
            
        CritterState opponentActiveCritter = opponent.getCritterByIndex(opponent.getActiveCritterIndex());
        CurrentStats opponentCritterStats = opponentActiveCritter.getStats();

        int activeCritterAttack = activeCritter.getStats().getCurrentAtk();
        int opponentCritterDefense = opponentCritterStats.getCurrentDef();

        double typeMultipler = 1.0;
        String typeEffectiveness = null;
        CritterType attackerType = activeCritter.getType();
        CritterType defenderType = opponentActiveCritter.getType();

        if (typeAdvantages.get(attackerType).contains(defenderType)) {
            typeMultipler = 1.5;
            typeEffectiveness = "It's super effective!";
        } else if (typeAdvantages.get(defenderType).contains(attackerType)) {
            typeMultipler = 0.5;
            typeEffectiveness = "It's not very effective...";
        }

        int baseDamage = (int) Math.max(0, abilityPower * (activeCritterAttack / (double)(activeCritterAttack + opponentCritterDefense)));
        int finalDamage = (int) Math.max(1, baseDamage * typeMultipler);

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

        if (typeEffectiveness != null) {
            actionLog += " " + typeEffectiveness;
        }

        currentBattle.getActionLogHistory().add(actionLog);
        
        if (newHealth == 0) {
            int nextCritterIndex = IntStream.range(0, opponent.getRoster().size())
                .filter(i -> opponent.getCritterByIndex(i).getStats().getCurrentHp() > 0)
                .findFirst()
                .orElse(-1);
            
            if (nextCritterIndex != -1) {
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