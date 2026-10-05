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

class ShieldEffectTest {

    private static final String ABILITY_ID = "eff-ward";
    private static final int AMOUNT = 5;
    private static final int DURATION = 3;

    private BattleState battleState;
    private CritterState caster;

    @BeforeEach
    void setUp() {
        caster = critter("caster", CritterType.METAL, 20, 5, 5, ability(ABILITY_ID));
        CritterState target = critter("target", CritterType.METAL, 20, 5, 5);

        battleState = battle(player(PLAYER_ONE_ID, caster), player(PLAYER_TWO_ID, target));
        battleState.getPlayer().setLastSelectedAbilityId(ABILITY_ID);
    }

    private ShieldEffect ward() {
        return ShieldEffect.builder().id("eff-shield").description("wards").amount(AMOUNT).duration(DURATION).build();
    }

    private ShieldEffect appliedWard() {
        return (ShieldEffect) caster.getActiveStatusEffects().get(0);
    }

    @Test
    @DisplayName("attaches a copy of the ward to the caster, not the target")
    void wardsTheCaster() {
        ShieldEffect abilityEffect = ward();

        abilityEffect.onApply(battleState);

        assertThat(caster.getActiveStatusEffects()).singleElement().isInstanceOf(ShieldEffect.class);
        assertThat(appliedWard()).isNotSameAs(abilityEffect);
        assertThat(battleState.getOpponent().getActiveCritter().getActiveStatusEffects()).isEmpty();
    }

    @Test
    @DisplayName("soaks damage before health and drops once it is used up")
    void absorbsThenBreaks() {
        ward().onApply(battleState);

        assertThat(caster.takeDamage(3)).isZero();
        assertThat(caster.getStats().getCurrentHp()).isEqualTo(20);
        assertThat(appliedWard().getAmount()).isEqualTo(2);

        assertThat(caster.takeDamage(6)).isEqualTo(4);
        assertThat(caster.getStats().getCurrentHp()).isEqualTo(16);
        assertThat(caster.getActiveStatusEffects()).isEmpty();
    }

    @Test
    @DisplayName("also soaks damage over time")
    void absorbsTicks() {
        ward().onApply(battleState);
        DamageOverTimeEffect burn = DamageOverTimeEffect.builder().id("eff-burn").damagePerTurn(2).duration(2).casterId("target").build();

        burn.onTick(battleState, caster);

        assertThat(caster.getStats().getCurrentHp()).isEqualTo(20);
        assertThat(appliedWard().getAmount()).isEqualTo(3);
        assertThat(battleState.getBattleStats().getPlayersDamageDealt().getOrDefault("target", 0)).isZero();
    }

    @Test
    @DisplayName("expires when its turns run out even if it was never hit")
    void expiresAfterDuration() {
        ward().onApply(battleState);
        ShieldEffect applied = appliedWard();

        assertThat(applied.onTick(battleState, caster)).isFalse();
        assertThat(applied.onTick(battleState, caster)).isFalse();
        assertThat(applied.onTick(battleState, caster)).as("expires on the last tick").isTrue();
        assertThat(lastLog(battleState)).contains("fades");
    }

    @Test
    @DisplayName("re-applying restores the ward instead of stacking a second one")
    void reapplyingRestores() {
        ward().onApply(battleState);
        caster.takeDamage(3);
        appliedWard().onTick(battleState, caster);

        ward().onApply(battleState);

        assertThat(caster.getActiveStatusEffects()).hasSize(1);
        assertThat(appliedWard().getAmount()).isEqualTo(AMOUNT);
        assertThat(appliedWard().getDuration()).isEqualTo(DURATION);
    }

    @Test
    @DisplayName("a hit that is partly absorbed says so in the log")
    void logsTheAbsorbedPart() {
        battleState.getOpponent().getActiveCritter().getActiveStatusEffects()
                .add(ShieldEffect.builder().id("eff-shield").amount(2).duration(3).build());
        DamageEffect strike = DamageEffect.builder().id("eff-strike").description("hits").damage(5).build();

        strike.apply(battleState);

        assertThat(battleState.getOpponent().getActiveCritter().getStats().getCurrentHp()).isEqualTo(17);
        assertThat(lastLog(battleState)).isEqualTo("target's ward absorbs 2 of it!");
    }
}
