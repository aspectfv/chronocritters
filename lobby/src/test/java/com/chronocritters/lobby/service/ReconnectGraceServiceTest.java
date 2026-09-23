package com.chronocritters.lobby.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.chronocritters.lobby.client.GameLogicWebClient;

import reactor.core.publisher.Mono;

class ReconnectGraceServiceTest {

    private static final String PLAYER_ID = "p1";
    private static final String BATTLE_ID = "battle-1";
    private static final String TOPIC = "/topic/battle/" + BATTLE_ID;

    private GameLogicWebClient gameLogicWebClient;
    private SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    void setUp() {
        gameLogicWebClient = mock(GameLogicWebClient.class);
        messagingTemplate = mock(SimpMessagingTemplate.class);
        when(gameLogicWebClient.forfeit(anyString(), anyString())).thenReturn(Mono.empty());
    }

    private ReconnectGraceService serviceWithGrace(int graceSeconds) {
        return new ReconnectGraceService(gameLogicWebClient, messagingTemplate, graceSeconds);
    }

    @Test
    @DisplayName("a player who never comes back forfeits once the window runs out")
    void forfeitsWhenTheWindowExpires() {
        serviceWithGrace(0).startGrace(PLAYER_ID, BATTLE_ID);

        verify(gameLogicWebClient, timeout(2000)).forfeit(BATTLE_ID, PLAYER_ID);
    }

    @Test
    @DisplayName("reconnecting inside the window keeps the battle alive")
    void reconnectingCancelsTheForfeit() throws InterruptedException {
        ReconnectGraceService reconnectGraceService = serviceWithGrace(30);

        reconnectGraceService.startGrace(PLAYER_ID, BATTLE_ID);
        reconnectGraceService.cancelGrace(PLAYER_ID, BATTLE_ID);

        Thread.sleep(200);
        verify(gameLogicWebClient, never()).forfeit(anyString(), anyString());
        assertThat(reconnectGraceService.isAwaitingReconnect(PLAYER_ID)).isFalse();
    }

    @Test
    @DisplayName("the opponent is told who dropped and how long they have")
    void announcesTheDisconnect() {
        serviceWithGrace(30).startGrace(PLAYER_ID, BATTLE_ID);

        assertThat(capturedFrame())
                .containsEntry("disconnectedPlayerId", PLAYER_ID)
                .containsEntry("reconnectSecondsRemaining", 30);
    }

    @Test
    @DisplayName("the frame that clears the banner carries an explicit null, not a missing key")
    void clearsTheBannerOnReturn() {
        ReconnectGraceService reconnectGraceService = serviceWithGrace(30);

        reconnectGraceService.startGrace(PLAYER_ID, BATTLE_ID);
        reconnectGraceService.cancelGrace(PLAYER_ID, BATTLE_ID);

        Map<String, Object> frame = capturedFrame();
        assertThat(frame).containsKey("disconnectedPlayerId");
        assertThat(frame.get("disconnectedPlayerId")).isNull();
    }

    @Test
    @DisplayName("cancelling for a player who never dropped announces nothing")
    void cancellingWithoutADisconnectIsSilent() {
        serviceWithGrace(30).cancelGrace(PLAYER_ID, BATTLE_ID);

        verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> capturedFrame() {
        ArgumentCaptor<Object> frame = ArgumentCaptor.forClass(Object.class);
        verify(messagingTemplate, org.mockito.Mockito.atLeastOnce()).convertAndSend(eq(TOPIC), frame.capture());
        return (Map<String, Object>) frame.getValue();
    }
}
