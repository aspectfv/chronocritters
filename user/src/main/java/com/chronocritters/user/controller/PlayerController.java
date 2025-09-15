package com.chronocritters.user.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import com.chronocritters.lib.model.domain.BaseStats;
import com.chronocritters.lib.model.domain.MatchHistoryEntry;
import com.chronocritters.lib.model.domain.Player;
import com.chronocritters.lib.model.domain.PlayerStats;
import com.chronocritters.lib.util.ExperienceUtil;
import com.chronocritters.user.service.PlayerService;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@Validated
public class PlayerController {
	private final PlayerService playerService;

	@QueryMapping
	public Player getPlayer(
		@Argument 
		@NotBlank(message = "Player ID cannot be empty.")
		String id
	) {
		return playerService.findById(id);
	}
	
	@QueryMapping
	public MatchHistoryEntry getMatchHistoryEntry(
		@Argument
		@NotBlank(message = "Player ID cannot be empty.")
		String playerId,
		@Argument 
		@NotBlank(message = "Battle ID cannot be empty.")
		String battleId
	) {
		return playerService.getMatchHistoryEntry(playerId, battleId);
	}

	@SchemaMapping(typeName = "PlayerStats", field = "expToNextLevel")
	public Long getExpToNextLevel(PlayerStats player) {
		return ExperienceUtil.getRequiredExpForPlayerLevel(player.getLevel() + 1);
	}

	@SchemaMapping(typeName = "BaseStats", field = "expToNextLevel")
	public Long getExpToNextLevel(BaseStats baseStats) {
		return ExperienceUtil.getRequiredExpForCritterLevel(baseStats.getLevel() + 1);
	}
}