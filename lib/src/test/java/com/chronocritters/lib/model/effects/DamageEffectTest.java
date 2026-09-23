package com.chronocritters.lib.model.effects;

import static com.chronocritters.lib.testsupport.BattleFixtures.PLAYER_ONE_ID;
import static com.chronocritters.lib.testsupport.BattleFixtures.PLAYER_TWO_ID;
import static com.chronocritters.lib.testsupport.BattleFixtures.ability;
import static com.chronocritters.lib.testsupport.BattleFixtures.battle;
import static com.chronocritters.lib.testsupport.BattleFixtures.critter;
import static com.chronocritters.lib.testsupport.BattleFixtures.lastLog;
import static com.chronocritters.lib.testsupport.BattleFixtures.player;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.domain.Ability;
import com.chronocritters.lib.model.enums.CritterType;

class DamageEffectTest {

    private static final String ABILITY_ID = "atk-test";

    private BattleState battleWith(CritterType attackerType, int attack, CritterType defenderType, int defenderHp, int defense, int damage) {
        Ability ability = ability(ABILITY_ID, DamageEffect.builder().id("eff-damage").description("hurts").damage(damage).build());

        CritterState attacker = critter("attacker", attackerType, 10, attack, 1, ability);
        CritterState defender = critter("defender", defenderType, defenderHp, 1, defense);

        BattleState battleState = battle(player(PLAYER_ONE_ID, attacker), player(PLAYER_TWO_ID, defender));
        battleState.getPlayer().setLastSelectedAbilityId(ABILITY_ID);
        return battleState;
    }

    private DamageEffect effectOf(BattleState battleState) {
        return (DamageEffect) battleState.getPlayer().getActiveCritter().getAbilityById(ABILITY_ID).getEffects().get(0);
    }

    @Test
    @DisplayName("scales damage by the attack-to-defence ratio")
    void appliesAttackDefenceRatio() {
        // 4 damage * (4 attack / 2 defence) = 8, neutral types
        BattleState battleState = battleWith(CritterType.FIRE, 4, CritterType.FIRE, 20, 2, 4);

        effectOf(battleState).apply(battleState);

        assertThat(battleState.getOpponent().getActiveCritter().getStats().getCurrentHp()).isEqualTo(12);
    }

    @Test
    @DisplayName("caps the attack-to-defence ratio at 3x so a glass cannon cannot one-shot")
    void capsAttackDefenceRatio() {
        // 2 damage * min(3.0, 100/1) = 6, not 200
        BattleState battleState = battleWith(CritterType.FIRE, 100, CritterType.FIRE, 20, 1, 2);

        effectOf(battleState).apply(battleState);

        assertThat(battleState.getOpponent().getActiveCritter().getStats().getCurrentHp()).isEqualTo(14);
    }

    @Test
    @DisplayName("applies the type advantage multiplier and says so in the log")
    void appliesSuperEffectiveMultiplier() {
        // 4 damage * (2/2) * 1.5 for WATER into FIRE
        BattleState battleState = battleWith(CritterType.WATER, 2, CritterType.FIRE, 20, 2, 4);

        effectOf(battleState).apply(battleState);

        assertThat(battleState.getOpponent().getActiveCritter().getStats().getCurrentHp()).isEqualTo(14);
        assertThat(lastLog(battleState)).contains("It's super effective!");
    }

    @Test
    @DisplayName("halves damage into a resistant type and says so in the log")
    void appliesNotVeryEffectiveMultiplier() {
        // 4 damage * (2/2) * 0.5 for FIRE into WATER
        BattleState battleState = battleWith(CritterType.FIRE, 2, CritterType.WATER, 20, 2, 4);

        effectOf(battleState).apply(battleState);

        assertThat(battleState.getOpponent().getActiveCritter().getStats().getCurrentHp()).isEqualTo(18);
        assertThat(lastLog(battleState)).contains("It's not very effective...");
    }

    @Test
    @DisplayName("publishes the hit as numbers so the client can animate it")
    void publishesTheHitAsNumbers() {
        BattleState battleState = battleWith(CritterType.WATER, 2, CritterType.FIRE, 20, 2, 4);

        effectOf(battleState).apply(battleState);

        assertThat(battleState.getLastTurnResult()).satisfies(result -> {
            assertThat(result.getCasterCritterId()).isEqualTo("attacker");
            assertThat(result.getTargetCritterId()).isEqualTo("defender");
            assertThat(result.getDamage()).isEqualTo(6);
            assertThat(result.getEffectiveness()).isEqualTo(1.5);
        });
    }

    @Test
    @DisplayName("never drives health below zero")
    void clampsHealthAtZero() {
        BattleState battleState = battleWith(CritterType.FIRE, 6, CritterType.FIRE, 3, 2, 10);

        effectOf(battleState).apply(battleState);

        assertThat(battleState.getOpponent().getActiveCritter().getStats().getCurrentHp()).isZero();
    }

    @Test
    @DisplayName("credits the damage to the attacking player and records a turn action")
    void recordsDamageAgainstAttacker() {
        BattleState battleState = battleWith(CritterType.FIRE, 4, CritterType.FIRE, 20, 2, 4);

        effectOf(battleState).apply(battleState);

        assertThat(battleState.getBattleStats().getPlayersDamageDealt())
                .containsEntry(PLAYER_ONE_ID, 8)
                .containsEntry(PLAYER_TWO_ID, 0);
        assertThat(battleState.getBattleStats().getTurnActionHistory())
                .singleElement()
                .satisfies(entry -> {
                    assertThat(entry.getPlayerId()).isEqualTo(PLAYER_ONE_ID);
                    assertThat(entry.getTurnActionLog()).contains("8 damage");
                });
    }
}
