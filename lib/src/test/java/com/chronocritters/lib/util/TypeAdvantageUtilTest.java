package com.chronocritters.lib.util;

import com.chronocritters.lib.model.CritterType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class TypeAdvantageUtilTest {

    private static final double SUPER_EFFECTIVE = 1.5;
    private static final double NOT_VERY_EFFECTIVE = 0.5;
    private static final double NORMAL_EFFECTIVE = 1.0;

    // Helper method to access the private static map using reflection
    @SuppressWarnings("unchecked")
    private static Map<CritterType, Set<CritterType>> getAdvantageMap() {
        try {
            Field field = TypeAdvantageUtil.class.getDeclaredField("TYPE_ADVANTAGES");
            field.setAccessible(true);
            return (Map<CritterType, Set<CritterType>>) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to access TYPE_ADVANTAGES map via reflection", e);
            return null; // Should be unreachable
        }
    }

    // 1. Dynamically generates super-effective matchups from the real implementation
    private static Stream<Arguments> superEffectiveMatchups() {
        return getAdvantageMap().entrySet().stream()
                .flatMap(entry -> {
                    CritterType attacker = entry.getKey();
                    return entry.getValue().stream()
                            .map(defender -> Arguments.of(attacker, defender));
                });
    }

    // 2. Dynamically generates not-very-effective matchups by reversing the super-effective ones
    private static Stream<Arguments> notVeryEffectiveMatchups() {
        return superEffectiveMatchups()
                .map(args -> Arguments.of(args.get()[1], args.get()[0])); // Reverse attacker and defender
    }

    // 3. Dynamically generates all remaining matchups as neutral
    private static Stream<Arguments> neutralMatchups() {
        Set<List<CritterType>> nonNeutralMatchups = new HashSet<>();
        superEffectiveMatchups().forEach(args -> nonNeutralMatchups.add(Arrays.asList((CritterType) args.get()[0], (CritterType) args.get()[1])));
        notVeryEffectiveMatchups().forEach(args -> nonNeutralMatchups.add(Arrays.asList((CritterType) args.get()[0], (CritterType) args.get()[1])));

        return Arrays.stream(CritterType.values())
                .flatMap(attacker -> Arrays.stream(CritterType.values())
                        .map(defender -> List.of(attacker, defender))
                )
                .filter(matchup -> !nonNeutralMatchups.contains(matchup))
                .map(matchup -> Arguments.of(matchup.get(0), matchup.get(1)));
    }

    @ParameterizedTest(name = "{0} (attacker) vs {1} (defender) should be SUPER effective")
    @MethodSource("superEffectiveMatchups")
    @DisplayName("getMultiplier should return 1.5 for super-effective matchups")
    void getMultiplier_forAdvantageousMatchups_shouldReturnSuperEffective(CritterType attacker, CritterType defender) {
        // When
        double multiplier = TypeAdvantageUtil.getMultiplier(attacker, defender);

        // Then
        assertThat(multiplier).isEqualTo(SUPER_EFFECTIVE);
    }

    @ParameterizedTest(name = "{0} (attacker) vs {1} (defender) should be NOT VERY effective")
    @MethodSource("notVeryEffectiveMatchups")
    @DisplayName("getMultiplier should return 0.5 for not-very-effective matchups")
    void getMultiplier_forDisadvantageousMatchups_shouldReturnNotVeryEffective(CritterType attacker, CritterType defender) {
        // When
        double multiplier = TypeAdvantageUtil.getMultiplier(attacker, defender);

        // Then
        assertThat(multiplier).isEqualTo(NOT_VERY_EFFECTIVE);
    }

    @ParameterizedTest(name = "{0} (attacker) vs {1} (defender) should be NORMAL effective")
    @MethodSource("neutralMatchups")
    @DisplayName("getMultiplier should return 1.0 for neutral matchups")
    void getMultiplier_forNeutralMatchups_shouldReturnNormalEffective(CritterType attacker, CritterType defender) {
        // When
        double multiplier = TypeAdvantageUtil.getMultiplier(attacker, defender);

        // Then
        assertThat(multiplier).isEqualTo(NORMAL_EFFECTIVE);
    }
}