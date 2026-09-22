package com.chronocritters.lobby.service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.chronocritters.lobby.dto.Match;

/**
 * Remembers which battle each player is currently in, so a disconnect can be
 * turned into a forfeit for the right battle.
 */
@Service
public class BattleSessionService {
    private final Map<String, String> battleIdByPlayerId = new ConcurrentHashMap<>();

    public void register(Match match) {
        battleIdByPlayerId.put(match.playerOneId(), match.battleId());
        battleIdByPlayerId.put(match.playerTwoId(), match.battleId());
    }

    public Optional<String> battleIdOf(String playerId) {
        return Optional.ofNullable(battleIdByPlayerId.get(playerId));
    }

    public void clear(String battleId) {
        battleIdByPlayerId.values().removeIf(battleId::equals);
    }
}
