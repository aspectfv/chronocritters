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

class CleanseEffectTest {

    private static final String ABILITY_ID = "eff-cleanse";

    private BattleState battleState;
    private CritterState caster;

    @BeforeEach
    void setUp() {
        caster = critter("caster", CritterType.WATER, 20, 5, 5, ability(ABILITY_ID));
        CritterState target = critter("target", CritterType.WATER, 20, 5, 5);

        battleState = battle(player(PLAYER_ONE_ID, caster), player(PLAYER_TWO_ID, target));
        battleState.getPlayer().setLastSelectedAbilityId(ABILITY_ID);
    }

    private CleanseEffect cleanse() {
        return CleanseEffect.builder().id("eff-cleanse").description("cleanses").build();
    }

    @Test
    @DisplayName("clears the caster's ailments and status, keeps its ward, and leaves no immunity behind")
    void clearsAilmentsKeepsWard() {
        caster.getActiveStatusEffects().add(DamageOverTimeEffect.builder().id("eff-bind").damagePerTurn(2).duration(2).build());
        caster.getActiveStatusEffects().add(ShieldEffect.builder().id("eff-ward").amount(5).duration(3).build());
        caster.setStatus(StatusEffect.builder().id("eff-burn").condition(StatusCondition.BURN).duration(2).potency(1).build());

        cleanse().apply(battleState);

        assertThat(caster.getActiveStatusEffects()).singleElement().isInstanceOf(ShieldEffect.class);
        assertThat(caster.getStatus()).isNull();
        assertThat(caster.getStatusImmunity()).isNull();
        assertThat(lastLog(battleState)).contains("shook off every ailment");
    }

    @Test
    @DisplayName("leaves the opposing critter's ailments where they are")
    void doesNotTouchTheTarget() {
        CritterState target = battleState.getOpponent().getActiveCritter();
        target.getActiveStatusEffects().add(DamageOverTimeEffect.builder().id("eff-burn").damagePerTurn(2).duration(2).build());

        cleanse().apply(battleState);

        assertThat(target.getActiveStatusEffects()).hasSize(1);
    }

    @Test
    @DisplayName("says when there was nothing to clear")
    void reportsNothingToClear() {
        cleanse().apply(battleState);

        assertThat(lastLog(battleState)).contains("nothing to shake off");
    }
}
