package com.chronocritters.lib.model.battle;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import com.chronocritters.lib.model.enums.BattleOutcome;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

    /** How far back a rewind reaches, in turns. */
    public static final int REWIND_TURNS = 2;

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
     * The players who have lost their active critter and owe a replacement.
     * They choose it themselves, and the choice is free. Both actives can fall
     * on the same turn, so this holds a player each rather than one player:
     * leaving the second one out left a fainted critter on the field to be hit
     * again and again. Nobody may act until it is empty.
     */
    @Default
    private Set<String> awaitingSwitchPlayerIds = new LinkedHashSet<>();

    /**
     * The player still to act in the current round, after the one whose turn it
     * is. Empty once both have acted, which is when the next round is ordered.
     */
    private String nextInRound;

    /**
     * A round came due while a fainted critter was still waiting to be
     * replaced. The order is decided once the replacement is on the field,
     * because it is the replacement's speed that counts.
     */
    private boolean roundPending;

    private BattleStats battleStats;

    /**
     * Every critter on both teams at the start of each recent turn, by turn
     * number and then critter id. Only what a rewind can still reach is kept.
     * It stays on the server: the clients are shown what a rewind changed, not
     * the numbers it could change back to.
     */
    @JsonIgnore
    @Default
    private NavigableMap<Integer, Map<String, CritterSnapshot>> history = new TreeMap<>();

    @Default
    private BattleOutcome battleOutcome = BattleOutcome.CONTINUE;
    
    private String winnerId;
    private BattleRewards battleRewards;

    /** Records the turn that is starting, and forgets what a rewind can no longer reach. */
    public void recordTurn() {
        Map<String, CritterSnapshot> critters = new HashMap<>();
        for (PlayerState player : List.of(playerOne, playerTwo)) {
            player.getRoster().forEach(critter -> critters.put(critter.getId(), CritterSnapshot.of(critter)));
        }

        int turn = battleStats.getTurnCount();
        history.put(turn, critters);

        Integer oldestReachable = history.floorKey(turn - REWIND_TURNS);
        if (oldestReachable != null) {
            history.headMap(oldestReachable, false).clear();
        }
    }

    /** The critters as they stood {@link #REWIND_TURNS} turns ago, or null this early in a battle. */
    public Map<String, CritterSnapshot> rewindTarget() {
        Map.Entry<Integer, Map<String, CritterSnapshot>> target = history.floorEntry(battleStats.getTurnCount() - REWIND_TURNS);
        return target == null ? null : target.getValue();
    }

    public boolean isAwaitingSwitch() {
        return !awaitingSwitchPlayerIds.isEmpty();
    }

    public boolean awaitsSwitchFrom(String playerId) {
        return awaitingSwitchPlayerIds.contains(playerId);
    }

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