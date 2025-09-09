package com.chronocritters.gamelogic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

import com.chronocritters.lib.exception.BaseRestExceptionHandler;

@Configuration
@EnableWebFluxSecurity
public class GameLogicSecurityConfig extends BaseRestExceptionHandler {
    // Inherits reactive security configuration
}
