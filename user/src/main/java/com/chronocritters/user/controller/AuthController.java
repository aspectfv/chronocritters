package com.chronocritters.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.chronocritters.user.dto.LoginRequest;
import com.chronocritters.user.dto.RegisterRequest;
import com.chronocritters.user.dto.User;
import com.chronocritters.user.service.AuthService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("auth/register")
    public String registerUser(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest.username(), registerRequest.password());
    }

    @PostMapping("auth/login")
    public User loginUser(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest.username(), loginRequest.password());
    }
}
