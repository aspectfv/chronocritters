package com.chronocritters.user.controller;

import org.springframework.web.bind.annotation.RestController;

import com.chronocritters.user.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.chronocritters.user.dto.LoginRequest;
import com.chronocritters.user.dto.RegisterRequest;


@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("auth/register")
    public String registerUser(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest.username(), registerRequest.password());
    }

    @PostMapping("auth/login")
    public String loginUser(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest.username(), loginRequest.password());
    }
}
