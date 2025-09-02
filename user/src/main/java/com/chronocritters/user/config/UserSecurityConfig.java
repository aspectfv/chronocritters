package com.chronocritters.user.config;

import org.springframework.context.annotation.Configuration;
import com.chronocritters.lib.config.BaseSecurityConfig;

@Configuration
public class UserSecurityConfig extends BaseSecurityConfig {
    // Inherits all beans from BaseSecurityConfig
    // Can override methods if needed for user-specific customizations
}