package com.chronocritters.lib.dto;

import jakarta.validation.constraints.NotBlank;

public record ExecuteAbilityRequest(
    @NotBlank(message = "abilityId cannot be empty") String abilityId
) {}
