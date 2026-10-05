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

class LifestealEffectTest {

    private static final String ABILITY_ID = "eff-drain";

    private BattleState battleState;
    private CritterState caster;
    private CritterState target;

    @BeforeEach
    void setUp() {
        caster = critter("caster", CritterType.TOXIC, 20, 5, 5, ability(ABILITY_ID));
        target = critter("target", CritterType.TOXIC, 20, 5, 5);

        battleState = battle(player(PLAYER_ONE_ID, caster), player(PLAYER_TWO_ID, target));
        battleState.getPlayer().setLastSelectedAbilityId(ABILITY_ID);
    }

    private LifestealEffect drain(int damage, int percent) {
        return LifestealEffect.builder().id("eff-drain").description("drains").damage(damage).drainPercent(percent).build();
    }

    @Test
    @DisplayName("hits like a damage effect and gives a share of the hit back")
    void hitsAndHeals() {
        caster.getStats().setCurrentHp(10);

        drain(6, 50).apply(battleState);

        assertThat(target.getStats().getCurrentHp()).isEqualTo(14);
        assertThat(caster.getStats().getCurrentHp()).isEqualTo(13);
        assertThat(lastLog(battleState)).contains("drains 3 health");
    }

    @Test
    @DisplayName("drains from what reached health, so a ward starves it")
    void drainsOnlyWhatLanded() {
        caster.getStats().setCurrentHp(10);
        target.getActiveStatusEffects().add(ShieldEffect.builder().id("eff-ward").amount(4).duration(3).build());

        drain(6, 50).apply(battleState);

        assertThat(target.getStats().getCurrentHp()).isEqualTo(18);
        assertThat(caster.getStats().getCurrentHp()).isEqualTo(11);
    }

    @Test
    @DisplayName("never overheals past the maximum")
    void clampsAtMaxHp() {
        caster.getStats().setCurrentHp(19);

        drain(6, 50).apply(battleState);

        assertThat(caster.getStats().getCurrentHp()).isEqualTo(20);
    }
}
