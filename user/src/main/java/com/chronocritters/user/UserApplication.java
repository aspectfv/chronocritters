package com.chronocritters.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.chronocritters.lib.config.SharedSecurityConfig;

@SpringBootApplication
@Import(SharedSecurityConfig.class)
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

}
