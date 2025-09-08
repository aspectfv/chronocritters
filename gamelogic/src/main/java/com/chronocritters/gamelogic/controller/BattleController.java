package com.chronocritters.gamelogic.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.chronocritters.gamelogic.service.BattleService;
import com.chronocritters.lib.dto.BattleRequest;
import com.chronocritters.lib.dto.ExecuteAbilityRequest;
import com.chronocritters.lib.dto.SwitchCritterRequest;
import com.chronocritters.lib.model.BattleState;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
public class BattleController {
    private final BattleService battleService;

    @GetMapping("/battle/{battleId}")
    public BattleState getBattle(@PathVariable String battleId) {
        return battleService.getBattleState(battleId);
    }

    @PostMapping("/battle/{battleId}")
    public ResponseEntity<Void> createBattle(@PathVariable String battleId, @Valid @RequestBody BattleRequest battleRequest) {
        battleService.createBattle(
                battleId,
                battleRequest.playerOneId(),
                battleRequest.playerTwoId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/battle/{battleId}/ability")
    public BattleState executeAbility(@PathVariable String battleId, @Valid @RequestBody ExecuteAbilityRequest request) {
        return battleService.executeAbility(battleId, request.playerId(), request.abilityId());
    }

    @PostMapping("/battle/{battleId}/switch")
    public BattleState switchCritter(@PathVariable String battleId, @Valid @RequestBody SwitchCritterRequest request) {
        return battleService.switchCritter(battleId, request.playerId(), request.targetCritterIndex());
    }
    

    @PostMapping("/battle/{battleId}/timeout")
    public BattleState handleTimeout(@PathVariable String battleId) {
        return battleService.handleTurnTimeout(battleId);
    }
}