package com.chronocritters.lobby.controller;

import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.chronocritters.lobby.service.MatchmakingService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MatchmakingController {
    private final MatchmakingService matchmakingService;
    
    @MessageMapping("/matchmaking/join")
    public void joinMatchmaking(SimpMessageHeaderAccessor headerAccessor) {
        // Safe access to session attributes
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        
        if (sessionAttributes == null) {
            return; // Silently ignore invalid requests
        }
        
        String userId = (String) sessionAttributes.get("userId");
        String username = (String) sessionAttributes.get("username");
        
        if (userId == null || username == null) {
            return; // Silently ignore invalid requests
        }
        
        // Simply enqueue the player
        matchmakingService.enqueue(userId);
    }
}
