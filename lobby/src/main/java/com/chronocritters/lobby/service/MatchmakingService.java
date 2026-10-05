package com.chronocritters.lobby.service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.chronocritters.lib.dto.BattleRequest;
import com.chronocritters.lobby.dto.Match;
import com.chronocritters.lobby.dto.Pairing;

/**
 * First-come-first-served matchmaking queue. Each player queues with the team
 * they picked, and a player who queues again replaces it.
 *
 * Joining and pairing happen under a single lock: splitting them let two
 * concurrent joiners interleave and pair with themselves.
 *
 * Only the shape of a team is checked here. Whether the player owns those
 * critters is checked by gamelogic, which reads the roster when the battle
 * starts.
 */
@Service
public class MatchmakingService {
    private final Deque<String> playerQueue = new ArrayDeque<>();
    private final Map<String, List<String>> teams = new HashMap<>();

    /** Queues the player and returns a pairing as soon as an opponent is available. */
    public synchronized Optional<Pairing> join(String playerId, List<String> team) {
        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Player ID cannot be null or empty");
        }
        if (team == null || team.isEmpty() || team.size() > BattleRequest.TEAM_SIZE || Set.copyOf(team).size() != team.size()) {
            throw new IllegalArgumentException("A team is up to " + BattleRequest.TEAM_SIZE + " different critters");
        }

        teams.put(playerId, List.copyOf(team));
        if (!playerQueue.contains(playerId)) {
            playerQueue.add(playerId);
        }

        if (playerQueue.size() < 2) {
            return Optional.empty();
        }

        String playerOneId = playerQueue.poll();
        String playerTwoId = playerQueue.poll();

        return Optional.of(new Pairing(
                new Match(playerOneId, playerTwoId, UUID.randomUUID().toString()),
                new BattleRequest(playerOneId, playerTwoId, teams.remove(playerOneId), teams.remove(playerTwoId))));
    }

    /** Removes a player who cancelled their search or disconnected. */
    public synchronized void leave(String playerId) {
        playerQueue.remove(playerId);
        teams.remove(playerId);
    }

    public synchronized boolean isQueued(String playerId) {
        return playerQueue.contains(playerId);
    }
}
