package com.chronocritters.lobby.exception;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import com.chronocritters.lib.exception.BaseWebSocketExceptionHandler;

@ControllerAdvice
@Controller
public class LobbyExceptionHandler extends BaseWebSocketExceptionHandler {
    // Inherits all WebSocket exception handling methods from BaseWebSocketExceptionHandler
    // Can add lobby-specific exception handlers here if needed
}
