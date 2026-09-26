package com.chronocritters.lib.model.effects;

import static com.chronocritters.lib.testsupport.BattleFixtures.PLAYER_ONE_ID;
import static com.chronocritters.lib.testsupport.BattleFixtures.PLAYER_TWO_ID;
import static com.chronocritters.lib.testsupport.BattleFixtures.ability;
import static com.chronocritters.lib.testsupport.BattleFixtures.battle;
import static com.chronocritters.lib.testsupport.BattleFixtures.critter;
import static com.chronocritters.lib.testsupport.BattleFixtures.lastLog;
import static com.chronocritters.lib.testsupport.BattleFixtures.player;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.enums.CritterType;

class HealEffectTest {

    private static final String ABILITY_ID = "eff-heal";

    private BattleState battleState;
    private CritterState caster;

    @BeforeEach
    void setUp() {
        caster = critter("caster", CritterType.GRASS, 20, 3, 3, ability(ABILITY_ID));
        CritterState target = critter("target", CritterType.GRASS, 20, 3, 3);

        battleState = battle(player(PLAYER_ONE_ID, caster), player(PLAYER_TWO_ID, target));
        battleState.getPlayer().setLastSelectedAbilityId(ABILITY_ID);
    }

    private HealEffect heal(int percent) {
        return HealEffect.builder().id("eff-mend").description("mends").healPercent(percent).build();
    }

    @Test
    @DisplayName("restores a share of the caster's maximum health")
    void restoresShareOfMaxHp() {
        caster.getStats().setCurrentHp(5);

        heal(50).apply(battleState);

        assertThat(caster.getStats().getCurrentHp()).isEqualTo(15);
        assertThat(lastLog(battleState)).contains("recovered 10 health");
    }

    @Test
    @DisplayName("never overheals past the maximum")
    void clampsAtMaxHp() {
        caster.getStats().setCurrentHp(18);

        heal(50).apply(battleState);

        assertThat(caster.getStats().getCurrentHp()).isEqualTo(20);
        assertThat(lastLog(battleState)).contains("recovered 2 health");
    }

    @Test
    @DisplayName("rounds the share to the nearest point")
    void roundsToNearest() {
        caster.getStats().setMaxHp(15);
        caster.getStats().setCurrentHp(1);

        heal(30).apply(battleState);

        assertThat(caster.getStats().getCurrentHp()).isEqualTo(1 + 5);
    }

    @Test
    @DisplayName("leaves the opposing critter alone")
    void doesNotTouchTheTarget() {
        CritterState target = battleState.getOpponent().getActiveCritter();
        target.getStats().setCurrentHp(5);

        heal(50).apply(battleState);

        assertThat(target.getStats().getCurrentHp()).isEqualTo(5);
    }
}
