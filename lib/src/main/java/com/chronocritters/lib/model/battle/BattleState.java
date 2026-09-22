package com.chronocritters.lib.model.battle;

import java.util.List;

import com.chronocritters.lib.model.enums.BattleOutcome;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BattleState {
    /** How long a player is given to act, in seconds. */
    public static final int TURN_DURATION_SECONDS = 30;

    private String battleId;
    private String activePlayerId;
    private PlayerState playerOne;
    private PlayerState playerTwo;
    private List<String> actionLogHistory;
    private int timeRemaining;

    /** Sent so the client's timer bar does not have to hardcode the same number. */
    @Default
    private int turnDuration = TURN_DURATION_SECONDS;

    private TurnResult lastTurnResult;

    /**
     * Set to the player who has lost their active critter and owes a
     * replacement. They choose it themselves, and the choice is free.
     */
    private String awaitingSwitchPlayerId;

    private BattleStats battleStats;

    @Default
    private BattleOutcome battleOutcome = BattleOutcome.CONTINUE;
    
    private String winnerId;
    private BattleRewards battleRewards;

    public PlayerState getPlayer() {
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

    public PlayerState getPlayerById(String playerId) {
        if (playerOne != null && playerOne.getId().equals(playerId)) {
            return playerOne;
        }
        if (playerTwo != null && playerTwo.getId().equals(playerId)) {
            return playerTwo;
        }
        return null;
    }
}