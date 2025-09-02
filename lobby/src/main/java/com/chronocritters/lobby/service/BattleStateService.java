package com.chronocritters.lobby.service;

import org.springframework.stereotype.Service;

import com.chronocritters.lobby.client.GameLogicWebClient;
import com.chronocritters.lib.model.BattleState;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BattleStateService {
    private final GameLogicWebClient gameLogicWebClient;
    
    public BattleState getBattleState(String battleId) {
        try {
            return gameLogicWebClient.getBattle(battleId).block();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve battle state for battleId: " + battleId, e);
        }
    }
    
    public void createBattle(String battleId, String playerOneId, String playerTwoId) {
        try {
            gameLogicWebClient.createBattle(battleId, playerOneId, playerTwoId).block();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create battle with battleId: " + battleId, e);
        }
    }

    public BattleState executeAbility(String battleId, String playerId, String abilityId) {
        try {
            return gameLogicWebClient.executeAbility(battleId, playerId, abilityId).block();
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute ability for battleId: " + battleId, e);
        }
    }
}