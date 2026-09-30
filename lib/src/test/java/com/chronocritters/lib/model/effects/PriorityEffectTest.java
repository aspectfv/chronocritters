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
import com.chronocritters.lib.model.enums.CritterType;

class PriorityEffectTest {

    private static final String ABILITY_ID = "atk-feint";

    @Test
    @DisplayName("marks the caster, and only the caster, to open the next round")
    void claimsTheNextRoundForTheCaster() {
        CritterState caster = critter("caster", CritterType.KINETIC, 20, 5, 5, ability(ABILITY_ID));
        CritterState target = critter("target", CritterType.KINETIC, 20, 5, 5);
        BattleState battleState = battle(player(PLAYER_ONE_ID, caster), player(PLAYER_TWO_ID, target));
        battleState.getPlayer().setLastSelectedAbilityId(ABILITY_ID);

        PriorityEffect.builder().id("eff-priority").description("moves first").build().apply(battleState);

        assertThat(caster.isPriority()).isTrue();
        assertThat(target.isPriority()).isFalse();
        assertThat(lastLog(battleState)).contains("will move first next round");
    }
}
