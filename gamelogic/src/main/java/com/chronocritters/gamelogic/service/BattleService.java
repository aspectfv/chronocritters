package com.chronocritters.gamelogic.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.chronocritters.gamelogic.converter.PlayerConverter;
import com.chronocritters.gamelogic.grpc.PlayerGrpcClient;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.PlayerState;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BattleService {
    private final List<BattleState> activeBattles = new ArrayList<>();
    private final PlayerGrpcClient playerGrpcClient;

    public BattleState createBattle(String battleId, String playerOneId, String playerTwoId) {
        // Initialize player states with actual players from GRPC fetch
        PlayerState playerOne = PlayerConverter.convertToPlayerState(playerGrpcClient.getPlayer(playerOneId));
        PlayerState playerTwo = PlayerConverter.convertToPlayerState(playerGrpcClient.getPlayer(playerTwoId));

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
