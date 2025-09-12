package com.chronocritters.lib.util;

import com.chronocritters.lib.model.CritterType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class TypeAdvantageUtilTest {

    private static final double SUPER_EFFECTIVE = 1.5;
    private static final double NOT_VERY_EFFECTIVE = 0.5;
    private static final double NORMAL_EFFECTIVE = 1.0;

    // Provides all super-effective matchup combinations
    private static Stream<Arguments> superEffectiveMatchups() {
        return Stream.of(
                Arguments.of(CritterType.FIRE, CritterType.GRASS),
                Arguments.of(CritterType.FIRE, CritterType.METAL),
                Arguments.of(CritterType.WATER, CritterType.FIRE),
                Arguments.of(CritterType.WATER, CritterType.KINETIC),
                Arguments.of(CritterType.GRASS, CritterType.WATER),
                Arguments.of(CritterType.ELECTRIC, CritterType.WATER),
                Arguments.of(CritterType.METAL, CritterType.ELECTRIC),
                Arguments.of(CritterType.TOXIC, CritterType.GRASS),
                Arguments.of(CritterType.KINETIC, CritterType.METAL)
        );
    }

    // Provides all not-very-effective matchup combinations (the reverse of super-effective)
    private static Stream<Arguments> notVeryEffectiveMatchups() {
        return Stream.of(
                Arguments.of(CritterType.GRASS, CritterType.FIRE),
                Arguments.of(CritterType.METAL, CritterType.FIRE),
                Arguments.of(CritterType.FIRE, CritterType.WATER),
                Arguments.of(CritterType.KINETIC, CritterType.WATER),
                Arguments.of(CritterType.WATER, CritterType.GRASS),
                Arguments.of(CritterType.WATER, CritterType.ELECTRIC),
                Arguments.of(CritterType.ELECTRIC, CritterType.METAL),
                Arguments.of(CritterType.GRASS, CritterType.TOXIC),
                Arguments.of(CritterType.METAL, CritterType.KINETIC)
        );
    }

    // Provides a representative set of neutral matchups
    private static Stream<Arguments> neutralMatchups() {
        return Stream.of(
                // A type against itself
                Arguments.of(CritterType.FIRE, CritterType.FIRE),
                Arguments.of(CritterType.WATER, CritterType.WATER),
                Arguments.of(CritterType.KINETIC, CritterType.KINETIC),
                // Unrelated types
                Arguments.of(CritterType.FIRE, CritterType.ELECTRIC),
                Arguments.of(CritterType.TOXIC, CritterType.WATER),
                Arguments.of(CritterType.METAL, CritterType.GRASS)
        );
    }

    @ParameterizedTest
    @MethodSource("superEffectiveMatchups")
    @DisplayName("getMultiplier should return 1.5 for super-effective matchups")
    void getMultiplier_forAdvantageousMatchups_shouldReturnSuperEffective(CritterType attacker, CritterType defender) {
        // When
        double multiplier = TypeAdvantageUtil.getMultiplier(attacker, defender);

        // Then
        assertThat(multiplier).isEqualTo(SUPER_EFFECTIVE);
    }

    @ParameterizedTest
    @MethodSource("notVeryEffectiveMatchups")
    @DisplayName("getMultiplier should return 0.5 for not-very-effective matchups")
    void getMultiplier_forDisadvantageousMatchups_shouldReturnNotVeryEffective(CritterType attacker, CritterType defender) {
        // When
        double multiplier = TypeAdvantageUtil.getMultiplier(attacker, defender);

        // Then
        assertThat(multiplier).isEqualTo(NOT_VERY_EFFECTIVE);
    }

    @ParameterizedTest
    @MethodSource("neutralMatchups")
    @DisplayName("getMultiplier should return 1.0 for neutral matchups")
    void getMultiplier_forNeutralMatchups_shouldReturnNormalEffective(CritterType attacker, CritterType defender) {
        // When
        double multiplier = TypeAdvantageUtil.getMultiplier(attacker, defender);

        // Then
        assertThat(multiplier).isEqualTo(NORMAL_EFFECTIVE);
    }
}