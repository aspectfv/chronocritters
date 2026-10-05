package com.chronocritters.lobby;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.chronocritters.lib.config.SharedSecurityConfig;

@SpringBootApplication
@Import(SharedSecurityConfig.class)
public class LobbyApplication {

	public static void main(String[] args) {
		SpringApplication.run(LobbyApplication.class, args);
	}

}
