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
    @DisplayName("asks the owner for a replacement instead of picking one for them")
    void asksTheOwnerForAReplacement() {
        faint(owner.getActiveCritter());

        assertThat(battleState.getAwaitingSwitchPlayerId()).isEqualTo(PLAYER_ONE_ID);
        assertThat(owner.getActiveCritterIndex()).as("nothing is sent out until they choose").isZero();
        assertThat(lastLog(battleState)).contains("must send out another critter");
    }

    @Test
    @DisplayName("leaves a benched critter fainting alone")
    void ignoresABenchedCritterFainting() {
        faint(owner.getCritterByIndex(1));

        assertThat(battleState.getAwaitingSwitchPlayerId()).isNull();
        assertThat(owner.getActiveCritterIndex()).isZero();
        assertThat(owner.getActiveCritter().getName()).isEqualTo("lead");
    }

    @Test
    @DisplayName("asks for nothing when the whole roster is down")
    void doesNotAskWhenNoCrittersRemain() {
        owner.getCritterByIndex(1).getStats().setCurrentHp(0);
        owner.getCritterByIndex(1).setFainted(true);

        faint(owner.getActiveCritter());

        assertThat(battleState.getAwaitingSwitchPlayerId()).isNull();
        assertThat(lastLog(battleState)).isEqualTo("lead fainted!");
    }

    @Test
    @DisplayName("falls back to roster order when a player lets the clock run out")
    void fallsBackToRosterOrder() {
        owner.getActiveCritter().getStats().setCurrentHp(0);

        assertThat(FaintingService.firstLivingCritterIndex(owner)).isEqualTo(1);
    }
}
