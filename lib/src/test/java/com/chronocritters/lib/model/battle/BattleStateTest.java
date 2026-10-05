package com.chronocritters.lib.model.battle;

import static com.chronocritters.lib.testsupport.BattleFixtures.PLAYER_ONE_ID;
import static com.chronocritters.lib.testsupport.BattleFixtures.PLAYER_TWO_ID;
import static com.chronocritters.lib.testsupport.BattleFixtures.battle;
import static com.chronocritters.lib.testsupport.BattleFixtures.critter;
import static com.chronocritters.lib.testsupport.BattleFixtures.player;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.chronocritters.lib.model.effects.StatusEffect;
import com.chronocritters.lib.model.enums.CritterType;
import com.chronocritters.lib.model.enums.StatusCondition;

class BattleStateTest {

    private BattleState battleState;
    private CritterState lead;

    @BeforeEach
    void setUp() {
        lead = critter("lead", CritterType.FIRE, 20, 3, 3);
        battleState = battle(
                player(PLAYER_ONE_ID, lead, critter("bench", CritterType.WATER, 20, 3, 3)),
                player(PLAYER_TWO_ID, critter("foe", CritterType.GRASS, 20, 3, 3)));
    }

    private void recordAt(int turn) {
        battleState.getBattleStats().setTurnCount(turn);
        battleState.recordTurn();
    }

    @Test
    @DisplayName("keeps only the turns a rewind can still reach")
    void keepsABoundedHistory() {
        for (int turn = 0; turn <= 5; turn++) recordAt(turn);

        assertThat(battleState.getHistory()).containsOnlyKeys(3, 4, 5);
    }

    @Test
    @DisplayName("reaches back two turns, or to the latest turn before that when sleep skipped some")
    void findsTheTurnTwoBack() {
        recordAt(0);
        lead.getStats().setCurrentHp(15);
        recordAt(1);
        lead.getStats().setCurrentHp(10);
        recordAt(4);

        assertThat(battleState.rewindTarget().get("lead").currentHp()).isEqualTo(15);
        assertThat(battleState.getHistory()).containsOnlyKeys(1, 4);
    }

    @Test
    @DisplayName("has nothing to go back to in the first two turns")
    void hasNoTargetTooEarly() {
        recordAt(0);
        recordAt(1);

        assertThat(battleState.rewindTarget()).isNull();
    }

    @Test
    @DisplayName("records every critter on both teams, benched ones included, as copies that later turns do not touch")
    void snapshotsAreIndependentCopies() {
        lead.setStatus(StatusEffect.builder().id("eff-burn").condition(StatusCondition.BURN).duration(3).potency(1).build());
        lead.getAbilityCooldowns().put("atk-big", 2);
        recordAt(0);

        lead.getStatus().setDuration(1);
        lead.getAbilityCooldowns().put("atk-big", 1);
        recordAt(2);

        CritterSnapshot snapshot = battleState.rewindTarget().get("lead");
        assertThat(snapshot.status().getDuration()).isEqualTo(3);
        assertThat(snapshot.abilityCooldowns()).containsEntry("atk-big", 2);
        assertThat(battleState.rewindTarget()).containsOnlyKeys("lead", "bench", "foe");
    }
}
