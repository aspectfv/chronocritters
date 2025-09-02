package com.chronocritters.lobby.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.chronocritters.lobby.service.BattleStateService;
import com.chronocritters.lib.model.BattleState;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BattleStateController {
    private final BattleStateService battleStateService;
    
    @MessageMapping("/battle/{battleId}/join")
    @SendTo("/topic/battle/{battleId}")
    public BattleState joinBattle(@DestinationVariable String battleId) {
        System.out.println("=== JOIN BATTLE MESSAGE RECEIVED ===");
        System.out.println("Battle ID: " + battleId);
        
        BattleState battleState = battleStateService.getBattleState(battleId);
        System.out.println("Battle state retrieved: " + (battleState != null ? "found" : "null"));
        
        return battleState;
    }

    @MessageMapping("/battle/{battleId}/ability")
    @SendTo("/topic/battle/{battleId}")
    public BattleState executeAbility(@DestinationVariable String battleId, String playerId, String abilityId) {
        System.out.println("=== EXECUTE ABILITY CALLED ===");
        return battleStateService.executeAbility(battleId, playerId, abilityId);
    }
}
