package com.chronocritters.lobby.controller;

import org.springframework.web.bind.annotation.RestController;

import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lobby.service.BattleTimerService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
public class BattleUpdateController {
    private final BattleTimerService battleTimerService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/battle/{battleId}/update")
    public ResponseEntity<Void> onBattleStateUpdate(@RequestBody BattleState battleState) {
        messagingTemplate.convertAndSend("/topic/battle/" + battleState.getBattleId(), battleState);
        battleTimerService.startOrResetTimer(battleState);
        return ResponseEntity.ok().build();
    }
    
}
