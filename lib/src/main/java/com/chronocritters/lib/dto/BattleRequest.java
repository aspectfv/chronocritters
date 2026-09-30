package com.chronocritters.lib.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

/**
 * Starts a battle between two players, each bringing the critters they picked
 * from their roster. A team is in the order it was picked, and the first one
 * leads.
 */
public record BattleRequest(
    @NotBlank(message = "playerOneId cannot be empty") String playerOneId,
    @NotBlank(message = "playerTwoId cannot be empty") String playerTwoId,
    @NotEmpty(message = "playerOneTeam cannot be empty") List<String> playerOneTeam,
    @NotEmpty(message = "playerTwoTeam cannot be empty") List<String> playerTwoTeam
) {
    /** How many critters a player takes into a battle. */
    public static final int TEAM_SIZE = 3;
}
