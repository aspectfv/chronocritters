package com.chronocritters.lib.exception;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseWebSocketExceptionHandler {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @MessageExceptionHandler
    @SendToUser("/error")
    public Map<String, Object> handleException(Exception exception, Principal principal) {
        logger.error("WebSocket error for user {}: {}", 
            principal != null ? principal.getName() : "unknown", 
            exception.getMessage());
        
        return createErrorResponse(exception.getMessage(), null);
    }
    
    @MessageExceptionHandler(IllegalArgumentException.class)
    @SendToUser("/error")
    public Map<String, Object> handleIllegalArgumentException(IllegalArgumentException exception, Principal principal) {
        logger.warn("Invalid argument for user {}: {}", 
            principal != null ? principal.getName() : "unknown", 
            exception.getMessage());
        
        return createErrorResponse(exception.getMessage(), "VALIDATION_ERROR");
    }
    
    @MessageExceptionHandler(RuntimeException.class)
    @SendToUser("/error")
    public Map<String, Object> handleRuntimeException(RuntimeException exception, Principal principal) {
        logger.error("Runtime error for user {}: {}", 
            principal != null ? principal.getName() : "unknown", 
            exception.getMessage());
        
        return createErrorResponse("An unexpected error occurred", "RUNTIME_ERROR");
    }
    
    protected Map<String, Object> createErrorResponse(String message, String type) {
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