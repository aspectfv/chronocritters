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
        return gameLogicWebClient.getBattle(battleId).block();
    }
    
    public void createBattle(String battleId, String playerOneId, String playerTwoId) {
        gameLogicWebClient.createBattle(battleId, playerOneId, playerTwoId).block();
    }

    public BattleState executeAbility(String battleId, String playerId, String abilityId) {
        return gameLogicWebClient.executeAbility(battleId, playerId, abilityId).block();
    }
}