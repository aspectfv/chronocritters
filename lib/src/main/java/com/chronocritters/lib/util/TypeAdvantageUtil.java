package com.chronocritters.lib.util;

import java.util.EnumMap;
import java.util.Map;

import com.chronocritters.lib.model.enums.CritterType;

public final class TypeAdvantageUtil {
    private static final Map<CritterType, Map<CritterType, Double>> TYPE_CHART = new EnumMap<>(CritterType.class);

    private static final double NOT_VERY_EFFECTIVE = 0.5;
    private static final double NORMAL_EFFECTIVE = 1.0;
    private static final double SUPER_EFFECTIVE = 1.5;

    /**
     * Every type is strong against two others and weak to two others, and is
     * neutral to the remaining two and to itself. With seven types that is the
     * most even chart there is: no type holds more good matchups than another,
     * so a team is chosen for what it covers rather than for which type is
     * simply best.
     */
    static {
        defineAsymmetricalAdvantage(CritterType.FIRE, CritterType.GRASS);
        defineAsymmetricalAdvantage(CritterType.FIRE, CritterType.METAL);
        defineAsymmetricalAdvantage(CritterType.WATER, CritterType.FIRE);
        defineAsymmetricalAdvantage(CritterType.WATER, CritterType.TOXIC);
        defineAsymmetricalAdvantage(CritterType.GRASS, CritterType.WATER);
        defineAsymmetricalAdvantage(CritterType.GRASS, CritterType.KINETIC);
        defineAsymmetricalAdvantage(CritterType.ELECTRIC, CritterType.WATER);
        defineAsymmetricalAdvantage(CritterType.ELECTRIC, CritterType.METAL);
        defineAsymmetricalAdvantage(CritterType.METAL, CritterType.TOXIC);
        defineAsymmetricalAdvantage(CritterType.METAL, CritterType.KINETIC);
        defineAsymmetricalAdvantage(CritterType.TOXIC, CritterType.GRASS);
        defineAsymmetricalAdvantage(CritterType.TOXIC, CritterType.ELECTRIC);
        defineAsymmetricalAdvantage(CritterType.KINETIC, CritterType.FIRE);
        defineAsymmetricalAdvantage(CritterType.KINETIC, CritterType.ELECTRIC);
    }

    private TypeAdvantageUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static double getMultiplier(CritterType attacker, CritterType defender) {
        return TYPE_CHART
                .getOrDefault(attacker, Map.of())
                .getOrDefault(defender, NORMAL_EFFECTIVE);
    }

    private static void defineAsymmetricalAdvantage(CritterType attacker, CritterType defender) {
        TYPE_CHART.computeIfAbsent(attacker, k -> new EnumMap<>(CritterType.class)).put(defender, SUPER_EFFECTIVE);
        TYPE_CHART.computeIfAbsent(defender, k -> new EnumMap<>(CritterType.class)).put(attacker, NOT_VERY_EFFECTIVE);
    }
}
