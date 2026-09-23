package com.chronocritters.gamelogic.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.chronocritters.lib.exception.BaseReactiveExceptionHandler;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GameLogicExceptionHandler extends BaseReactiveExceptionHandler {

    @ExceptionHandler(BattleNotFoundException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleBattleNotFound(BattleNotFoundException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse(ex.getMessage(), "BATTLE_NOT_FOUND")));
    }
}
