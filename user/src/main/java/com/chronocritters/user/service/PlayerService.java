package com.chronocritters.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.chronocritters.lib.model.MatchHistoryEntry;
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

    public MatchHistoryEntry getMatchHistoryEntry(String playerId, String battleId) {
        Optional<Player> player = playerRepository.findMatchHistoryEntryByPlayerIdAndBattleId(playerId, battleId);
        if (player.isPresent() && player.get().getMatchHistory() != null && !player.get().getMatchHistory().isEmpty()) {
            return player.get().getMatchHistory().get(0); // only one entry should match
        }
        return null;
    }
}
