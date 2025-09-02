package com.chronocritters.gamelogic.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.chronocritters.lib.exception.BaseReactiveExceptionHandler;

@RestControllerAdvice
public class GameLogicExceptionHandler extends BaseReactiveExceptionHandler {
    // Inherits all exception handling methods from BaseReactiveExceptionHandler
    // Can add service-specific exception handlers here if needed
}