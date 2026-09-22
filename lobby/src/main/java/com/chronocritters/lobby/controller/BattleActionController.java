package com.chronocritters.lobby.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.chronocritters.lobby.client.GameLogicWebClient;
import com.chronocritters.lobby.session.StompSession;

import lombok.RequiredArgsConstructor;

/**
 * Forfeits travel over the WebSocket rather than the battle REST API, because
 * the socket connection is already authenticated. That keeps the gamelogic
 * forfeit endpoint internal and gives a voluntary forfeit and a disconnect
 * forfeit a single code path.
 */
@Controller
@RequiredArgsConstructor
public class BattleActionController {
    private final GameLogicWebClient gameLogicWebClient;

    @MessageMapping("/battle/{battleId}/forfeit")
    public void forfeit(@DestinationVariable String battleId, SimpMessageHeaderAccessor headerAccessor) {
        gameLogicWebClient.forfeit(battleId, StompSession.requireUserId(headerAccessor)).block();
    }
}
