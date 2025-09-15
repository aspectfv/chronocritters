package com.chronocritters.lib.mapper;

import com.chronocritters.lib.model.battle.PlayerState;
import com.chronocritters.proto.player.PlayerProto.PlayerResponse;

public final class PlayerMapper {
    private PlayerMapper() {}

    public static PlayerState toPlayerState(PlayerResponse proto) {
        if (proto == null) throw new IllegalArgumentException("PlayerResponse cannot be null");
        if (proto.getId().isBlank()) throw new IllegalArgumentException("Player ID from PlayerResponse cannot be blank");
        if (proto.getRosterList().isEmpty()) throw new IllegalArgumentException("Roster list from PlayerResponse cannot be empty");

        var battleRoster = proto.getRosterList().stream()
            .map(CritterMapper::toModel)
            .map(CritterMapper::toCritterState)
            .toList();

        return PlayerState.builder()
            .id(proto.getId())
            .username(proto.getUsername())
            .activeCritterIndex(0)
            .roster(battleRoster)
            .build();
    }
}