package com.chronocritters.user.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import com.chronocritters.user.dto.LoginResponse;
import com.chronocritters.user.service.AuthService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@Validated
public class AuthController {
	private final AuthService authService;

	@MutationMapping
	public LoginResponse register(
			@Argument @NotBlank(message = "Username cannot be empty") 
			@Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters") 
			String username,
			
			@Argument @NotBlank(message = "Password cannot be empty") 
			@Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters") 
			String password
	) {
		return authService.register(username, password);
	}

	@MutationMapping
	public LoginResponse login(
			@Argument @NotBlank(message = "Username cannot be empty") 
			String username, 
			
			@Argument @NotBlank(message = "Password cannot be empty") 
			String password
	) {
		return authService.login(username, password);
	}
}