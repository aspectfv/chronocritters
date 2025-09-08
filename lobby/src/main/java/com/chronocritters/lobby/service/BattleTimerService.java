// File: lobby/src/main/java/com/chronocritters/lobby/service/BattleTimerService.java
package com.chronocritters.lobby.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lobby.client.GameLogicWebClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BattleTimerService {
    private final SimpMessagingTemplate messagingTemplate;
    private final GameLogicWebClient gameLogicWebClient;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ConcurrentHashMap<String, ScheduledFuture<?>> activeTimers = new ConcurrentHashMap<>();
    
    private final Logger log = LoggerFactory.getLogger(getClass());

    public void startOrResetTimer(BattleState battleState) {
        String battleId = battleState.getBattleId();
        stopTimer(battleId);

        if (battleState.getActivePlayerId() == null) {
            return;
        }

        final AtomicInteger timeRemaining = new AtomicInteger(battleState.getTimeRemaining());

        final Runnable timerTickTask = () -> {
            int remaining = timeRemaining.decrementAndGet();
            
            messagingTemplate.convertAndSend("/topic/battle/" + battleId, Map.of("timeRemaining", remaining));

            if (remaining <= 0) {
                stopTimer(battleId);
                gameLogicWebClient.handleTurnTimeout(battleId)
                    .doOnError(error -> log.warn("Failed to handle turn timeout for battle {}", battleId))
                    .subscribe();
            }
        };

        ScheduledFuture<?> timerHandle = scheduler.scheduleAtFixedRate(timerTickTask, 1, 1, TimeUnit.SECONDS);
        activeTimers.put(battleId, timerHandle);
    }

    public void stopTimer(String battleId) {
        ScheduledFuture<?> timerHandle = activeTimers.remove(battleId);
        if (timerHandle != null) {
            timerHandle.cancel(true);
        }
    }
}