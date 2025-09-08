package com.chronocritters.lobby.service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Service;

import com.chronocritters.lobby.dto.Match;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchmakingService {
    private final ConcurrentLinkedQueue<String> playerQueue = new ConcurrentLinkedQueue<>();

    public void enqueue(String playerId) {
        if (playerId == null || playerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Player ID cannot be null or empty");
        }
        
        if (playerQueue.contains(playerId)) {
            throw new IllegalStateException("Player is already in the matchmaking queue");
        }
        
        playerQueue.add(playerId);
    }

    public Optional<Match> tryMatch() {
        if (playerQueue.size() < 2) {
            return Optional.empty();
        }
        
        String playerOneId = playerQueue.poll();
        String playerTwoId = playerQueue.poll();
        
        if (playerOneId == null || playerTwoId == null) {
            throw new RuntimeException("Failed to retrieve players from queue");
        }
        
        if (playerOneId.equals(playerTwoId)) {
            throw new IllegalStateException("Cannot match a player with themselves");
        }
        
        String battleId = UUID.randomUUID().toString();

        return Optional.of(new Match(playerOneId, playerTwoId, battleId));
    }
}
