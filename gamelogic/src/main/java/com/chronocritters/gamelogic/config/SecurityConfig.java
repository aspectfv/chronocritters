package com.chronocritters.gamelogic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

import com.chronocritters.lib.config.BaseReactiveSecurityConfig;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig extends BaseReactiveSecurityConfig {
    // Inherits reactive security configuration
}
