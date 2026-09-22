package com.chronocritters.lobby.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.chronocritters.lobby.client.GameLogicWebClient;
import com.chronocritters.lobby.service.BattleSessionService;
import com.chronocritters.lobby.service.MatchmakingService;
import com.chronocritters.lobby.session.StompSession;

import lombok.RequiredArgsConstructor;

/**
 * A closed tab must not leave a ghost in the matchmaking queue or an opponent
 * waiting out turn timers, so a dropped connection cancels any pending search
 * and forfeits any battle in progress.
 */
@Component
@RequiredArgsConstructor
public class SessionDisconnectListener {
    private static final Logger logger = LoggerFactory.getLogger(SessionDisconnectListener.class);

    private final MatchmakingService matchmakingService;
    private final BattleSessionService battleSessionService;
    private final GameLogicWebClient gameLogicWebClient;

    @EventListener
    public void onSessionDisconnect(SessionDisconnectEvent event) {
        StompSession.userId(SimpMessageHeaderAccessor.wrap(event.getMessage())).ifPresent(userId -> {
            matchmakingService.leave(userId);

            battleSessionService.battleIdOf(userId).ifPresent(battleId -> {
                logger.info("Player {} disconnected during battle {}; forfeiting.", userId, battleId);
                gameLogicWebClient.forfeit(battleId, userId).subscribe();
            });
        });
    }
}
