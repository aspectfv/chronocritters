package com.chronocritters.lobby.dto;

import com.chronocritters.lib.dto.BattleRequest;

/**
 * Two queued players, paired. The match is what both clients are told; the
 * request, which carries the teams they queued with, is what gamelogic is
 * asked to start.
 */
public record Pairing(Match match, BattleRequest battleRequest) {}
