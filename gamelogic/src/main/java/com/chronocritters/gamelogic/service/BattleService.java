package com.chronocritters.gamelogic.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.chronocritters.gamelogic.grpc.PlayerGrpcClient;
import com.chronocritters.lib.mapper.PlayerProtoMapper;
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

    private static final int TURN_DURATION_SECONDS = 30;

    public BattleState getBattleState(String battleId) {
        return activeBattles.stream()
                .filter(battle -> battle.getBattleId().equals(battleId))
                .findFirst()
                .orElse(null);
    }

    public void createBattle(String battleId, String playerOneId, String playerTwoId) {
        PlayerState playerOne = PlayerProtoMapper.convertToPlayerState(playerGrpcClient.getPlayer(playerOneId));
        PlayerState playerTwo = PlayerProtoMapper.convertToPlayerState(playerGrpcClient.getPlayer(playerTwoId));

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
                .timeRemaining(TURN_DURATION_SECONDS)
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

        PlayerState player = currentBattle.getPlayer();
        PlayerState opponent = currentBattle.getOpponent();

        CritterState activeCritter = player.getCritterByIndex(player.getActiveCritterIndex());

        Ability ability = activeCritter.getAbilityById(abilityId);
        if (ability == null) {
            throw new IllegalArgumentException("Invalid ability ID");
        }

        switch (ability.getType()) {
            case ATTACK -> executeAttackAbility(currentBattle, player, opponent, activeCritter, ability);
            case DEFENSE -> executeDefenseAbility(currentBattle, player, activeCritter, ability);
            case HEAL -> executeHealAbility(currentBattle, player, activeCritter, ability);
            default -> throw new IllegalArgumentException("Unexpected value: " + ability.getType());
        }

        currentBattle.setActivePlayerId(opponent.getId());
        currentBattle.setTimeRemaining(TURN_DURATION_SECONDS);
        
        player.setHasTurn(false);
        opponent.setHasTurn(true);

        return currentBattle;
    }

    public BattleState handleTurnTimeout(String battleId) {
        BattleState currentBattle = getBattleState(battleId);
        if (currentBattle == null) {
            throw new IllegalArgumentException("Invalid battle ID");
        }

        PlayerState player = currentBattle.getPlayer();
        PlayerState opponent = currentBattle.getOpponent();

        String timeoutLog = String.format("%s ran out of time!", player.getUsername());
        currentBattle.getActionLogHistory().add(timeoutLog);
        
        currentBattle.setActivePlayerId(opponent.getId());
        currentBattle.setTimeRemaining(TURN_DURATION_SECONDS);
        
        player.setHasTurn(false);
        opponent.setHasTurn(true);
        
        return currentBattle;
    }

    private void executeAttackAbility(
        BattleState currentBattle, PlayerState player, 
        PlayerState opponent, CritterState activeCritter, Ability ability
    ) {
        int damage = ability.getPower();
            
        CritterState opponentActiveCritter = opponent.getCritterByIndex(opponent.getActiveCritterIndex());
        CurrentStats opponentCritterStats = opponentActiveCritter.getStats();

        int newHealth = Math.max(0, opponentCritterStats.getCurrentHp() - damage);
        opponentCritterStats.setCurrentHp(newHealth);

        String actionLog = String.format("%s's %s used %s for %d damage! %s's %s now has %d health.",
            player.getUsername(),
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
                    player.getUsername() + " wins the battle!";
                currentBattle.getActionLogHistory().add(winLog);

                playerGrpcClient.updateMatchHistory(player.getId(), opponent.getId());
            }
        }
    }

    private void executeDefenseAbility(
        BattleState currentBattle, PlayerState player, CritterState activeCritter, Ability ability
    ) {
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

    private void executeHealAbility(
    BattleState currentBattle, PlayerState player, CritterState activeCritter, Ability ability
    ) {
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
    }

}