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
import com.chronocritters.lib.model.battle.CurrentStats;
import com.chronocritters.lib.model.enums.CritterType;
import com.chronocritters.lib.model.enums.Stat;

class StatModifierEffectTest {

    private static final String ABILITY_ID = "eff-stat";

    private BattleState battleState;

    @BeforeEach
    void setUp() {
        CritterState caster = critter("caster", CritterType.KINETIC, 10, 10, 10, ability(ABILITY_ID));
        CritterState target = critter("target", CritterType.KINETIC, 10, 10, 10);

        battleState = battle(player(PLAYER_ONE_ID, caster), player(PLAYER_TWO_ID, target));
        battleState.getPlayer().setLastSelectedAbilityId(ABILITY_ID);
    }

    private StatModifierEffect shift(Stat stat, int stages) {
        return StatModifierEffect.builder().id("eff-shift").description("shifts").stat(stat).stages(stages).build();
    }

    private CurrentStats casterStats() {
        return battleState.getPlayer().getActiveCritter().getStats();
    }

    private CurrentStats targetStats() {
        return battleState.getOpponent().getActiveCritter().getStats();
    }

    @Test
    @DisplayName("a positive shift raises the caster's own stat")
    void raisesTheCaster() {
        shift(Stat.ATTACK, 1).apply(battleState);

        assertThat(casterStats().getCurrentAtk()).isEqualTo(15);
        assertThat(targetStats().getCurrentAtk()).isEqualTo(10);
        assertThat(lastLog(battleState)).contains("caster's attack rose by 1");
    }

    @Test
    @DisplayName("a negative shift lowers the opposing critter's stat")
    void lowersTheTarget() {
        shift(Stat.DEFENSE, -1).apply(battleState);

        assertThat(targetStats().getCurrentDef()).isEqualTo(7);
        assertThat(casterStats().getCurrentDef()).isEqualTo(10);
        assertThat(lastLog(battleState)).contains("target's defense fell by 1");
    }

    @Test
    @DisplayName("stages stack on the same stat and read from the base, not from each other")
    void stagesStack() {
        shift(Stat.ATTACK, 1).apply(battleState);
        shift(Stat.ATTACK, 1).apply(battleState);

        assertThat(casterStats().getStage(Stat.ATTACK)).isEqualTo(2);
        assertThat(casterStats().getCurrentAtk()).isEqualTo(20);
    }

    @Test
    @DisplayName("stops at six stages and says so")
    void capsAtSix() {
        shift(Stat.ATTACK, 6).apply(battleState);
        shift(Stat.ATTACK, 1).apply(battleState);

        assertThat(casterStats().getStage(Stat.ATTACK)).isEqualTo(CurrentStats.MAX_STAGE);
        assertThat(casterStats().getCurrentAtk()).isEqualTo(40);
        assertThat(lastLog(battleState)).contains("can't go any higher");
    }

    @Test
    @DisplayName("a stat lowered six stages is a quarter of itself, never zero")
    void floorsAtOne() {
        targetStats().setDefense(2);

        shift(Stat.DEFENSE, -6).apply(battleState);

        assertThat(targetStats().getCurrentDef()).isEqualTo(1);
    }

    @Test
    @DisplayName("a raised stat changes the damage the next hit does")
    void raisedAttackHitsHarder() {
        DamageEffect strike = DamageEffect.builder().id("eff-strike").description("hits").damage(4).build();

        shift(Stat.ATTACK, 2).apply(battleState);
        strike.apply(battleState);

        assertThat(targetStats().getCurrentHp()).isEqualTo(10 - 8);
    }
}
