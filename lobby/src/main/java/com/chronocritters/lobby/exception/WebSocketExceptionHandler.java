package com.chronocritters.lobby.exception;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Controller
public class WebSocketExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketExceptionHandler.class);
    
    @MessageExceptionHandler
    @SendToUser("/error")
    public Map<String, Object> handleException(Exception exception, Principal principal) {
        logger.error("WebSocket error for user {}: {}", 
            principal != null ? principal.getName() : "unknown", 
            exception.getMessage(), exception);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        errorResponse.put("message", exception.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        return errorResponse;
    }
    
    @MessageExceptionHandler(IllegalArgumentException.class)
    @SendToUser("/error")
    public Map<String, Object> handleIllegalArgumentException(IllegalArgumentException exception, Principal principal) {
        logger.warn("Invalid argument for user {}: {}", 
            principal != null ? principal.getName() : "unknown", 
            exception.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        errorResponse.put("message", "Invalid request: " + exception.getMessage());
        errorResponse.put("type", "VALIDATION_ERROR");
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        return errorResponse;
    }
    
    @MessageExceptionHandler(RuntimeException.class)
    @SendToUser("/error")
    public Map<String, Object> handleRuntimeException(RuntimeException exception, Principal principal) {
        logger.error("Runtime error for user {}: {}", 
            principal != null ? principal.getName() : "unknown", 
            exception.getMessage(), exception);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        errorResponse.put("message", "An unexpected error occurred");
        errorResponse.put("type", "RUNTIME_ERROR");
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        return errorResponse;
    }
}
