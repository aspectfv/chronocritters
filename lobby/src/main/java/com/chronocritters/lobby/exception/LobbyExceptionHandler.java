package com.chronocritters.lobby.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Controller
public class LobbyExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(LobbyExceptionHandler.class);

    @MessageExceptionHandler
    @SendToUser("/error")
    public Map<String, Object> handleException(Exception exception, Principal principal) {
        logger.error("WebSocket error for user {}: {}", username(principal), exception.getMessage());

        return errorResponse(exception.getMessage(), null);
    }

    @MessageExceptionHandler(IllegalArgumentException.class)
    @SendToUser("/error")
    public Map<String, Object> handleIllegalArgumentException(IllegalArgumentException exception, Principal principal) {
        logger.warn("Invalid argument for user {}: {}", username(principal), exception.getMessage());

        return errorResponse(exception.getMessage(), "VALIDATION_ERROR");
    }

    @MessageExceptionHandler(RuntimeException.class)
    @SendToUser("/error")
    public Map<String, Object> handleRuntimeException(RuntimeException exception, Principal principal) {
        logger.error("Runtime error for user {}: {}", username(principal), exception.getMessage());

        return errorResponse("An unexpected error occurred", "RUNTIME_ERROR");
    }

    private String username(Principal principal) {
        return principal != null ? principal.getName() : "unknown";
    }

    private Map<String, Object> errorResponse(String message, String type) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());

        if (type != null) {
            errorResponse.put("type", type);
        }

        return errorResponse;
    }
}
