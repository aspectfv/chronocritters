package com.chronocritters.lobby.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.chronocritters.lobby.dto.Match;
import com.chronocritters.lobby.service.MatchmakingService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MatchmakingController {
    private final MatchmakingService matchmakingService;
    private final SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/matchmaking/join")
    public void joinMatchmaking(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        
        if (sessionAttributes == null) {
            return;
        }
        
        String userId = (String) sessionAttributes.get("userId");
        String username = (String) sessionAttributes.get("username");
        
        if (userId == null || username == null) {
            return;
        }
        
        matchmakingService.enqueue(userId);
        
        // Try to find a match
        Optional<Match> match = matchmakingService.tryMatch();
        
        if (match.isPresent()) {
            Match foundMatch = match.get();
            
            messagingTemplate.convertAndSend(
                "/topic/battle/" + foundMatch.battleId(), foundMatch
            );
        }
    }
}
