package com.chronocritters.lib.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import com.chronocritters.lib.model.enums.CritterType;

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
        "WATER, TOXIC, 1.5",
        "TOXIC, WATER, 0.5",
        "GRASS, WATER, 1.5",
        "WATER, GRASS, 0.5",
        "GRASS, KINETIC, 1.5",
        "KINETIC, GRASS, 0.5",
        "ELECTRIC, WATER, 1.5",
        "WATER, ELECTRIC, 0.5",
        "ELECTRIC, METAL, 1.5",
        "METAL, ELECTRIC, 0.5",
        "METAL, TOXIC, 1.5",
        "TOXIC, METAL, 0.5",
        "METAL, KINETIC, 1.5",
        "KINETIC, METAL, 0.5",
        "TOXIC, GRASS, 1.5",
        "GRASS, TOXIC, 0.5",
        "TOXIC, ELECTRIC, 1.5",
        "ELECTRIC, TOXIC, 0.5",
        "KINETIC, FIRE, 1.5",
        "FIRE, KINETIC, 0.5",
        "KINETIC, ELECTRIC, 1.5",
        "ELECTRIC, KINETIC, 0.5"
    })
    @DisplayName("should return correct multiplier for defined asymmetrical matchups")
    void testDefinedAsymmetricalMatchups(CritterType attacker, CritterType defender, double expectedMultiplier) {
        assertThat(TypeAdvantageUtil.getMultiplier(attacker, defender)).isEqualTo(expectedMultiplier);
    }

    /**
     * The seeded roster stands on this loop, so it is pinned here rather than
     * left as a property of three separate chart entries that could each be
     * edited on their own.
     */
    @ParameterizedTest(name = "{0} beats {1}, and {1} does not beat {0}")
    @CsvSource({
        "FIRE, GRASS",
        "GRASS, WATER",
        "WATER, FIRE"
    })
    @DisplayName("the seeded roster's three types form a closed loop")
    void seededRosterTypesFormAClosedLoop(CritterType attacker, CritterType defender) {
        assertThat(TypeAdvantageUtil.getMultiplier(attacker, defender)).isGreaterThan(1.0);
        assertThat(TypeAdvantageUtil.getMultiplier(defender, attacker)).isLessThan(1.0);
    }

    // Test a representative sample of neutral matchups
    @ParameterizedTest(name = "{0} vs {1} -> 1.0")
    @CsvSource({
        "FIRE, FIRE",       // Self-matchup
        "WATER, WATER",     // Self-matchup
        "FIRE, ELECTRIC",   // Unrelated matchup
        "KINETIC, TOXIC",   // Unrelated matchup
        "GRASS, METAL"      // Unrelated matchup
    })
    @DisplayName("should return 1.0 for neutral matchups")
    void testNeutralMatchups(CritterType attacker, CritterType defender) {
        assertThat(TypeAdvantageUtil.getMultiplier(attacker, defender)).isEqualTo(1.0);
    }

    @ParameterizedTest(name = "{0} is strong against two types and weak to two")
    @EnumSource(CritterType.class)
    @DisplayName("every type has as many advantages and weaknesses as every other")
    void chartIsSymmetricInCount(CritterType type) {
        assertThat(typesWhere(defender -> TypeAdvantageUtil.getMultiplier(type, defender) > 1.0)).hasSize(2);
        assertThat(typesWhere(attacker -> TypeAdvantageUtil.getMultiplier(attacker, type) > 1.0)).hasSize(2);
    }

    @Test
    @DisplayName("no matchup is an immunity")
    void noMatchupIsAnImmunity() {
        for (CritterType attacker : CritterType.values()) {
            for (CritterType defender : CritterType.values()) {
                assertThat(TypeAdvantageUtil.getMultiplier(attacker, defender)).isPositive();
            }
        }
    }

    /**
     * Equal counts alone would allow one type's two advantages to land on the
     * types another type is weak to. Strictly better means at least as good
     * against every defender and better against one; no pair may stand like that.
     */
    @Test
    @DisplayName("no type is strictly better than another")
    void noTypeIsStrictlyBetterThanAnother() {
        for (CritterType better : CritterType.values()) {
            for (CritterType worse : CritterType.values()) {
                if (better == worse) continue;

                boolean neverWorse = Arrays.stream(CritterType.values()).allMatch(defender ->
                        TypeAdvantageUtil.getMultiplier(better, defender) >= TypeAdvantageUtil.getMultiplier(worse, defender)
                        && TypeAdvantageUtil.getMultiplier(defender, better) <= TypeAdvantageUtil.getMultiplier(defender, worse));

                assertThat(neverWorse).as("%s is never worse than %s", better, worse).isFalse();
            }
        }
    }

    private static List<CritterType> typesWhere(Predicate<CritterType> matches) {
        return Arrays.stream(CritterType.values()).filter(matches).toList();
    }
}
