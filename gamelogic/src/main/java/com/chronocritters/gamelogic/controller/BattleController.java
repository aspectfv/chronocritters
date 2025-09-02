package com.chronocritters.gamelogic.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.chronocritters.gamelogic.service.BattleService;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.gamelogic.dto.BattleRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
public class BattleController {
    private final BattleService battleService;

    @GetMapping("/battle/{battleId}")
    public BattleState getBattle(@PathVariable String battleId) {
        return battleService.getBattle(battleId);
    }
    

    @PostMapping("/battle/{battleId}")
    public ResponseEntity<Void> createBattle(@PathVariable String battleId, @RequestBody BattleRequest battleRequest) {
        battleService.createBattle(
                battleId,
                battleRequest.playerOneId(),
                battleRequest.playerTwoId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
