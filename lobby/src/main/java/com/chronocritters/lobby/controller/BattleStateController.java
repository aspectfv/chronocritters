package com.chronocritters.lobby.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.chronocritters.lobby.service.BattleStateService;
import com.chronocritters.lib.dto.ExecuteAbilityRequest;
import com.chronocritters.lib.model.BattleState;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BattleStateController {
    private final BattleStateService battleStateService;

    @MessageMapping("/battle/{battleId}/ability")
    @SendTo("/topic/battle/{battleId}")
    public BattleState executeAbility(@DestinationVariable String battleId, @Payload ExecuteAbilityRequest request) {
        return battleStateService.executeAbility(battleId, request.playerId(), request.abilityId());
    }
}
