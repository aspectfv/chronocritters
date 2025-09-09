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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.chronocritters.gamelogic.client.LobbyWebClient;
import com.chronocritters.gamelogic.grpc.PlayerGrpcClient;
import com.chronocritters.gamelogic.handler.TurnEffectsHandler;
import com.chronocritters.gamelogic.handler.ExecuteAbilityHandler;
import com.chronocritters.gamelogic.handler.FaintingHandler;
import com.chronocritters.gamelogic.handler.TurnTransitionHandler;
import com.chronocritters.lib.interfaces.AbilityStrategy;
import com.chronocritters.lib.interfaces.EffectStrategy;
import com.chronocritters.lib.interfaces.TurnActionHandler;
import com.chronocritters.lib.mapper.PlayerProtoMapper;
import com.chronocritters.lib.model.AbilityType;
import com.chronocritters.lib.model.BattleOutcome;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.EffectType;
import com.chronocritters.lib.model.PlayerState;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BattleService {
    private final Map<String, BattleState> activeBattles = new ConcurrentHashMap<>();

    private final Map<AbilityType, AbilityStrategy> abilityStrategies;
    private final Map<EffectType, EffectStrategy> effectStrategies;
    
    private final PlayerGrpcClient playerGrpcClient;
    private final LobbyWebClient lobbyWebClient;

    private final ApplicationEventPublisher eventPublisher;

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
        if (currentBattle == null) throw new IllegalArgumentException("Invalid battle ID");
        if (!currentBattle.getActivePlayerId().equals(playerId)) throw new IllegalStateException("It's not the player's turn");

        TurnActionHandler turnChain = new ExecuteAbilityHandler(abilityId, abilityStrategies);
        turnChain
            .setNext(new FaintingHandler(eventPublisher))
            .setNext(new TurnEffectsHandler(effectStrategies))
            .setNext(new FaintingHandler(eventPublisher))
            .setNext(new TurnTransitionHandler());

        turnChain.handle(currentBattle);

        finalizeTurn(currentBattle);
        return currentBattle;
    }

    public BattleState switchCritter(String battleId, String playerId, int targetCritterIndex) {
        BattleState currentBattle = getBattleState(battleId);
        if (currentBattle == null) throw new IllegalArgumentException("Invalid battle ID");
        if (!currentBattle.getActivePlayerId().equals(playerId)) throw new IllegalStateException("It's not the player's turn");

        PlayerState player = currentBattle.getPlayer();

        if (targetCritterIndex < 0 || targetCritterIndex >= player.getRoster().size()) throw new IllegalArgumentException("Invalid critter index");
        if (targetCritterIndex == player.getActiveCritterIndex()) throw new IllegalArgumentException("Cannot switch to the currently active critter");
        
        CritterState targetCritter = player.getCritterByIndex(targetCritterIndex);
        if (targetCritter.getStats().getCurrentHp() <= 0) throw new IllegalArgumentException("Cannot switch to a fainted critter");

        String switchLog = String.format("%s switched from %s to %s", player.getUsername(),
                player.getActiveCritter().getName(), targetCritter.getName());
        currentBattle.getActionLogHistory().add(switchLog);
        
        player.setActiveCritterIndex(targetCritterIndex);

        TurnActionHandler switchChain = new TurnEffectsHandler(effectStrategies);
        switchChain
            .setNext(new FaintingHandler(eventPublisher))
            .setNext(new TurnTransitionHandler());

        switchChain.handle(currentBattle);

        finalizeTurn(currentBattle);
        return currentBattle;
    }
    
    public void handleTurnTimeout(String battleId) {
        BattleState currentBattle = getBattleState(battleId);
        if (currentBattle == null) {
            throw new IllegalArgumentException("Invalid battle ID");
        }

        String timeoutLog = String.format("%s ran out of time!", currentBattle.getPlayer().getUsername());
        currentBattle.getActionLogHistory().add(timeoutLog);

        TurnActionHandler timeoutChain = new TurnEffectsHandler(effectStrategies);
        timeoutChain
            .setNext(new FaintingHandler(eventPublisher))
            .setNext(new TurnTransitionHandler());

        timeoutChain.handle(currentBattle);
        finalizeTurn(currentBattle);
    }

    private void finalizeTurn(BattleState battleState) {
        boolean playerOneHasLost = battleState.getPlayerOne().getRoster().stream().allMatch(CritterState::isFainted);
        boolean playerTwoHasLost = battleState.getPlayerTwo().getRoster().stream().allMatch(CritterState::isFainted);

        if (playerOneHasLost || playerTwoHasLost) {
            battleState.setBattleOutcome(BattleOutcome.BATTLE_END);
            
            String winnerId = playerOneHasLost ? battleState.getPlayerTwo().getId() : battleState.getPlayerOne().getId();
            String loserId = playerOneHasLost ? battleState.getPlayerOne().getId() : battleState.getPlayerTwo().getId();

            battleState.setWinnerId(winnerId);

            String endLog = String.format("%s has no more critters! %s wins the battle!", 
                (playerOneHasLost ? battleState.getPlayerOne().getUsername() : battleState.getPlayerTwo().getUsername()),
                (winnerId.equals(battleState.getPlayerOne().getId()) ? battleState.getPlayerOne().getUsername() : battleState.getPlayerTwo().getUsername())
            );
            battleState.getActionLogHistory().add(endLog);

            applyWinLoss(battleState, winnerId, loserId);
        }

        lobbyWebClient.updateBattleState(battleState.getBattleId(), battleState).subscribe();
    }

    private void applyWinLoss(BattleState battleState, String winnerId, String loserId) {
        battleState.setActivePlayerId(null);
        playerGrpcClient.updateMatchHistory(winnerId, loserId);

        cleanupScheduler.schedule(() -> {
            BattleState removed = activeBattles.remove(battleState.getBattleId());
            if (removed != null) {
                log.info("Battle {} cleaned up.", battleState.getBattleId());
            }
        }, CLEANUP_DELAY_SECONDS, TimeUnit.SECONDS);
    }
}