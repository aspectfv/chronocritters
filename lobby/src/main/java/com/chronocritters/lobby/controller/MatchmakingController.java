package com.chronocritters.lobby.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.chronocritters.lobby.client.GameLogicWebClient;
import com.chronocritters.lobby.dto.Match;
import com.chronocritters.lobby.service.BattleTimerService;
import com.chronocritters.lobby.service.MatchmakingService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MatchmakingController {
    private final MatchmakingService matchmakingService;
    private final BattleTimerService battleTimerService;
    private final GameLogicWebClient gameLogicWebClient;
    private final SimpMessagingTemplate messagingTemplate;
    
    
    @MessageMapping("/matchmaking/join")
    public void joinMatchmaking(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        
        if (sessionAttributes == null) {
            throw new IllegalStateException("Session attributes not found");
        }
        
        String userId = (String) sessionAttributes.get("userId");
        String username = (String) sessionAttributes.get("username");
        
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required for matchmaking");
        }
        
        if (username == null) {
            throw new IllegalArgumentException("Username is required for matchmaking");
        }
        
        matchmakingService.enqueue(userId);
        Optional<Match> match = matchmakingService.tryMatch();
        
        if (match.isPresent()) {
            Match foundMatch = match.get();
            
            gameLogicWebClient.createBattle(foundMatch.battleId(), foundMatch.playerOneId(), foundMatch.playerTwoId())
                .then(gameLogicWebClient.getBattleState(foundMatch.battleId()))
                .doOnSuccess(battleTimerService::startOrResetTimer)
                .block();
            
            messagingTemplate.convertAndSendToUser(
                    foundMatch.playerOneId(),
                    "/matchmaking/status",
                    foundMatch
            );
            messagingTemplate.convertAndSendToUser(
                    foundMatch.playerTwoId(),
                    "/matchmaking/status",
                    foundMatch
            );
        }
    }
}
