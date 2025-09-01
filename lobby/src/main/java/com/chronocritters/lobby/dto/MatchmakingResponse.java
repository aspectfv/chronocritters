package com.chronocritters.lobby.dto;

public record MatchmakingResponse(
    String status,           // "QUEUED", "MATCH_FOUND"
    String battleId          // null when queued, populated when match found
) {}