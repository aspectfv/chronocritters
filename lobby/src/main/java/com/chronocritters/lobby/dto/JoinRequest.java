package com.chronocritters.lobby.dto;

import java.util.List;

/** The critters a player queues with, in the order they picked them. */
public record JoinRequest(List<String> team) {}
