package com.chronocritters.user.auth.dto;

public record LoginResponse(User user, String token) {}
