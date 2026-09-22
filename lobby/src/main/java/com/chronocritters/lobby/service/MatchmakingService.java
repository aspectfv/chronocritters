package com.chronocritters.lobby.service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.chronocritters.lobby.dto.Match;

/**
 * First-come-first-served matchmaking queue.
 *
 * Joining and pairing happen under a single lock: splitting them let two
 * concurrent joiners interleave and pair with themselves.
 */
@Service
public class MatchmakingService {
    private final Deque<String> playerQueue = new ArrayDeque<>();

    /** Queues the player and returns a match as soon as an opponent is available. */
    public synchronized Optional<Match> join(String playerId) {
        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Player ID cannot be null or empty");
        }

        if (!playerQueue.contains(playerId)) {
            playerQueue.add(playerId);
        }

        if (playerQueue.size() < 2) {
            return Optional.empty();
        }

        String playerOneId = playerQueue.poll();
        String playerTwoId = playerQueue.poll();

        return Optional.of(new Match(playerOneId, playerTwoId, UUID.randomUUID().toString()));
    }

    /** Removes a player who cancelled their search or disconnected. */
    public synchronized void leave(String playerId) {
        playerQueue.remove(playerId);
    }

    public synchronized boolean isQueued(String playerId) {
        return playerQueue.contains(playerId);
    }
}
