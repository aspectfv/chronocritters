package com.chronocritters.lobby.config;

import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.chronocritters.lobby.service.BattleSessionService;
import com.chronocritters.lobby.service.MatchmakingService;
import com.chronocritters.lobby.service.ReconnectGraceService;
import com.chronocritters.lobby.session.StompSession;

import lombok.RequiredArgsConstructor;

/**
 * A closed tab must not leave a ghost in the matchmaking queue or an opponent
 * waiting out turn timers. A pending search is dropped straight away, while a
 * battle in progress goes through {@link ReconnectGraceService} so a refresh is
 * not the same thing as quitting.
 */
@Component
@RequiredArgsConstructor
public class SessionLifecycleListener {
    private final MatchmakingService matchmakingService;
    private final BattleSessionService battleSessionService;
    private final ReconnectGraceService reconnectGraceService;

    @EventListener
    public void onSessionConnected(SessionConnectedEvent event) {
        connectedUserId(event).ifPresent(userId ->
            battleSessionService.battleIdOf(userId).ifPresent(battleId ->
                reconnectGraceService.cancelGrace(userId, battleId)));
    }

    @EventListener
    public void onSessionDisconnect(SessionDisconnectEvent event) {
        StompSession.userId(SimpMessageHeaderAccessor.wrap(event.getMessage())).ifPresent(userId -> {
            matchmakingService.leave(userId);

            battleSessionService.battleIdOf(userId).ifPresent(battleId ->
                reconnectGraceService.startGrace(userId, battleId));
        });
    }

    /**
     * The event carries the outgoing CONNECTED frame, which has no session
     * attributes of its own. The CONNECT it acknowledges is attached to it, and
     * that is the message {@code AuthChannelInterceptor} put the player id on.
     */
    private Optional<String> connectedUserId(SessionConnectedEvent event) {
        Object connectMessage = event.getMessage().getHeaders()
                .get(SimpMessageHeaderAccessor.CONNECT_MESSAGE_HEADER);

        if (!(connectMessage instanceof Message<?> message)) {
            return Optional.empty();
        }

        return StompSession.userId(SimpMessageHeaderAccessor.wrap(message));
    }
}
