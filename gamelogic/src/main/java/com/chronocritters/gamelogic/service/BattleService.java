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

    public BattleState getBattle(String battleId) {
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

        BattleState battleState = BattleState.builder()
                .battleId(battleId)
                .activePlayerId(playerOneId)
                .playerOne(playerOne)
                .playerTwo(playerTwo)
                .lastActionLog("Battle started between " + playerOne.getUsername() + " and " + playerTwo.getUsername())
                .build();

        activeBattles.add(battleState);
    }

    public BattleState executeAbility(String battleId, String playerId, String abilityId) {
        BattleState currentBattle = getBattle(battleId);
        if (currentBattle == null) {
            throw new IllegalArgumentException("Invalid battle ID");
        }

        if (!currentBattle.getActivePlayerId().equals(playerId)) {
            throw new IllegalStateException("It's not the player's turn");
        }

        PlayerState currentPlayer = playerId.equals(currentBattle.getPlayerOne().getId()) 
            ? currentBattle.getPlayerOne() 
            : currentBattle.getPlayerTwo();

        PlayerState opponent = playerId.equals(currentBattle.getPlayerOne().getId()) 
            ? currentBattle.getPlayerTwo() 
            : currentBattle.getPlayerOne();

        CritterState activeCritter = currentPlayer.getRoster().get(currentPlayer.getActiveCritterIndex());

        Ability ability = activeCritter.getAbilityById(abilityId);
        if (ability == null) {
            throw new IllegalArgumentException("Invalid ability ID");
        }

        switch (ability.getType()) {
            case ATTACK -> {
                return executeAttackAbility(currentBattle, currentPlayer, opponent, activeCritter, ability);
            }
            case DEFENSE -> {
            }
            case SUPPORT -> {
                
            }
            default -> throw new IllegalArgumentException("Unexpected value: " + ability.getType());
        }

        return currentBattle;
    }

    private BattleState executeAttackAbility(BattleState currentBattle, PlayerState currentPlayer, 
                                           PlayerState opponent, CritterState activeCritter, Ability ability) {
        // Calculate damage
        int damage = ability.getPower();
            
        // Apply damage to opponent's active critter
        CritterState opponentActiveCritter = opponent.getRoster().get(opponent.getActiveCritterIndex());
        CurrentStats opponentCritterStats = opponentActiveCritter.getStats();
        int newHealth = Math.max(0, opponentCritterStats.getCurrentHp() - damage);
        opponentCritterStats.setCurrentHp(newHealth);

        // Update battle log
        String actionLog = String.format("%s's %s used %s for %d damage! %s's %s now has %d health.",
            currentPlayer.getUsername(),
            activeCritter.getName(),
            ability.getName(),
            damage,
            opponent.getUsername(),
            opponentActiveCritter.getName(),
            newHealth);
        
        currentBattle.setLastActionLog(actionLog);
        
        // Check if opponent's critter fainted
        if (newHealth == 0) {
            // Switch to next available critter or end battle
            CritterState nextCritter = opponent.getRoster().get(opponent.getActiveCritterIndex() + 1);
            if (nextCritter != null) {
                opponent.setActiveCritterIndex(opponent.getActiveCritterIndex() + 1);
                currentBattle.setLastActionLog(actionLog + " " + opponentActiveCritter.getName() + " fainted! " + 
                    opponent.getUsername() + " sent out " + nextCritter.getName() + "!");
            } else {
                // Battle ends - current player wins
                currentBattle.setActivePlayerId(null);
                currentBattle.setLastActionLog(actionLog + " " + opponentActiveCritter.getName() + " fainted! " + 
                    currentPlayer.getUsername() + " wins the battle!");
                return currentBattle;
            }
        }
        
        // Switch turns
        String nextPlayerId = currentPlayer.getId().equals(currentBattle.getPlayerOne().getId()) 
            ? currentBattle.getPlayerTwo().getId() 
            : currentBattle.getPlayerOne().getId();
        currentBattle.setActivePlayerId(nextPlayerId);
        
        // Update turn flags
        currentPlayer.setHasTurn(false);
        opponent.setHasTurn(true);

        return currentBattle;
    }
}
