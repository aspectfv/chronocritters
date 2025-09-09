package com.chronocritters.gamelogic.strategies.abilities;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.chronocritters.lib.context.ExecuteAbilityContext;
import com.chronocritters.lib.interfaces.AbilityStrategy;
import com.chronocritters.lib.model.Ability;
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
        typeAdvantages.put(CritterType.METAL, Set.of(CritterType.ELECTRIC, CritterType.TOXIC));
        typeAdvantages.put(CritterType.TOXIC, Set.of(CritterType.GRASS));
    }
    
    @Override
    public AbilityType getAbilityType() {
        return AbilityType.ATTACK;
    }

    @Override
    public void executeAbility(ExecuteAbilityContext context) {
        BattleState currentBattle = context.getBattleState();
        PlayerState player = context.getPlayer();
        PlayerState opponent = context.getOpponent();
        CritterState activeCritter = context.getActiveCritter();
        Ability ability = context.getAbility();
        
        CritterState opponentActiveCritter = opponent.getCritterByIndex(opponent.getActiveCritterIndex());
        CurrentStats opponentCritterStats = opponentActiveCritter.getStats();

        int activeCritterAttack = activeCritter.getStats().getCurrentAtk();
        int opponentCritterDefense = opponentCritterStats.getCurrentDef();

        double typeMultiplier = 1.0;
        String typeEffectiveness = null;
        CritterType attackerType = activeCritter.getType();
        CritterType defenderType = opponentActiveCritter.getType();

        if (typeAdvantages.getOrDefault(attackerType, Set.of()).contains(defenderType)) {
            typeMultiplier = 1.5;
            typeEffectiveness = "It's super effective!";
        } else if (typeAdvantages.getOrDefault(defenderType, Set.of()).contains(attackerType)) {
            typeMultiplier = 0.5;
            typeEffectiveness = "It's not very effective...";
        }

        int baseDamage = (int) Math.max(0, ability.getPower() * (activeCritterAttack / (double)(activeCritterAttack + opponentCritterDefense)));
        int finalDamage = (int) Math.max(1, baseDamage * typeMultiplier);

        int newHealth = Math.max(0, opponentCritterStats.getCurrentHp() - finalDamage);
        opponentCritterStats.setCurrentHp(newHealth);

        String actionLog = String.format("%s's %s used %s for %d damage! %s's %s now has %d health.",
            player.getUsername(), activeCritter.getName(), ability.getName(), finalDamage,
            opponent.getUsername(), opponentActiveCritter.getName(), newHealth);

        if (typeEffectiveness != null) {
            actionLog += " " + typeEffectiveness;
        }

        currentBattle.getActionLogHistory().add(actionLog);
    }
}