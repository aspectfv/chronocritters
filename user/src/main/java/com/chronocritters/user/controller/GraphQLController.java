package com.chronocritters.user.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.chronocritters.lib.model.Player;
import com.chronocritters.user.service.PlayerService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class GraphQLController {
	private final PlayerService playerService;

	@QueryMapping
	public Player getPlayer(@Argument String id) {
		return playerService.findById(id);
	}
}