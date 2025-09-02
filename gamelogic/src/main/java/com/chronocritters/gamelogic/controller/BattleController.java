package com.chronocritters.gamelogic.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.chronocritters.gamelogic.service.BattleService;
import com.chronocritters.lib.dto.BattleRequest;
import com.chronocritters.lib.dto.ExecuteAbilityRequest;
import com.chronocritters.lib.model.BattleState;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
public class BattleController {
    private final BattleService battleService;

    @GetMapping("/battle/{battleId}")
    public BattleState getBattle(@PathVariable String battleId) {
        return battleService.getBattleState(battleId);
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

    @PostMapping("/battle/{battleId}/ability")
    public BattleState executeAbility(@PathVariable String battleId, @RequestBody ExecuteAbilityRequest request) {
        return battleService.executeAbility(battleId, request.playerId(), request.abilityId());
    }
}
