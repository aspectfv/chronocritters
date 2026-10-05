package com.chronocritters.lib.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.domain.BaseStats;
import com.chronocritters.lib.model.domain.Critter;
import com.chronocritters.lib.model.enums.CritterType;
import com.chronocritters.proto.player.PlayerProto.CritterProto;

class CritterMapperTest {

    private static Critter critter(int level) {
        return Critter.builder()
                .id("fire-test")
                .name("Test Critter")
                .description("for the mapper")
                .type(CritterType.FIRE)
                .baseStats(BaseStats.builder().health(10).attack(10).defense(10).speed(10).level(level).build())
                .abilities(List.of())
                .build();
    }

    @Test
    @DisplayName("leaves a level one critter on its base stats")
    void doesNotScaleAtLevelOne() {
        CritterState state = CritterMapper.toCritterState(critter(1));

        assertThat(state.getStats().getMaxHp()).isEqualTo(10);
        assertThat(state.getStats().getCurrentAtk()).isEqualTo(10);
        assertThat(state.getStats().getCurrentDef()).isEqualTo(10);
        assertThat(state.getStats().getCurrentSpd()).isEqualTo(10);
    }

    @Test
    @DisplayName("grows every battle stat by eight percent of base per level")
    void scalesEveryStatByLevel() {
        CritterState state = CritterMapper.toCritterState(critter(11));

        // Ten levels gained, so 10 * (1 + 0.08 * 10).
        assertThat(state.getStats().getMaxHp()).isEqualTo(18);
        assertThat(state.getStats().getCurrentAtk()).isEqualTo(18);
        assertThat(state.getStats().getCurrentDef()).isEqualTo(18);
        assertThat(state.getStats().getCurrentSpd()).isEqualTo(18);
    }

    @Test
    @DisplayName("starts a critter at full health whatever its level")
    void startsAtFullHealth() {
        CritterState state = CritterMapper.toCritterState(critter(7));

        assertThat(state.getStats().getCurrentHp()).isEqualTo(state.getStats().getMaxHp());
    }

    @Test
    @DisplayName("reads a critter with no level as level one rather than scaling it down")
    void treatsAMissingLevelAsLevelOne() {
        CritterState state = CritterMapper.toCritterState(critter(0));

        assertThat(state.getStats().getMaxHp()).isEqualTo(10);
        assertThat(state.getStats().getCurrentAtk()).isEqualTo(10);
    }

    @Test
    @DisplayName("carries speed and level over the wire, so gamelogic can scale what it is sent")
    void roundTripsSpeedAndLevel() {
        CritterProto proto = CritterMapper.toProto(critter(9));

        assertThat(proto.getBaseStats().getSpeed()).isEqualTo(10);
        assertThat(proto.getBaseStats().getLevel()).isEqualTo(9);

        BaseStats returned = CritterMapper.toModel(proto).getBaseStats();

        assertThat(returned.getSpeed()).isEqualTo(10);
        assertThat(returned.getLevel()).isEqualTo(9);
    }
}
