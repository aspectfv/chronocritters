package com.chronocritters.lib.dto;

import jakarta.validation.constraints.NotBlank;

public record BattleRequest(
    @NotBlank(message = "playerOneId cannot be empty") String playerOneId,
    @NotBlank(message = "playerTwoId cannot be empty") String playerTwoId
) {}