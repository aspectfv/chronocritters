package com.chronocritters.lib.dto;

import jakarta.validation.constraints.NotBlank;

public record ForfeitRequest(
    @NotBlank(message = "playerId cannot be empty") String playerId
) {}
