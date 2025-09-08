package com.chronocritters.lib.dto;

import jakarta.validation.constraints.NotBlank;

public record ExecuteAbilityRequest(
    @NotBlank(message = "playerId cannot be empty") String playerId,
    @NotBlank(message = "abilityId cannot be empty") String abilityId
) {}