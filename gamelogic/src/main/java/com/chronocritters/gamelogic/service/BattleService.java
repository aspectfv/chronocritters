package com.chronocritters.gamelogic.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.chronocritters.gamelogic.converter.PlayerConverter;
import com.chronocritters.gamelogic.grpc.PlayerGrpcClient;
import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.PlayerState;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BattleService {
    private final List<BattleState> activeBattles = new ArrayList<>();
    private final PlayerGrpcClient playerGrpcClient;

    public BattleState getBattle(String battleId) {
        return activeBattles.stream()
                .filter(battle -> battle.getBattleId().equals(battleId))
                .findFirst()
                .orElse(null);
    }

    public void createBattle(String battleId, String playerOneId, String playerTwoId) {
        PlayerState playerOne = PlayerConverter.convertToPlayerState(playerGrpcClient.getPlayer(playerOneId));
        PlayerState playerTwo = PlayerConverter.convertToPlayerState(playerGrpcClient.getPlayer(playerTwoId));

        playerOne.setHasTurn(true);
        playerTwo.setHasTurn(false);

        BattleState battleState = BattleState.builder()
                .battleId(battleId)
                .activePlayerId(playerOneId)
                .playerOne(playerOne)
                .playerTwo(playerTwo)
                .lastActionLog("Battle started between " + playerOne.getUsername() + " and " + playerTwo.getUsername())
                .build();

        activeBattles.add(battleState);
    }

    public BattleState executeAbility(String battleId, String playerId, String abilityId) {
        BattleState currentBattle = getBattle(battleId);
        if (currentBattle == null) {
            throw new IllegalArgumentException("Invalid battle ID");
        }

        if (!currentBattle.getActivePlayerId().equals(playerId)) {
            throw new IllegalStateException("It's not the player's turn");
        }

        PlayerState currentPlayer = playerId.equals(currentBattle.getPlayerOne().getId()) 
            ? currentBattle.getPlayerOne() 
            : currentBattle.getPlayerTwo();

        PlayerState opponent = playerId.equals(currentBattle.getPlayerOne().getId()) 
            ? currentBattle.getPlayerTwo() 
            : currentBattle.getPlayerOne();

        CritterState activeCritter = currentPlayer.getActiveCritter();

        Ability ability = activeCritter.getAbilityById(abilityId);
        if (ability == null) {
            throw new IllegalArgumentException("Invalid ability ID");
        }

        switch (ability.getType()) {
            case ATTACK -> {

            }
            case DEFENSE -> {
            }
            default -> throw new IllegalArgumentException("Unexpected value: " + ability.getType());
        }

        return currentBattle;
    }
}
