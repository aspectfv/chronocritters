package com.chronocritters.gamelogic.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.PlayerState;

@Service
public class BattleService {
    private final List<BattleState> activeBattles = new ArrayList<>();

    public BattleState createBattle(String battleId, String playerOneId, String playerTwoId) {
        // TODO: Initialize player states with actual players from GRPC fetch
        PlayerState playerOne = PlayerState.builder().id(playerOneId).build();
        PlayerState playerTwo = PlayerState.builder().id(playerTwoId).build();

        BattleState battleState = BattleState.builder()
                .battleId(battleId)
                .activePlayerId(playerOneId)
                .playerOne(playerOne)
                .playerTwo(playerTwo)
                .lastActionLog("")
                .build();
        activeBattles.add(battleState);

        return battleState;
    }
}
