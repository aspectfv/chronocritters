package com.chronocritters.lib.dto;

import jakarta.validation.constraints.Min;

public record SwitchCritterRequest(
    @Min(value = 0, message = "targetCritterIndex must be a positive number") int targetCritterIndex
) {}
