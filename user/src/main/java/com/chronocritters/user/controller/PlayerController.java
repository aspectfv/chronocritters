package com.chronocritters.user.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import com.chronocritters.lib.model.Player;
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
}