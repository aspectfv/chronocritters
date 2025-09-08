package com.chronocritters.lib.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record SwitchCritterRequest(
    @NotBlank(message = "playerId cannot be empty") String playerId,
    @Min(value = 0, message = "targetCritterIndex must be a positive number") int targetCritterIndex
) {}