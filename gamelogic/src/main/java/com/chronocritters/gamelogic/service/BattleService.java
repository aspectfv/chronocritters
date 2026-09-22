package com.chronocritters.gamelogic.service;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.chronocritters.gamelogic.exception.BattleNotFoundException;
import com.chronocritters.gamelogic.grpc.PlayerGrpcClient;
import com.chronocritters.gamelogic.handler.ExecuteAbilityHandler;
import com.chronocritters.gamelogic.handler.FaintingHandler;
import com.chronocritters.gamelogic.handler.TurnEffectsHandler;
import com.chronocritters.gamelogic.handler.TurnTransitionHandler;
import com.chronocritters.lib.interfaces.handler.ITurnActionHandler;
import com.chronocritters.lib.mapper.BattleRewardsMapper;
import com.chronocritters.lib.mapper.PlayerMapper;
import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.BattleStats;
import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.battle.PlayerState;
import com.chronocritters.lib.model.enums.BattleOutcome;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BattleService {
    private final Map<String, BattleState> activeBattles = new ConcurrentHashMap<>();
    
    private final PlayerGrpcClient playerGrpcClient;
    private final LobbyWebClient lobbyWebClient;

    private final ApplicationEventPublisher eventPublisher;

    private final ScheduledExecutorService cleanupScheduler = Executors.newSingleThreadScheduledExecutor();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final int CLEANUP_DELAY_SECONDS = 60;

    /**
     * Passing the turn on a timeout forever means two idle clients hold a battle
     * and a timer thread indefinitely, so a player who misses this many turns in
     * a row concedes.
     */
    private static final int MAX_CONSECUTIVE_TIMEOUTS = 3;

    public BattleState getBattleState(String battleId) {
        return activeBattles.get(battleId);
    }

    /** The battles still waiting on someone to move, so a restarted lobby can re-arm their timers. */
    public List<BattleState> getBattlesAwaitingATurn() {
        return activeBattles.values().stream()
                .filter(battle -> battle.getBattleOutcome() == BattleOutcome.CONTINUE)
                .filter(battle -> battle.getActivePlayerId() != null)
                .toList();
    }

    /** Battle state is only readable by the two players taking part in it. */
    public BattleState getBattleStateFor(String battleId, String playerId) {
        BattleState battleState = requireBattle(battleId);

        if (battleState.getPlayerById(playerId) == null) {
            throw new IllegalStateException("Player is not part of this battle");
        }

        return battleState;
    }

    public BattleState createBattle(String battleId, String playerOneId, String playerTwoId) {
        PlayerState playerOne = PlayerMapper.toPlayerState(playerGrpcClient.getPlayer(playerOneId));
        PlayerState playerTwo = PlayerMapper.toPlayerState(playerGrpcClient.getPlayer(playerTwoId));

        playerOne.setHasTurn(true);
        playerTwo.setHasTurn(false);

        String initialLog = "Battle started between " + playerOne.getUsername() + " and " + playerTwo.getUsername();
        List<String> logHistory = new ArrayList<>();
        logHistory.add(initialLog);

        Map<String, Integer> initialDamageDealt = new HashMap<>();
        initialDamageDealt.put(playerOneId, 0);
        initialDamageDealt.put(playerTwoId, 0);

        BattleStats battleStats = BattleStats.builder()
                .battleStartTime(System.currentTimeMillis())
                .playersDamageDealt(initialDamageDealt)
                .build();

        BattleState battleState = BattleState.builder()
                .battleId(battleId)
                .activePlayerId(playerOneId)
                .playerOne(playerOne)
                .playerTwo(playerTwo)
                .actionLogHistory(logHistory)
                .timeRemaining(BattleState.TURN_DURATION_SECONDS)
                .battleStats(battleStats)
                .build();

        activeBattles.put(battleId, battleState);
        return battleState;
    }

    public BattleState executeAbility(String battleId, String playerId, String abilityId) {
        BattleState currentBattle = requireActiveTurn(battleId, playerId);

        if (playerId.equals(currentBattle.getAwaitingSwitchPlayerId())) {
            throw new IllegalStateException("Send out a replacement critter first");
        }

        currentBattle.setLastTurnResult(null);
        currentBattle.getPlayer().setConsecutiveTimeouts(0);

        ITurnActionHandler turnChain = new ExecuteAbilityHandler(abilityId);
        turnChain
            .setNext(new FaintingHandler(eventPublisher))
            .setNext(new TurnEffectsHandler())
            .setNext(new FaintingHandler(eventPublisher))
            .setNext(new TurnTransitionHandler());

        turnChain.handle(currentBattle);

        finalizeTurn(currentBattle);
        return currentBattle;
    }

    /**
     * A switch normally costs the turn. Replacing a critter that just fainted is
     * free, and is allowed whether or not the clock is on this player.
     */
    public BattleState switchCritter(String battleId, String playerId, int targetCritterIndex) {
        BattleState currentBattle = requireBattle(battleId);
        boolean isReplacingAFaintedCritter = playerId.equals(currentBattle.getAwaitingSwitchPlayerId());

        if (!isReplacingAFaintedCritter) {
            requireActiveTurn(battleId, playerId);
        }

        PlayerState player = currentBattle.getPlayerById(playerId);
        currentBattle.setLastTurnResult(null);
        player.setConsecutiveTimeouts(0);

        sendOut(currentBattle, player, targetCritterIndex);

        if (isReplacingAFaintedCritter) {
            currentBattle.setAwaitingSwitchPlayerId(null);
            finalizeTurn(currentBattle);
            return currentBattle;
        }

        ITurnActionHandler turnChain = new TurnEffectsHandler();
        turnChain
            .setNext(new FaintingHandler(eventPublisher))
            .setNext(new TurnTransitionHandler());
        
        turnChain.handle(currentBattle);

        finalizeTurn(currentBattle);
        return currentBattle;
    }

    private void sendOut(BattleState battleState, PlayerState player, int targetCritterIndex) {
        if (targetCritterIndex < 0 || targetCritterIndex >= player.getRoster().size()) throw new IllegalArgumentException("Invalid critter index");
        if (targetCritterIndex == player.getActiveCritterIndex()) throw new IllegalArgumentException("Cannot switch to the currently active critter");

        CritterState targetCritter = player.getCritterByIndex(targetCritterIndex);
        if (targetCritter.getStats().getCurrentHp() <= 0) throw new IllegalArgumentException("Cannot switch to a fainted critter");

        battleState.getActionLogHistory().add(String.format("%s switched from %s to %s", player.getUsername(),
                player.getActiveCritter().getName(), targetCritter.getName()));

        player.setActiveCritterIndex(targetCritterIndex);
    }
    
    public void handleTurnTimeout(String battleId) {
        BattleState currentBattle = requireBattle(battleId);

        if (currentBattle.getBattleOutcome() != BattleOutcome.CONTINUE) {
            return;
        }

        currentBattle.setLastTurnResult(null);

        String owedBy = currentBattle.getAwaitingSwitchPlayerId();
        if (owedBy != null) {
            sendOutFirstLivingCritter(currentBattle, currentBattle.getPlayerById(owedBy));
            return;
        }

        PlayerState idlePlayer = currentBattle.getPlayer();
        idlePlayer.setConsecutiveTimeouts(idlePlayer.getConsecutiveTimeouts() + 1);

        currentBattle.getActionLogHistory().add(String.format("%s ran out of time!", idlePlayer.getUsername()));

        if (idlePlayer.getConsecutiveTimeouts() >= MAX_CONSECUTIVE_TIMEOUTS) {
            currentBattle.getActionLogHistory().add(
                String.format("%s missed %d turns in a row and concedes.", idlePlayer.getUsername(), MAX_CONSECUTIVE_TIMEOUTS));
            forfeit(battleId, idlePlayer.getId());
            return;
        }

        ITurnActionHandler turnChain = new TurnEffectsHandler();
        turnChain
            .setNext(new FaintingHandler(eventPublisher))
            .setNext(new TurnTransitionHandler());

        turnChain.handle(currentBattle);

        finalizeTurn(currentBattle);
    }

    /**
     * Ends the battle immediately in the opponent's favour. Used both when a
     * player forfeits and when the lobby reports that they have disconnected.
     */
    public void forfeit(String battleId, String playerId) {
        BattleState currentBattle = requireBattle(battleId);

        PlayerState forfeitingPlayer = currentBattle.getPlayerById(playerId);
        if (forfeitingPlayer == null) throw new IllegalArgumentException("Player is not part of this battle");

        if (currentBattle.getBattleOutcome() != BattleOutcome.CONTINUE) {
            return;
        }

        forfeitingPlayer.getRoster().forEach(critter -> {
            critter.getStats().setCurrentHp(0);
            critter.setFainted(true);
        });

        currentBattle.getActionLogHistory().add(String.format("%s forfeited the battle!", forfeitingPlayer.getUsername()));

        finalizeTurn(currentBattle);
    }

    /** The clock ran out on a replacement choice, so the roster order decides it. */
    private void sendOutFirstLivingCritter(BattleState battleState, PlayerState player) {
        int replacementIndex = FaintingService.firstLivingCritterIndex(player);
        battleState.setAwaitingSwitchPlayerId(null);

        if (replacementIndex < 0) {
            finalizeTurn(battleState);
            return;
        }

        battleState.getActionLogHistory().add(String.format("%s took too long, so %s is sent out.",
                player.getUsername(), player.getCritterByIndex(replacementIndex).getName()));
        player.setActiveCritterIndex(replacementIndex);

        finalizeTurn(battleState);
    }

    private BattleState requireBattle(String battleId) {
        BattleState battleState = getBattleState(battleId);
        if (battleState == null) throw new BattleNotFoundException(battleId);
        return battleState;
    }

    private BattleState requireActiveTurn(String battleId, String playerId) {
        BattleState battleState = requireBattle(battleId);
        if (!playerId.equals(battleState.getActivePlayerId())) throw new IllegalStateException("It's not the player's turn");
        return battleState;
    }

    private void finalizeTurn(BattleState battleState) {
        boolean playerOneHasLost = battleState.getPlayerOne().getRoster().stream().allMatch(CritterState::isFainted);
        boolean playerTwoHasLost = battleState.getPlayerTwo().getRoster().stream().allMatch(CritterState::isFainted);

        if (playerOneHasLost || playerTwoHasLost) {
            battleState.setBattleOutcome(BattleOutcome.BATTLE_END);
            
            PlayerState winner = playerOneHasLost ? battleState.getPlayerTwo() : battleState.getPlayerOne();
            PlayerState loser = playerOneHasLost ? battleState.getPlayerOne() : battleState.getPlayerTwo();

            battleState.setWinnerId(winner.getId());

            String endLog = String.format("%s has no more critters! %s wins the battle!", 
                loser.getUsername(),
                winner.getUsername()
            );
            battleState.getActionLogHistory().add(endLog);

            battleState.getBattleStats().setDuration(
                (System.currentTimeMillis() - battleState.getBattleStats().getBattleStartTime()) / 1000
            );

            applyWinLoss(battleState, winner, loser);
        }

        lobbyWebClient.updateBattleState(battleState.getBattleId(), battleState).subscribe();
    }

    private void applyWinLoss(BattleState battleState, PlayerState winner, PlayerState loser) {
        battleState.setActivePlayerId(null);

        List<String> winnerRoster = winner.getRoster().stream().map(CritterState::getId).toList();
        List<String> loserRoster = loser.getRoster().stream().map(CritterState::getId).toList();

        // Rewards are best-effort. The battle is already decided, so a user
        // service that is down must cost the players their experience, not leave
        // both of them staring at a board that never reaches its end state.
        try {
            playerGrpcClient.updateMatchHistory(
                battleState.getBattleId(), winner.getId(), loser.getId(),
                battleState.getBattleStats(), winnerRoster, loserRoster
            );

            battleState.setBattleRewards(BattleRewardsMapper.toModel(
                playerGrpcClient.getBattleRewards(winner.getId(), loser.getId(), winnerRoster, loserRoster)
            ));
        } catch (RuntimeException e) {
            logger.error("Could not record the result of battle {}: {}", battleState.getBattleId(), e.getMessage(), e);
        }

        cleanupScheduler.schedule(() -> {
            BattleState removed = activeBattles.remove(battleState.getBattleId());
            if (removed != null) {
                logger.info("Battle {} cleaned up.", battleState.getBattleId());
            }
        }, CLEANUP_DELAY_SECONDS, TimeUnit.SECONDS);
    }
}