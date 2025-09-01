package com.chronocritters.lobby.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import com.chronocritters.lobby.service.MatchmakingService;
import com.chronocritters.lobby.dto.Match;
import com.chronocritters.lobby.dto.MatchmakingResponse;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MatchmakingController {
    private final MatchmakingService matchmakingService;
    private final SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/matchmaking/join")
    @SendToUser("/matchmaking")
    public MatchmakingResponse joinMatchmaking(SimpMessageHeaderAccessor headerAccessor) {
        // Safe access to session attributes
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        
        if (sessionAttributes == null) {
            return new MatchmakingResponse("ERROR", null);
        }
        
        String userId = (String) sessionAttributes.get("userId");
        String username = (String) sessionAttributes.get("username");
        
        if (userId == null || username == null) {
            return new MatchmakingResponse("ERROR", null);
        }
        
        // Add player to queue
        matchmakingService.enqueue(userId);
        
        // Try to find a match
        Optional<Match> match = matchmakingService.tryMatch();

        if (match.isPresent()) {
            // Match found - send notifications to both players
            MatchmakingResponse matchResponse = new MatchmakingResponse(
                "MATCH_FOUND", 
                match.get().battleId()
            );
            
            // Send to both matched players
            messagingTemplate.convertAndSendToUser(
                match.get().player1(), "/matchmaking", matchResponse);
            messagingTemplate.convertAndSendToUser(
                match.get().player2(), "/matchmaking", matchResponse);
            
            // The current player won't get the queued response since they were matched
            return null;
        }
        
        // No match found - return queue status
        return new MatchmakingResponse("QUEUED", null);
    }
}
