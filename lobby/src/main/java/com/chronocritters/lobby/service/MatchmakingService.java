package com.chronocritters.lobby.service;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.UUID;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.chronocritters.lobby.dto.Match;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchmakingService {
    private final ConcurrentLinkedQueue<String> playerQueue = new ConcurrentLinkedQueue<>();

    public void enqueue(String playerId) {
        playerQueue.add(playerId);
    }

    public Optional<Match> tryMatch() {
        if (playerQueue.size() < 2) {
            return Optional.empty();
        }
        
        String player1 = playerQueue.poll();
        String player2 = playerQueue.poll();
        String battleId = UUID.randomUUID().toString();
        
        return Optional.of(new Match(player1, player2, battleId));
    }
}
