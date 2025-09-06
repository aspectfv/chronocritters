package com.chronocritters.lobby.service;

import org.springframework.stereotype.Service;

import com.chronocritters.lobby.client.GameLogicWebClient;
import com.chronocritters.lib.model.BattleState;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BattleStateService {
    private final GameLogicWebClient gameLogicWebClient;
    private final BattleTimerService battleTimerService;
    
    public void createBattle(String battleId, String playerOneId, String playerTwoId) {
        gameLogicWebClient.createBattle(battleId, playerOneId, playerTwoId)
            .then(gameLogicWebClient.getBattleState(battleId))
            .doOnSuccess(battleTimerService::startOrResetTimer)
            .block();
    }

    public BattleState executeAbility(String battleId, String playerId, String abilityId) {
        BattleState newBattleState = gameLogicWebClient.executeAbility(battleId, playerId, abilityId).block();
        if (newBattleState != null) {
            battleTimerService.startOrResetTimer(newBattleState);
        }
        return newBattleState;
    }
}