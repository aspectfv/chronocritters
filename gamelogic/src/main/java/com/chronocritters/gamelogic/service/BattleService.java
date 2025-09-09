package com.chronocritters.gamelogic.service;

import java.util.ArrayList;
import java.util.Iterator;
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
import com.chronocritters.gamelogic.event.CritterFaintedEvent;
import com.chronocritters.gamelogic.grpc.PlayerGrpcClient;
import com.chronocritters.lib.context.ApplyEffectContext;
import com.chronocritters.lib.context.ExecuteAbilityContext;
import com.chronocritters.lib.interfaces.AbilityStrategy;
import com.chronocritters.lib.interfaces.EffectStrategy;
import com.chronocritters.lib.mapper.PlayerProtoMapper;
import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.BattleOutcome;
import com.chronocritters.lib.model.AbilityType;
import com.chronocritters.lib.model.ActiveEffect;
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

        PlayerState player = currentBattle.getPlayer();
        PlayerState opponent = currentBattle.getOpponent();

        executePlayerAbility(currentBattle, player, opponent, abilityId);
        
        checkAndPublishFaintEvents(currentBattle);
        if (currentBattle.getBattleOutcome() != BattleOutcome.CONTINUE) {
            applyWinLoss(currentBattle, opponent.getId(), player.getId());
            return currentBattle;
        }

        applyEndOfTurnEffects(currentBattle);

        checkAndPublishFaintEvents(currentBattle);
        if (currentBattle.getBattleOutcome() != BattleOutcome.CONTINUE) {
            boolean playerOneLost = currentBattle.getPlayerOne().getRoster().stream().allMatch(c -> c.getStats().getCurrentHp() <= 0);
            if (playerOneLost) {
                 applyWinLoss(currentBattle, currentBattle.getPlayerTwo().getId(), currentBattle.getPlayerOne().getId());
            } else {
                 applyWinLoss(currentBattle, currentBattle.getPlayerOne().getId(), currentBattle.getPlayerTwo().getId());
            }
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
        if (currentBattle == null) throw new IllegalArgumentException("Invalid battle ID");
        if (!currentBattle.getActivePlayerId().equals(playerId)) throw new IllegalStateException("It's not the player's turn");

        PlayerState player = currentBattle.getPlayer();
        PlayerState opponent = currentBattle.getOpponent();

        if (targetCritterIndex < 0 || targetCritterIndex >= player.getRoster().size()) throw new IllegalArgumentException("Invalid critter index");
        if (targetCritterIndex == player.getActiveCritterIndex()) throw new IllegalArgumentException("Cannot switch to the currently active critter");
        
        CritterState targetCritter = player.getCritterByIndex(targetCritterIndex);
        if (targetCritter.getStats().getCurrentHp() <= 0) throw new IllegalArgumentException("Cannot switch to a fainted critter");

        String switchLog = String.format("%s switched from %s to %s", player.getUsername(),
                player.getActiveCritter().getName(), targetCritter.getName());
        currentBattle.getActionLogHistory().add(switchLog);
        
        player.setActiveCritterIndex(targetCritterIndex);

        applyEndOfTurnEffects(currentBattle);

        checkAndPublishFaintEvents(currentBattle);
        if (currentBattle.getBattleOutcome() != BattleOutcome.CONTINUE) {
            boolean playerOneLost = currentBattle.getPlayerOne().getRoster().stream().allMatch(c -> c.getStats().getCurrentHp() <= 0);
            if (playerOneLost) {
                applyWinLoss(currentBattle, currentBattle.getPlayerTwo().getId(), currentBattle.getPlayerOne().getId());
            } else {
                applyWinLoss(currentBattle, currentBattle.getPlayerOne().getId(), currentBattle.getPlayerTwo().getId());
            }
            return currentBattle;
        }

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

        applyEndOfTurnEffects(currentBattle);

        currentBattle.setActivePlayerId(opponent.getId());
        currentBattle.setTimeRemaining(TURN_DURATION_SECONDS);
        
        player.setHasTurn(false);
        opponent.setHasTurn(true);
        
        lobbyWebClient.updateBattleState(battleId, currentBattle).subscribe();
    }

    private void executePlayerAbility(BattleState battle, PlayerState player, PlayerState opponent, String abilityId) {
        CritterState activeCritter = player.getActiveCritter();
        Ability ability = activeCritter.getAbilityById(abilityId);
        if (ability == null) throw new IllegalArgumentException("Invalid ability ID");

        AbilityStrategy strategy = abilityStrategies.get(ability.getType());
        if (strategy == null) throw new IllegalStateException("No strategy found for ability type: " + ability.getType());

        ExecuteAbilityContext context = ExecuteAbilityContext.builder()
            .battleState(battle).player(player).opponent(opponent)
            .activeCritter(activeCritter).ability(ability).build();
        
        strategy.executeAbility(context);
    }

    private void checkAndPublishFaintEvents(BattleState battleState) {
        checkPlayerFaint(battleState, battleState.getPlayerOne(), battleState.getPlayerTwo());
        checkPlayerFaint(battleState, battleState.getPlayerTwo(), battleState.getPlayerOne());
    }

    private void checkPlayerFaint(BattleState battleState, PlayerState player, PlayerState opponent) {
        for (CritterState critter : player.getRoster()) {
            if (critter.getStats().getCurrentHp() <= 0 && !critter.isFainted()) {
                 critter.setFainted(true);
                 eventPublisher.publishEvent(new CritterFaintedEvent(this, battleState, player, opponent, critter));
            }
        }
    }

    private void applyEndOfTurnEffects(BattleState battleState) {
        for (PlayerState playerState : List.of(battleState.getPlayerOne(), battleState.getPlayerTwo())) {
            for (CritterState critter : playerState.getRoster()) {
                if (critter.getStats().getCurrentHp() > 0) {
                    Iterator<ActiveEffect> iterator = critter.getActiveEffects().iterator();
                    while (iterator.hasNext()) {
                        ActiveEffect effect = iterator.next();
                        EffectStrategy strategy = effectStrategies.get(effect.getType());
                        if (strategy != null) {
                            ApplyEffectContext context = ApplyEffectContext.builder()
                                .battleState(battleState)
                                .targetCritter(critter)
                                .effect(effect)
                                .build();
                            strategy.applyActiveEffect(context);
                        }

                        System.out.println("Remaining duration before decrement: " + effect.getRemainingDuration());
                        
                        effect.setRemainingDuration(effect.getRemainingDuration() - 1);
                        if (effect.getRemainingDuration() <= 0) {
                            String effectEndLog = String.format("The %s on %s wore off.", effect.getName(), critter.getName());
                            battleState.getActionLogHistory().add(effectEndLog);
                            iterator.remove();
                        }
                    }
                }
            }
        }
    }

    private void applyWinLoss(BattleState battleState, String winnerId, String loserId) {
        battleState.setActivePlayerId(null);
        playerGrpcClient.updateMatchHistory(winnerId, loserId);

        lobbyWebClient.updateBattleState(battleState.getBattleId(), battleState).subscribe();
        cleanupScheduler.schedule(() -> {
            BattleState removed = activeBattles.remove(battleState.getBattleId());
            if (removed != null) {
                log.info("Battle {} cleaned up.", battleState.getBattleId());
            }
        }, CLEANUP_DELAY_SECONDS, TimeUnit.SECONDS);
    }
}