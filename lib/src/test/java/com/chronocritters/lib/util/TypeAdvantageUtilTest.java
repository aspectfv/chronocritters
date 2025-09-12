package com.chronocritters.lib.util;

import com.chronocritters.lib.model.CritterType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class TypeAdvantageUtilTest {

    // Test all defined asymmetrical relationships in both directions
    @ParameterizedTest(name = "{0} vs {1} -> {2}")
    @CsvSource({
        "FIRE, GRASS, 1.5",
        "GRASS, FIRE, 0.5",
        "FIRE, METAL, 1.5",
        "METAL, FIRE, 0.5",
        "WATER, FIRE, 1.5",
        "FIRE, WATER, 0.5",
        "WATER, KINETIC, 1.5",
        "KINETIC, WATER, 0.5",
        "GRASS, WATER, 1.5",
        "WATER, GRASS, 0.5",
        "ELECTRIC, WATER, 1.5",
        "WATER, ELECTRIC, 0.5",
        "METAL, ELECTRIC, 1.5",
        "ELECTRIC, METAL, 0.5",
        "TOXIC, GRASS, 1.5",
        "GRASS, TOXIC, 0.5",
        "KINETIC, METAL, 1.5",
        "METAL, KINETIC, 0.5"
    })
    @DisplayName("should return correct multiplier for defined asymmetrical matchups")
    void testDefinedAsymmetricalMatchups(CritterType attacker, CritterType defender, double expectedMultiplier) {
        assertThat(TypeAdvantageUtil.getMultiplier(attacker, defender)).isEqualTo(expectedMultiplier);
    }

    // Test a representative sample of neutral matchups
    @ParameterizedTest(name = "{0} vs {1} -> 1.0")
    @CsvSource({
        "FIRE, FIRE",       // Self-matchup
        "WATER, WATER",     // Self-matchup
        "FIRE, ELECTRIC",   // Unrelated matchup
        "KINETIC, TOXIC"    // Unrelated matchup
    })
    @DisplayName("should return 1.0 for neutral matchups")
    void testNeutralMatchups(CritterType attacker, CritterType defender) {
        assertThat(TypeAdvantageUtil.getMultiplier(attacker, defender)).isEqualTo(1.0);
    }
}