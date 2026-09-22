package com.chronocritters.gamelogic.service;

import static com.chronocritters.lib.testsupport.BattleFixtures.PLAYER_ONE_ID;
import static com.chronocritters.lib.testsupport.BattleFixtures.PLAYER_TWO_ID;
import static com.chronocritters.lib.testsupport.BattleFixtures.battle;
import static com.chronocritters.lib.testsupport.BattleFixtures.critter;
import static com.chronocritters.lib.testsupport.BattleFixtures.lastLog;
import static com.chronocritters.lib.testsupport.BattleFixtures.player;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.chronocritters.gamelogic.event.CritterFaintedEvent;
import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.battle.PlayerState;
import com.chronocritters.lib.model.enums.CritterType;

class FaintingServiceTest {

    private final FaintingService faintingService = new FaintingService();

    private BattleState battleState;
    private PlayerState owner;

    @BeforeEach
    void setUp() {
        owner = player(PLAYER_ONE_ID,
                critter("lead", CritterType.FIRE, 5, 3, 3),
                critter("bench", CritterType.WATER, 5, 3, 3));

        battleState = battle(owner, player(PLAYER_TWO_ID, critter("rival", CritterType.GRASS, 5, 3, 3)));
    }

    private void faint(CritterState critter) {
        critter.getStats().setCurrentHp(0);
        critter.setFainted(true);
        faintingService.onCritterFainted(new CritterFaintedEvent(this, battleState, owner, critter));
    }

    @Test
    @DisplayName("sends out the next living critter when the active one faints")
    void sendsOutTheNextLivingCritter() {
        faint(owner.getActiveCritter());

        assertThat(owner.getActiveCritterIndex()).isEqualTo(1);
        assertThat(owner.getActiveCritter().getName()).isEqualTo("bench");
    }

    @Test
    @DisplayName("names the replacement in the log, not the critter that just fainted")
    void logsTheReplacementCritter() {
        faint(owner.getActiveCritter());

        assertThat(lastLog(battleState)).contains("bench").doesNotContain("lead is sent out");
    }

    @Test
    @DisplayName("leaves a benched critter fainting alone")
    void ignoresABenchedCritterFainting() {
        faint(owner.getCritterByIndex(1));

        assertThat(owner.getActiveCritterIndex()).isZero();
        assertThat(owner.getActiveCritter().getName()).isEqualTo("lead");
    }

    @Test
    @DisplayName("does not send anyone out when the whole roster is down")
    void doesNotSwitchWhenNoCrittersRemain() {
        owner.getCritterByIndex(1).getStats().setCurrentHp(0);
        owner.getCritterByIndex(1).setFainted(true);

        faint(owner.getActiveCritter());

        assertThat(owner.getActiveCritterIndex()).isZero();
        assertThat(lastLog(battleState)).isEqualTo("lead fainted!");
    }
}
