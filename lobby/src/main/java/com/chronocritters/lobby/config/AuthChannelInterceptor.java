package com.chronocritters.lobby.config;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.chronocritters.lib.util.JwtUtil;
import io.jsonwebtoken.Claims;

import java.util.List;
import java.util.Map;

@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authHeaders = accessor.getNativeHeader("Authorization");
            
            if (authHeaders != null && !authHeaders.isEmpty()) {
                String authHeader = authHeaders.get(0);
                
                if (authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    
                    try {
                        Claims claims = JwtUtil.validateToken(token);
                        String username = claims.get("username", String.class);
                        String userId = claims.getSubject();
                        
                        Authentication auth = new UsernamePasswordAuthenticationToken(
                            username, null, List.of()
                        );
                        
                        accessor.setUser(auth);
                        
                        // Safe access to session attributes
                        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                        if (sessionAttributes != null) {
                            sessionAttributes.put("username", username);
                            sessionAttributes.put("userId", userId);
                        }
                        
                    } catch (Exception e) {
                        throw new RuntimeException("Invalid JWT token");
                    }
                }
            }
        }
        
        return message;
    }
}