package com.chronocritters.lobby.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.chronocritters.lobby.client.GameLogicWebClient;

/**
 * A dropped WebSocket is usually a refresh or a phone locking its screen, not a
 * player walking away, so a disconnect starts a countdown rather than forfeiting
 * outright. Reconnecting inside the window cancels it; running out of window
 * hands the win to the opponent, which is what the old immediate forfeit did.
 */
@Service
public class ReconnectGraceService {
    private static final Logger logger = LoggerFactory.getLogger(ReconnectGraceService.class);

    private final GameLogicWebClient gameLogicWebClient;
    private final SimpMessagingTemplate messagingTemplate;
    private final int graceSeconds;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Map<String, ScheduledFuture<?>> pendingForfeits = new ConcurrentHashMap<>();

    public ReconnectGraceService(GameLogicWebClient gameLogicWebClient,
                                 SimpMessagingTemplate messagingTemplate,
                                 @Value("${battle.reconnect-grace-seconds}") int graceSeconds) {
        this.gameLogicWebClient = gameLogicWebClient;
        this.messagingTemplate = messagingTemplate;
        this.graceSeconds = graceSeconds;
    }

    /** Starts the countdown and tells the other player why the board has stalled. */
    public void startGrace(String playerId, String battleId) {
        cancelPending(playerId);

        logger.info("Player {} dropped out of battle {}; forfeiting in {}s unless they return.",
                playerId, battleId, graceSeconds);

        broadcast(battleId, playerId, graceSeconds);

        ScheduledFuture<?> forfeit = scheduler.schedule(() -> {
            pendingForfeits.remove(playerId);
            logger.info("Player {} did not return to battle {}; forfeiting.", playerId, battleId);
            gameLogicWebClient.forfeit(battleId, playerId).subscribe();
        }, graceSeconds, TimeUnit.SECONDS);

        pendingForfeits.put(playerId, forfeit);
    }

    /** Called when the player reconnects, so the board can resume. */
    public void cancelGrace(String playerId, String battleId) {
        if (cancelPending(playerId)) {
            logger.info("Player {} returned to battle {} inside the grace window.", playerId, battleId);
            broadcast(battleId, null, 0);
        }
    }

    public boolean isAwaitingReconnect(String playerId) {
        return pendingForfeits.containsKey(playerId);
    }

    private boolean cancelPending(String playerId) {
        ScheduledFuture<?> pending = pendingForfeits.remove(playerId);
        if (pending == null) {
            return false;
        }
        pending.cancel(false);
        return true;
    }

    private void broadcast(String battleId, String disconnectedPlayerId, int secondsRemaining) {
        // The key is always present, null included: the client merges frames onto
        // the state it already has, so omitting it would leave the banner up.
        Map<String, Object> frame = new HashMap<>();
        frame.put("disconnectedPlayerId", disconnectedPlayerId);
        frame.put("reconnectSecondsRemaining", secondsRemaining);
        messagingTemplate.convertAndSend("/topic/battle/" + battleId, frame);
    }
}
