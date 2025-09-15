// --- NEW, SUPERIOR TypeAdvantageUtil.java ---

package com.chronocritters.lib.util;

import java.util.EnumMap;
import java.util.Map;

import com.chronocritters.lib.model.enums.CritterType;

public final class TypeAdvantageUtil {
    private static final Map<CritterType, Map<CritterType, Double>> TYPE_CHART = new EnumMap<>(CritterType.class);

    private static final double NOT_VERY_EFFECTIVE = 0.5;
    private static final double NORMAL_EFFECTIVE = 1.0;
    private static final double SUPER_EFFECTIVE = 1.5;

    static {
        defineAsymmetricalAdvantage(CritterType.FIRE, CritterType.GRASS);
        defineAsymmetricalAdvantage(CritterType.FIRE, CritterType.METAL);
        defineAsymmetricalAdvantage(CritterType.WATER, CritterType.FIRE);
        defineAsymmetricalAdvantage(CritterType.WATER, CritterType.KINETIC);
        defineAsymmetricalAdvantage(CritterType.GRASS, CritterType.WATER);
        defineAsymmetricalAdvantage(CritterType.ELECTRIC, CritterType.WATER);
        defineAsymmetricalAdvantage(CritterType.METAL, CritterType.ELECTRIC);
        defineAsymmetricalAdvantage(CritterType.TOXIC, CritterType.GRASS);
        defineAsymmetricalAdvantage(CritterType.KINETIC, CritterType.METAL);
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
    
    // private static void defineSymmetricalAdvantage(CritterType type1, CritterType type2) {
    //     TYPE_CHART.computeIfAbsent(type1, k -> new EnumMap<>(CritterType.class)).put(type2, SUPER_EFFECTIVE);
    //     TYPE_CHART.computeIfAbsent(type2, k -> new EnumMap<>(CritterType.class)).put(type1, SUPER_EFFECTIVE);
    // }
}