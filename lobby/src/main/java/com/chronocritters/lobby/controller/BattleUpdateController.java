package com.chronocritters.lobby.controller;

import org.springframework.web.bind.annotation.RestController;

import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lobby.service.BattleTimerService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
public class BattleUpdateController {
    private final BattleTimerService battleTimerService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/battle/{battleId}/update")
    public ResponseEntity<Void> onBattleStateUpdate(@PathVariable String battleId, @RequestBody BattleState battleState) {
        messagingTemplate.convertAndSend("/topic/battle/" + battleId, battleState);
        if (battleState.getActivePlayerId() != null) {
            battleTimerService.startOrResetTimer(battleState);
        } else {
            battleTimerService.stopTimer(battleId);
        }
        return ResponseEntity.ok().build();
    }
    
}
