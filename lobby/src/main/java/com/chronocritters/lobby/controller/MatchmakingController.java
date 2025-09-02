package com.chronocritters.lobby.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.chronocritters.lobby.dto.Match;
import com.chronocritters.lobby.service.BattleStateService;
import com.chronocritters.lobby.service.MatchmakingService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MatchmakingController {
    private final MatchmakingService matchmakingService;
    private final SimpMessagingTemplate messagingTemplate;
    private final BattleStateService battleStateService;
    
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
        
        // This will throw exceptions that the global handler will catch
        matchmakingService.enqueue(userId);
        
        Optional<Match> match = matchmakingService.tryMatch();
        
        if (match.isPresent()) {
            Match foundMatch = match.get();
            
            // Create battle - this could also throw exceptions
            battleStateService.createBattle(
                foundMatch.battleId(),
                foundMatch.playerOneId(),
                foundMatch.playerTwoId()
            );
            
            // Notify players
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
