package com.chronocritters.lobby.config;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.chronocritters.lobby.service.BattleSessionService;
import com.chronocritters.lobby.service.MatchmakingService;
import com.chronocritters.lobby.service.ReconnectGraceService;
import com.chronocritters.lobby.session.StompSession;

/**
 * The identity on these two events is carried differently, and getting it wrong
 * fails silently: the listener simply never runs. Both shapes are pinned here.
 */
class SessionLifecycleListenerTest {

    private static final String USER_ID = "p1";
    private static final String BATTLE_ID = "battle-1";

    private MatchmakingService matchmakingService;
    private BattleSessionService battleSessionService;
    private ReconnectGraceService reconnectGraceService;
    private SessionLifecycleListener listener;

    @BeforeEach
    void setUp() {
        matchmakingService = mock(MatchmakingService.class);
        battleSessionService = mock(BattleSessionService.class);
        reconnectGraceService = mock(ReconnectGraceService.class);
        listener = new SessionLifecycleListener(matchmakingService, battleSessionService, reconnectGraceService);

        when(battleSessionService.battleIdOf(USER_ID)).thenReturn(Optional.of(BATTLE_ID));
    }

    private Message<byte[]> messageWithSessionUser(SimpMessageType type, String userId) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(type);
        accessor.setSessionId("session-1");
        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(StompSession.USER_ID, userId);
        accessor.setSessionAttributes(sessionAttributes);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    /** The CONNECTED frame has no session attributes; the CONNECT it acknowledges is attached to it. */
    private SessionConnectedEvent connectedEvent(String userId) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(SimpMessageType.CONNECT_ACK);
        accessor.setSessionId("session-1");
        accessor.setHeader(SimpMessageHeaderAccessor.CONNECT_MESSAGE_HEADER,
                messageWithSessionUser(SimpMessageType.CONNECT, userId));
        return new SessionConnectedEvent(this, MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders()));
    }

    @Test
    @DisplayName("reconnecting cancels the countdown on the battle the player was in")
    void reconnectingCancelsTheCountdown() {
        listener.onSessionConnected(connectedEvent(USER_ID));

        verify(reconnectGraceService).cancelGrace(USER_ID, BATTLE_ID);
    }

    @Test
    @DisplayName("a CONNECTED frame with no CONNECT attached is ignored rather than throwing")
    void ignoresAConnectedFrameWithoutItsConnect() {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(SimpMessageType.CONNECT_ACK);
        accessor.setSessionId("session-1");

        listener.onSessionConnected(new SessionConnectedEvent(this,
                MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders())));

        verify(reconnectGraceService, never()).cancelGrace(anyString(), anyString());
    }

    @Test
    @DisplayName("dropping out leaves the queue and starts the countdown")
    void droppingOutStartsTheCountdown() {
        listener.onSessionDisconnect(new SessionDisconnectEvent(this,
                messageWithSessionUser(SimpMessageType.DISCONNECT, USER_ID), "session-1", CloseStatus.NORMAL));

        verify(matchmakingService).leave(USER_ID);
        verify(reconnectGraceService).startGrace(USER_ID, BATTLE_ID);
    }

    @Test
    @DisplayName("a player who was not in a battle only leaves the queue")
    void anIdlePlayerOnlyLeavesTheQueue() {
        when(battleSessionService.battleIdOf("p9")).thenReturn(Optional.empty());

        listener.onSessionDisconnect(new SessionDisconnectEvent(this,
                messageWithSessionUser(SimpMessageType.DISCONNECT, "p9"), "session-9", CloseStatus.NORMAL));

        verify(matchmakingService).leave("p9");
        verify(reconnectGraceService, never()).startGrace(anyString(), anyString());
    }
}
