package com.chronocritters.gamelogic.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.chronocritters.gamelogic.config.BattleAuthFilter;
import com.chronocritters.gamelogic.service.BattleService;
import com.chronocritters.lib.dto.BattleRequest;
import com.chronocritters.lib.dto.ExecuteAbilityRequest;
import com.chronocritters.lib.dto.ForfeitRequest;
import com.chronocritters.lib.dto.SwitchCritterRequest;
import com.chronocritters.lib.model.battle.BattleState;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * The player id on every player-driven action comes from the JWT via
 * {@link BattleAuthFilter}, never from the request body.
 */
@RestController
@RequiredArgsConstructor
@Validated
public class BattleController {
    private final BattleService battleService;

    @GetMapping("/battle/{battleId}")
    public BattleState getBattle(@PathVariable String battleId, @RequestAttribute(BattleAuthFilter.PLAYER_ID_ATTRIBUTE) String playerId) {
        return battleService.getBattleStateFor(battleId, playerId);
    }

    @PostMapping("/battle/{battleId}")
    public ResponseEntity<BattleState> createBattle(@PathVariable String battleId, @Valid @RequestBody BattleRequest battleRequest) {
        BattleState battleState = battleService.createBattle(
                battleId,
                battleRequest.playerOneId(),
                battleRequest.playerTwoId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(battleState);
    }

    @PostMapping("/battle/{battleId}/ability")
    public BattleState executeAbility(@PathVariable String battleId, @RequestAttribute(BattleAuthFilter.PLAYER_ID_ATTRIBUTE) String playerId, @Valid @RequestBody ExecuteAbilityRequest request) {
        return battleService.executeAbility(battleId, playerId, request.abilityId());
    }

    @PostMapping("/battle/{battleId}/switch")
    public BattleState switchCritter(@PathVariable String battleId, @RequestAttribute(BattleAuthFilter.PLAYER_ID_ATTRIBUTE) String playerId, @Valid @RequestBody SwitchCritterRequest request) {
        return battleService.switchCritter(battleId, playerId, request.targetCritterIndex());
    }

    @PostMapping("/battle/{battleId}/timeout")
    public ResponseEntity<Void> handleTimeout(@PathVariable String battleId) {
        battleService.handleTurnTimeout(battleId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/battle/{battleId}/forfeit")
    public ResponseEntity<Void> forfeit(@PathVariable String battleId, @Valid @RequestBody ForfeitRequest request) {
        battleService.forfeit(battleId, request.playerId());
        return ResponseEntity.ok().build();
    }
}
