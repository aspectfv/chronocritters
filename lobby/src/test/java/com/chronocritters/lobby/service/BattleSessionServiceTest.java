package com.chronocritters.lobby.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.chronocritters.lobby.dto.Match;

class BattleSessionServiceTest {

    private static final Match MATCH = new Match("p1", "p2", "battle-1");

    private BattleSessionService battleSessionService;

    @BeforeEach
    void setUp() {
        battleSessionService = new BattleSessionService();
    }

    @Test
    @DisplayName("remembers the battle both players were placed in")
    void tracksBothPlayers() {
        battleSessionService.register(MATCH);

        assertThat(battleSessionService.battleIdOf("p1")).contains("battle-1");
        assertThat(battleSessionService.battleIdOf("p2")).contains("battle-1");
    }

    @Test
    @DisplayName("has nothing for a player who is not in a battle")
    void hasNothingForAnIdlePlayer() {
        assertThat(battleSessionService.battleIdOf("p3")).isEmpty();
    }

    @Test
    @DisplayName("clearing a finished battle forgets both of its players and nobody else")
    void clearingForgetsOnlyThatBattle() {
        battleSessionService.register(MATCH);
        battleSessionService.register(new Match("p3", "p4", "battle-2"));

        battleSessionService.clear("battle-1");

        assertThat(battleSessionService.battleIdOf("p1")).isEmpty();
        assertThat(battleSessionService.battleIdOf("p2")).isEmpty();
        assertThat(battleSessionService.battleIdOf("p3")).contains("battle-2");
    }

    @Test
    @DisplayName("a player who joins a new battle is tracked against that battle")
    void reregisteringMovesThePlayer() {
        battleSessionService.register(MATCH);

        battleSessionService.register(new Match("p1", "p5", "battle-3"));

        assertThat(battleSessionService.battleIdOf("p1")).contains("battle-3");
    }
}
