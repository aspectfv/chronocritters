package com.chronocritters.user.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.chronocritters.user.dto.LoginResponse;
import com.chronocritters.user.service.AuthService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@MutationMapping
	public LoginResponse register(@Argument String username, @Argument String password) {
		return authService.register(username, password);
	}

	@MutationMapping
	public LoginResponse login(@Argument String username, @Argument String password) {
		return authService.login(username, password);
	}
}