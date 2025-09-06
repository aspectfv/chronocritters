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
    
    public BattleState getBattleState(String battleId) {
        BattleState battleState = gameLogicWebClient.getBattle(battleId).block();
        if (battleState != null) {
            battleTimerService.startOrResetTimer(battleState);
        }
        return battleState;
    }
    
    public void createBattle(String battleId, String playerOneId, String playerTwoId) {
        gameLogicWebClient.createBattle(battleId, playerOneId, playerTwoId)
            .then(gameLogicWebClient.getBattle(battleId))
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