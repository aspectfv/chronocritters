package com.chronocritters.lib.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BattleState {
    private String battleId;
    private String activePlayerId;
    private PlayerState playerOne;
    private PlayerState playerTwo;
    private List<String> actionLogHistory;

    public PlayerState getCurrentPlayer() {
        if (playerOne != null && playerOne.getId().equals(activePlayerId)) {
            return playerOne;
        }
        if (playerTwo != null && playerTwo.getId().equals(activePlayerId)) {
            return playerTwo;
        }
        return null;
    }

    public PlayerState getOpponent() {
        if (playerOne != null && playerOne.getId().equals(activePlayerId)) {
            return playerTwo;
        }
        if (playerTwo != null && playerTwo.getId().equals(activePlayerId)) {
            return playerOne;
        }
        return null;
    }
}
