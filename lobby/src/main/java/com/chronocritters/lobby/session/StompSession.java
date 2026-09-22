package com.chronocritters.lobby.session;

import java.util.Map;
import java.util.Optional;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

/** Reads the identity that AuthChannelInterceptor put on the STOMP session. */
public final class StompSession {
    public static final String USER_ID = "userId";
    public static final String USERNAME = "username";

    private StompSession() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Optional<String> userId(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null) {
            return Optional.empty();
        }

        return Optional.ofNullable((String) sessionAttributes.get(USER_ID))
                .filter(userId -> !userId.isBlank());
    }

    public static String requireUserId(SimpMessageHeaderAccessor headerAccessor) {
        return userId(headerAccessor)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user ID is required"));
    }
}
