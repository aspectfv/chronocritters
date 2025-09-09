package com.chronocritters.gamelogic.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.chronocritters.lib.exception.BaseRestExceptionHandler;

@RestControllerAdvice
public class GameLogicExceptionHandler extends BaseRestExceptionHandler {
    // Inherits all exception handling methods from BaseRestExceptionHandler
    // Can add service-specific exception handlers here if needed
}