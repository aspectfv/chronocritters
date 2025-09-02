package com.chronocritters.lobby.config;

import org.springframework.context.annotation.Configuration;
import com.chronocritters.lib.config.BaseSecurityConfig;

@Configuration
public class LobbySecurityConfig extends BaseSecurityConfig {
    // Inherits all beans from BaseSecurityConfig
    // Can override methods if needed for lobby-specific customizations
}