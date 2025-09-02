package com.chronocritters.lobby.service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Service;

import com.chronocritters.lobby.dto.Match;

@Service
public class MatchmakingService {
    private final ConcurrentLinkedQueue<String> playerQueue = new ConcurrentLinkedQueue<>();

    public void enqueue(String playerId) {
        playerQueue.add(playerId);
    }

    public Optional<Match> tryMatch() {
        if (playerQueue.size() < 2) {
            return Optional.empty();
        }
        
        String playerOneId = playerQueue.poll();
        String playerTwoId = playerQueue.poll();
        String battleId = UUID.randomUUID().toString();

        return Optional.of(new Match(playerOneId, playerTwoId, battleId));
    }
}
