package com.chronocritters.lib.util;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import com.chronocritters.lib.model.CritterType;

public final class TypeAdvantageUtil {
    private static final Map<CritterType, Set<CritterType>> TYPE_ADVANTAGES = new EnumMap<>(CritterType.class);

    private static final double NOT_VERY_EFFECTIVE = 0.5;
    private static final double NORMAL_EFFECTIVE = 1.0;
    private static final double SUPER_EFFECTIVE = 1.5;

    static {
        TYPE_ADVANTAGES.put(CritterType.FIRE, Set.of(CritterType.GRASS, CritterType.METAL));
        TYPE_ADVANTAGES.put(CritterType.WATER, Set.of(CritterType.FIRE, CritterType.KINETIC));
        TYPE_ADVANTAGES.put(CritterType.GRASS, Set.of(CritterType.WATER));
        TYPE_ADVANTAGES.put(CritterType.ELECTRIC, Set.of(CritterType.WATER));
        TYPE_ADVANTAGES.put(CritterType.METAL, Set.of(CritterType.ELECTRIC));
        TYPE_ADVANTAGES.put(CritterType.TOXIC, Set.of(CritterType.GRASS));
        TYPE_ADVANTAGES.put(CritterType.KINETIC, Set.of(CritterType.METAL));
    }

    private TypeAdvantageUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static double getMultiplier(CritterType attacker, CritterType defender) {
        if (TYPE_ADVANTAGES.getOrDefault(attacker, Set.of()).contains(defender)) {
            return SUPER_EFFECTIVE;
        }
        if (TYPE_ADVANTAGES.getOrDefault(defender, Set.of()).contains(attacker)) {
            return NOT_VERY_EFFECTIVE;
        }
        return NORMAL_EFFECTIVE;
    }
}
