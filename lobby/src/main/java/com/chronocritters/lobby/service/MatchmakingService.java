package com.chronocritters.lobby.service;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Service;

@Service
public class MatchmakingService {
    private final ConcurrentLinkedQueue<String> playerQueue = new ConcurrentLinkedQueue<>();

    public void enqueue(String playerId) {
        playerQueue.add(playerId);
    }

    public String dequeue() {
        return playerQueue.poll();
    }

    public int size() {
        return playerQueue.size();
    }
}
