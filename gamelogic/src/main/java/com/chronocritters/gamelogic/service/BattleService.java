package com.chronocritters.gamelogic.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.chronocritters.gamelogic.converter.PlayerConverter;
import com.chronocritters.gamelogic.grpc.PlayerGrpcClient;
import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.CurrentStats;
import com.chronocritters.lib.model.PlayerState;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BattleService {
    private final List<BattleState> activeBattles = new ArrayList<>();
    private final PlayerGrpcClient playerGrpcClient;

    public BattleState getBattleState(String battleId) {
        return activeBattles.stream()
                .filter(battle -> battle.getBattleId().equals(battleId))
                .findFirst()
                .orElse(null);
    }

    public void createBattle(String battleId, String playerOneId, String playerTwoId) {
        PlayerState playerOne = PlayerConverter.convertToPlayerState(playerGrpcClient.getPlayer(playerOneId));
        PlayerState playerTwo = PlayerConverter.convertToPlayerState(playerGrpcClient.getPlayer(playerTwoId));

        playerOne.setHasTurn(true);
        playerTwo.setHasTurn(false);

        String initialLog = "Battle started between " + playerOne.getUsername() + " and " + playerTwo.getUsername();
        List<String> logHistory = new ArrayList<>();
        logHistory.add(initialLog);

        BattleState battleState = BattleState.builder()
                .battleId(battleId)
                .activePlayerId(playerOneId)
                .playerOne(playerOne)
                .playerTwo(playerTwo)
                .actionLogHistory(logHistory)
                .build();

        activeBattles.add(battleState);
    }

    public BattleState executeAbility(String battleId, String playerId, String abilityId) {
        BattleState currentBattle = getBattleState(battleId);
        if (currentBattle == null) {
            throw new IllegalArgumentException("Invalid battle ID");
        }

        if (!currentBattle.getActivePlayerId().equals(playerId)) {
            throw new IllegalStateException("It's not the player's turn");
        }

        PlayerState currentPlayer = currentBattle.getCurrentPlayer();

        PlayerState opponent = currentBattle.getOpponent();

        CritterState activeCritter = currentPlayer.getCritterByIndex(currentPlayer.getActiveCritterIndex());

        Ability ability = activeCritter.getAbilityById(abilityId);
        if (ability == null) {
            throw new IllegalArgumentException("Invalid ability ID");
        }

        switch (ability.getType()) {
            case ATTACK -> executeAttackAbility(currentBattle, currentPlayer, opponent, activeCritter, ability);
            case DEFENSE -> executeDefenseAbility(currentBattle, currentPlayer, opponent, activeCritter, ability);
            case SUPPORT -> executeSupportAbility(currentBattle, currentPlayer, opponent, activeCritter, ability);
            default -> throw new IllegalArgumentException("Unexpected value: " + ability.getType());
        }

        return currentBattle;
    }

    private void executeAttackAbility(
        BattleState currentBattle, PlayerState currentPlayer, 
        PlayerState opponent, CritterState activeCritter, Ability ability
    ) {
        int damage = ability.getPower();
            
        CritterState opponentActiveCritter = opponent.getCritterByIndex(opponent.getActiveCritterIndex());
        CurrentStats opponentCritterStats = opponentActiveCritter.getStats();

        int newHealth = Math.max(0, opponentCritterStats.getCurrentHp() - damage);
        opponentCritterStats.setCurrentHp(newHealth);

        String actionLog = String.format("%s's %s used %s for %d damage! %s's %s now has %d health.",
            currentPlayer.getUsername(),
            activeCritter.getName(),
            ability.getName(),
            damage,
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
                    currentPlayer.getUsername() + " wins the battle!";
                currentBattle.getActionLogHistory().add(winLog);
                return;
            }
        }
        
        currentBattle.setActivePlayerId(opponent.getId());

        currentPlayer.setHasTurn(false);
        opponent.setHasTurn(true);
    }

    private void executeDefenseAbility(
        BattleState currentBattle, PlayerState currentPlayer, 
        PlayerState opponent, CritterState activeCritter, Ability ability
    ) {
        CurrentStats critterStats = activeCritter.getStats();
        
        int defenseBoost = ability.getPower();
        int newDefense = critterStats.getCurrentDef() + defenseBoost;
        critterStats.setCurrentDef(newDefense);
        
        String actionLog = String.format("%s's %s used %s! %s's defense increased by %d (now %d).",
            currentPlayer.getUsername(),
            activeCritter.getName(),
            ability.getName(),
            activeCritter.getName(),
            defenseBoost,
            newDefense);
        
        currentBattle.getActionLogHistory().add(actionLog);
        currentBattle.setActivePlayerId(opponent.getId());
        
        currentPlayer.setHasTurn(false);
        opponent.setHasTurn(true);
    }

    private void executeSupportAbility(BattleState currentBattle, PlayerState currentPlayer, 
                                           PlayerState opponent, CritterState activeCritter, Ability ability) {
        CurrentStats critterStats = activeCritter.getStats();

        int heal = ability.getPower();
        int newHealth = critterStats.getCurrentHp() + heal;

        critterStats.setCurrentHp(newHealth);
        String actionLog = String.format("%s's %s used %s! %s healed for %d (now %d health).",
            currentPlayer.getUsername(),
            activeCritter.getName(),
            ability.getName(),
            activeCritter.getName(),
            heal,
            newHealth);

        currentBattle.getActionLogHistory().add(actionLog);
        currentBattle.setActivePlayerId(opponent.getId());

        currentPlayer.setHasTurn(false);
        opponent.setHasTurn(true);
    }

}
