package com.chronocritters.gamelogic.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GameLogicExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GameLogicExceptionHandler.class);

    @ExceptionHandler(BattleNotFoundException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleBattleNotFound(BattleNotFoundException ex) {
        logger.warn("Battle not found: {}", ex.getMessage());

        return respond(HttpStatus.NOT_FOUND, ex.getMessage(), "BATTLE_NOT_FOUND");
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidationExceptions(WebExchangeBindException ex) {
        logger.warn("Validation error: {}", ex.getMessage());

        String message = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return respond(HttpStatus.BAD_REQUEST, message, "VALIDATION_ERROR");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Invalid argument: {}", ex.getMessage());

        return respond(HttpStatus.BAD_REQUEST, ex.getMessage(), "VALIDATION_ERROR");
    }

    @ExceptionHandler(IllegalStateException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleIllegalStateException(IllegalStateException ex) {
        logger.warn("Invalid state: {}", ex.getMessage());

        return respond(HttpStatus.FORBIDDEN, ex.getMessage(), "STATE_ERROR");
    }

    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleRuntimeException(RuntimeException ex) {
        logger.error("Runtime error: {}", ex.getMessage());

        return respond(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", "RUNTIME_ERROR");
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleGenericException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage());

        return respond(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", "GENERIC_ERROR");
    }

    private Mono<ResponseEntity<Map<String, Object>>> respond(HttpStatus status, String message, String type) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        errorResponse.put("message", message);
        errorResponse.put("type", type);
        errorResponse.put("timestamp", System.currentTimeMillis());

        return Mono.just(ResponseEntity.status(status).body(errorResponse));
    }
}
