package com.chronocritters.lobby.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import com.chronocritters.lobby.service.BattleStateService;
import com.chronocritters.lib.model.BattleState;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BattleStateController {
    private final BattleStateService battleStateService;
    
    @SubscribeMapping("/topic/battle/{battleId}")
    public BattleState subscribeToBattle(@DestinationVariable String battleId) {
        return battleStateService.getBattleState(battleId);
    }

    @MessageMapping("/battle/{battleId}/ability")
    @SendTo("/topic/battle/{battleId}")
    public BattleState executeAbility(@DestinationVariable String battleId, String playerId, String abilityId) {
        return battleStateService.executeAbility(battleId, playerId, abilityId);
    }
}
