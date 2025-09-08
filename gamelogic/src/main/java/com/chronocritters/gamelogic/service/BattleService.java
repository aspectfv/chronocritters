package com.chronocritters.gamelogic.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.chronocritters.gamelogic.client.LobbyWebClient;
import com.chronocritters.gamelogic.grpc.PlayerGrpcClient;
import com.chronocritters.lib.context.AbilityExecutionContext;
import com.chronocritters.lib.interfaces.AbilityStrategy;
import com.chronocritters.lib.mapper.PlayerProtoMapper;
import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.AbilityExecutionResult;
import com.chronocritters.lib.model.AbilityType;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.PlayerState;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BattleService {
    private final Map<String, BattleState> activeBattles = new ConcurrentHashMap<>();
    private final Map<AbilityType, AbilityStrategy> abilityStrategies;
    
    private final PlayerGrpcClient playerGrpcClient;
    private final LobbyWebClient lobbyWebClient;

    private final ScheduledExecutorService cleanupScheduler = Executors.newSingleThreadScheduledExecutor();

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final int TURN_DURATION_SECONDS = 30;
    private static final int CLEANUP_DELAY_SECONDS = 60;

    public BattleState getBattleState(String battleId) {
        return activeBattles.get(battleId);
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

        activeBattles.put(battleId, battleState);
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

        AbilityStrategy strategy = abilityStrategies.get(ability.getType());
        if (strategy == null) {
            throw new IllegalStateException("No strategy found for ability type: " + ability.getType());
        }

        AbilityExecutionContext context = AbilityExecutionContext.builder()
            .battleState(currentBattle)
            .player(player)
            .opponent(opponent)
            .activeCritter(activeCritter)
            .ability(ability)
            .build();

        AbilityExecutionResult result = strategy.executeAbility(context);

        if (result == AbilityExecutionResult.BATTLE_WON) {
            playerGrpcClient.updateMatchHistory(player.getId(), opponent.getId());
            lobbyWebClient.updateBattleState(battleId, currentBattle).subscribe();

            cleanupScheduler.schedule(() -> {
                BattleState removed = activeBattles.remove(battleId);
                if (removed != null) {
                    log.info("Battle {} cleaned up after timeout", battleId);
                }
            }, CLEANUP_DELAY_SECONDS, TimeUnit.SECONDS);
            return currentBattle;
        }

        currentBattle.setActivePlayerId(opponent.getId());
        currentBattle.setTimeRemaining(TURN_DURATION_SECONDS);
        
        player.setHasTurn(false);
        opponent.setHasTurn(true);

        lobbyWebClient.updateBattleState(battleId, currentBattle).subscribe();
        return currentBattle;
    }

    public BattleState switchCritter(String battleId, String playerId, int targetCritterIndex) {
        BattleState currentBattle = getBattleState(battleId);
        if (currentBattle == null) {
            throw new IllegalArgumentException("Invalid battle ID");
        }

        if (!currentBattle.getActivePlayerId().equals(playerId)) {
            throw new IllegalStateException("It's not the player's turn");
        }

        PlayerState player = currentBattle.getPlayer();
        PlayerState opponent = currentBattle.getOpponent();

        if (targetCritterIndex < 0 || targetCritterIndex >= player.getRoster().size()) {
            throw new IllegalArgumentException("Invalid critter index");
        }

        if (targetCritterIndex == player.getActiveCritterIndex()) {
            throw new IllegalArgumentException("Cannot switch to the currently active critter");
        }

        CritterState targetCritter = player.getCritterByIndex(targetCritterIndex);
        if (targetCritter.getStats().getCurrentHp() <= 0) {
            throw new IllegalArgumentException("Cannot switch to a fainted critter");
        }

        String switchLog = String.format("%s switched from %s to %s", player.getUsername(),
                player.getCritterByIndex(player.getActiveCritterIndex()).getName(), targetCritter.getName());
        currentBattle.getActionLogHistory().add(switchLog);

        player.setActiveCritterIndex(targetCritterIndex);

        currentBattle.setActivePlayerId(opponent.getId());
        currentBattle.setTimeRemaining(TURN_DURATION_SECONDS);
        
        player.setHasTurn(false);
        opponent.setHasTurn(true);

        lobbyWebClient.updateBattleState(battleId, currentBattle).subscribe();
        return currentBattle;
    }
    
    public void handleTurnTimeout(String battleId) {
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
        
        lobbyWebClient.updateBattleState(battleId, currentBattle).subscribe();
    }


}