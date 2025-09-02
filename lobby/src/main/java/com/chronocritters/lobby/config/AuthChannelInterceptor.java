package com.chronocritters.lobby.config;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

@Component
public class AuthChannelInterceptor implements ChannelInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthChannelInterceptor.class);

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authHeaders = accessor.getNativeHeader("Authorization");
            
            if (authHeaders == null || authHeaders.isEmpty()) {
                logger.warn("WebSocket connection attempt without Authorization header");
                throw new IllegalArgumentException("Authorization header is required");
            }
            
            String authHeader = authHeaders.get(0);
            
            if (!authHeader.startsWith("Bearer ")) {
                logger.warn("Invalid Authorization header format");
                throw new IllegalArgumentException("Authorization header must start with 'Bearer '");
            }
            
            String token = authHeader.substring(7);
            
            if (token.trim().isEmpty()) {
                throw new IllegalArgumentException("JWT token cannot be empty");
            }
            
            try {
                Claims claims = JwtUtil.validateToken(token);
                String userId = claims.getSubject();
                String username = claims.get("username", String.class);
                
                if (userId == null || userId.trim().isEmpty()) {
                    throw new IllegalArgumentException("JWT token must contain a valid user ID");
                }
                
                if (username == null || username.trim().isEmpty()) {
                    throw new IllegalArgumentException("JWT token must contain a valid username");
                }

                Authentication auth = new UsernamePasswordAuthenticationToken(
                    username, null, List.of()
                );
                
                accessor.setUser(auth);
                
                Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                if (sessionAttributes != null) {
                    sessionAttributes.put("username", username);
                    sessionAttributes.put("userId", userId);
                } else {
                    logger.error("Session attributes are null during WebSocket connection");
                    throw new IllegalStateException("Failed to initialize session attributes");
                }
                
                logger.debug("WebSocket authenticated: {}", username);
                
            } catch (ExpiredJwtException e) {
                logger.warn("Expired JWT token");
                throw new IllegalArgumentException("JWT token has expired");
            } catch (MalformedJwtException | SignatureException e) {
                logger.warn("Invalid JWT token: {}", e.getClass().getSimpleName());
                throw new IllegalArgumentException("JWT token is invalid");
            } catch (IllegalArgumentException | IllegalStateException e) {
                logger.error("JWT validation error: {}", e.getMessage());
                throw new RuntimeException("Authentication failed due to unexpected error");
            }
        }
        
        return message;
    }
}