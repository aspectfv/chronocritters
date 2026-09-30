package com.chronocritters.gamelogic.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.chronocritters.gamelogic.client.LobbyWebClient;
import com.chronocritters.gamelogic.exception.BattleNotFoundException;
import com.chronocritters.gamelogic.grpc.PlayerGrpcClient;
import com.chronocritters.gamelogic.handler.ExecuteAbilityHandler;
import com.chronocritters.gamelogic.handler.FaintingHandler;
import com.chronocritters.gamelogic.handler.RewindHandler;
import com.chronocritters.gamelogic.handler.TurnEffectsHandler;
import com.chronocritters.gamelogic.handler.TurnTransitionHandler;
import com.chronocritters.lib.dto.BattleRequest;
import com.chronocritters.lib.interfaces.handler.ITurnActionHandler;
import com.chronocritters.lib.mapper.BattleRewardsMapper;
import com.chronocritters.lib.mapper.PlayerMapper;
import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.BattleStats;
import com.chronocritters.lib.model.battle.CritterSnapshot;
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

    public BattleState createBattle(String battleId, BattleRequest request) {
        PlayerState playerOne = withTeam(request.playerOneId(), request.playerOneTeam());
        PlayerState playerTwo = withTeam(request.playerTwoId(), request.playerTwoTeam());

        List<String> logHistory = new ArrayList<>();
        logHistory.add("Battle started between " + playerOne.getUsername() + " and " + playerTwo.getUsername());

        Map<String, Integer> initialDamageDealt = new HashMap<>();
        initialDamageDealt.put(playerOne.getId(), 0);
        initialDamageDealt.put(playerTwo.getId(), 0);

        BattleStats battleStats = BattleStats.builder()
                .battleStartTime(System.currentTimeMillis())
                .playersDamageDealt(initialDamageDealt)
                .build();

        BattleState battleState = BattleState.builder()
                .battleId(battleId)
                .playerOne(playerOne)
                .playerTwo(playerTwo)
                .actionLogHistory(logHistory)
                .timeRemaining(BattleState.TURN_DURATION_SECONDS)
                .battleStats(battleStats)
                .build();

        // An exact tie is decided by a coin toss rather than by who joined the
        // queue first, so neither seat is the better one to hold.
        TurnTransitionHandler.startRound(battleState);
        battleState.recordTurn();

        boolean speedTie = playerOne.getActiveCritter().effectiveSpeed() == playerTwo.getActiveCritter().effectiveSpeed();
        String opener = battleState.getPlayer().getActiveCritter().getName();
        logHistory.add(speedTie
                ? opener + " wins the toss and moves first!"
                : opener + " is faster and moves first!");

        activeBattles.put(battleId, battleState);
        return battleState;
    }

    /**
     * The player with only the critters they picked, in the order they picked
     * them, so the first one leads. A player with fewer critters than a team
     * holds brings all of them.
     */
    private PlayerState withTeam(String playerId, List<String> team) {
        PlayerState player = PlayerMapper.toPlayerState(playerGrpcClient.getPlayer(playerId));
        int teamSize = Math.min(BattleRequest.TEAM_SIZE, player.getRoster().size());

        if (team.size() != teamSize || Set.copyOf(team).size() != teamSize) {
            throw new IllegalArgumentException("A team is " + teamSize + " different critters");
        }

        Map<String, CritterState> roster = player.getRoster().stream()
                .collect(Collectors.toMap(CritterState::getId, Function.identity()));

        List<CritterState> picked = new ArrayList<>();
        for (String critterId : team) {
            CritterState critter = roster.get(critterId);
            if (critter == null) throw new IllegalArgumentException("Critter " + critterId + " is not in the player's roster");
            picked.add(critter);
        }

        player.setRoster(picked);
        return player;
    }

    public BattleState executeAbility(String battleId, String playerId, String abilityId) {
        BattleState currentBattle = requireBattle(battleId);

        // The board is frozen until every fainted active has been replaced.
        // Playing on top of one resolves the move against a critter that is
        // already down: a stun landing on a corpse, damage credited to a critter
        // on its way off the field. It is checked before the turn, because
        // whose turn comes next may not be decided until the replacement is in.
        if (currentBattle.isAwaitingSwitch()) {
            throw new IllegalStateException(currentBattle.awaitsSwitchFrom(playerId)
                    ? "Send out a replacement critter first"
                    : "Waiting for a replacement critter");
        }

        requireActiveTurn(battleId, playerId);

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
     * Puts the player's active critter back the way it stood two turns ago.
     * Once per battle, and only on the player's own turn. It costs no turn: the
     * player rewinds and then still picks a move, which is what makes the
     * timing of it the decision.
     */
    public BattleState rewind(String battleId, String playerId) {
        BattleState currentBattle = requireBattle(battleId);
        if (currentBattle.isAwaitingSwitch()) throw new IllegalStateException("Waiting for a replacement critter");
        requireActiveTurn(battleId, playerId);

        PlayerState player = currentBattle.getPlayer();
        if (player.isRewindUsed()) throw new IllegalStateException("A rewind can only be used once a battle");

        Map<String, CritterSnapshot> target = currentBattle.rewindTarget();
        if (target == null) throw new IllegalStateException("There are not two turns to rewind yet");

        player.setConsecutiveTimeouts(0);
        new RewindHandler(target.get(player.getActiveCritter().getId())).handle(currentBattle);

        finalizeTurn(currentBattle);
        return currentBattle;
    }

    /**
     * A switch normally costs the turn. Replacing a critter that just fainted is
     * free, and is allowed whether or not the clock is on this player.
     */
    public BattleState switchCritter(String battleId, String playerId, int targetCritterIndex) {
        BattleState currentBattle = requireBattle(battleId);
        boolean isReplacingAFaintedCritter = currentBattle.awaitsSwitchFrom(playerId);

        if (!isReplacingAFaintedCritter) {
            if (currentBattle.isAwaitingSwitch()) throw new IllegalStateException("Waiting for a replacement critter");
            requireActiveTurn(battleId, playerId);
        }

        PlayerState player = currentBattle.getPlayerById(playerId);
        currentBattle.setLastTurnResult(null);
        player.setConsecutiveTimeouts(0);

        sendOut(currentBattle, player, targetCritterIndex);

        if (isReplacingAFaintedCritter) {
            currentBattle.getAwaitingSwitchPlayerIds().remove(playerId);

            // The choice is free, so the player whose turn it is gets a whole
            // one once the last replacement is on the field. If a round came
            // due while it was missing, the replacement's speed orders it.
            if (!currentBattle.isAwaitingSwitch()) {
                currentBattle.setTimeRemaining(currentBattle.getTurnDuration());
                TurnTransitionHandler.resumeRound(currentBattle);
            }

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

        // Cooldowns, stat stages and a claim on the next round are kept by the
        // critter that is on the field, so a critter coming off it stops
        // counting and comes back ready and unshifted. Refreshing a move or shaking off a debuff this
        // way costs the turn the switch itself costs.
        player.getActiveCritter().getAbilityCooldowns().clear();
        player.getActiveCritter().getStats().getStages().clear();
        player.getActiveCritter().setPriority(false);

        player.setActiveCritterIndex(targetCritterIndex);
    }
    
    public void handleTurnTimeout(String battleId) {
        BattleState currentBattle = requireBattle(battleId);

        if (currentBattle.getBattleOutcome() != BattleOutcome.CONTINUE) {
            return;
        }

        currentBattle.setLastTurnResult(null);

        if (currentBattle.isAwaitingSwitch()) {
            sendOutFirstLivingCritters(currentBattle);
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
    private void sendOutFirstLivingCritters(BattleState battleState) {
        for (String playerId : List.copyOf(battleState.getAwaitingSwitchPlayerIds())) {
            PlayerState player = battleState.getPlayerById(playerId);
            int replacementIndex = FaintingService.firstLivingCritterIndex(player);

            if (replacementIndex < 0) continue;

            battleState.getActionLogHistory().add(String.format("%s took too long, so %s is sent out.",
                    player.getUsername(), player.getCritterByIndex(replacementIndex).getName()));
            player.setActiveCritterIndex(replacementIndex);
        }

        battleState.getAwaitingSwitchPlayerIds().clear();
        battleState.setTimeRemaining(battleState.getTurnDuration());
        TurnTransitionHandler.resumeRound(battleState);

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