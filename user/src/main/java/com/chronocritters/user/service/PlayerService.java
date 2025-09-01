package com.chronocritters.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chronocritters.lib.model.Player;
import com.chronocritters.user.repository.PlayerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;

    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    public Player findById(String id) {
        return playerRepository.findById(id).orElse(null);
    }

    public Player save(Player player) {
        return playerRepository.save(player);
    }

    public void deleteById(String id) {
        playerRepository.deleteById(id);
    }
}
