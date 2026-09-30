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
import com.chronocritters.lib.model.enums.StatusCondition;

class StatusEffectTest {

    private static final String ABILITY_ID = "eff-status";

    private BattleState battleState;
    private CritterState caster;
    private CritterState target;

    @BeforeEach
    void setUp() {
        caster = critter("caster", CritterType.TOXIC, 20, 8, 5, ability(ABILITY_ID));
        target = critter("target", CritterType.TOXIC, 20, 8, 5);
        target.getStats().setSpeed(8);

        battleState = battle(player(PLAYER_ONE_ID, caster), player(PLAYER_TWO_ID, target));
        battleState.getPlayer().setLastSelectedAbilityId(ABILITY_ID);
    }

    private StatusEffect status(StatusCondition condition, int duration, int potency) {
        return StatusEffect.builder().id("eff-" + condition).description("afflicts")
                .condition(condition).duration(duration).potency(potency).build();
    }

    @Test
    @DisplayName("puts a copy of itself in the target's status slot, not in the effect list")
    void fillsTheStatusSlot() {
        StatusEffect abilityEffect = status(StatusCondition.BURN, 3, 1);

        abilityEffect.onApply(battleState);

        assertThat(target.getStatus()).isNotSameAs(abilityEffect);
        assertThat(target.hasStatus(StatusCondition.BURN)).isTrue();
        assertThat(target.getStatus().getCasterId()).isEqualTo("caster");
        assertThat(target.getActiveStatusEffects()).isEmpty();
    }

    @Test
    @DisplayName("fails on a critter that already holds a status, whichever it is")
    void oneStatusAtATime() {
        status(StatusCondition.BURN, 3, 1).onApply(battleState);

        status(StatusCondition.SLEEP, 2, 0).onApply(battleState);

        assertThat(target.hasStatus(StatusCondition.BURN)).isTrue();
        assertThat(lastLog(battleState)).contains("is already burned, so eff-status has no effect");
    }

    @Test
    @DisplayName("a status that has just ended cannot be put straight back, though another one can")
    void immunityAfterEnding() {
        status(StatusCondition.SLEEP, 1, 0).onApply(battleState);
        target.endStatus();

        status(StatusCondition.SLEEP, 1, 0).onApply(battleState);
        assertThat(target.getStatus()).isNull();
        assertThat(lastLog(battleState)).contains("has only just stopped being asleep");

        status(StatusCondition.BURN, 2, 1).onApply(battleState);
        assertThat(target.hasStatus(StatusCondition.BURN)).isTrue();
    }

    @Test
    @DisplayName("a burn deals its potency every tick, credited to the caster, and halves attack while it lasts")
    void burn() {
        status(StatusCondition.BURN, 2, 3).onApply(battleState);
        assertThat(target.effectiveAttack()).isEqualTo(4);

        assertThat(target.getStatus().onTick(battleState, target)).isFalse();
        assertThat(target.getStatus().onTick(battleState, target)).isTrue();

        assertThat(target.getStats().getCurrentHp()).isEqualTo(20 - 6);
        assertThat(battleState.getBattleStats().getPlayersDamageDealt()).containsEntry("caster", 6);
    }

    @Test
    @DisplayName("poison hits harder on every tick it stays on")
    void poisonRamps() {
        status(StatusCondition.POISON, 3, 2).onApply(battleState);
        StatusEffect poison = target.getStatus();

        poison.onTick(battleState, target);
        assertThat(target.getStats().getCurrentHp()).isEqualTo(18);
        poison.onTick(battleState, target);
        assertThat(target.getStats().getCurrentHp()).isEqualTo(14);
        assertThat(poison.onTick(battleState, target)).isTrue();
        assertThat(target.getStats().getCurrentHp()).isEqualTo(8);
    }

    @Test
    @DisplayName("paralysis deals nothing, halves speed, and runs out on the usual countdown")
    void paralysis() {
        status(StatusCondition.PARALYSIS, 2, 0).onApply(battleState);
        assertThat(target.effectiveSpeed()).isEqualTo(4);

        target.getStatus().onTick(battleState, target);
        assertThat(target.getStatus().onTick(battleState, target)).isTrue();
        assertThat(target.getStats().getCurrentHp()).isEqualTo(20);
    }

    @Test
    @DisplayName("sleep does not tick with time, only with the owner's turns it skips")
    void sleepCountsSkippedTurns() {
        status(StatusCondition.SLEEP, 2, 0).onApply(battleState);
        StatusEffect sleep = target.getStatus();

        assertThat(sleep.onTick(battleState, target)).isFalse();
        assertThat(sleep.getDuration()).isEqualTo(2);

        assertThat(sleep.sleepThroughTurn(battleState, target)).isFalse();
        assertThat(sleep.sleepThroughTurn(battleState, target)).as("spent on the second skipped turn").isTrue();
        assertThat(lastLog(battleState)).isEqualTo("target is no longer asleep.");
    }
}
