package com.chronocritters.lib.factory;

import java.util.Map;

import com.chronocritters.lib.context.EffectContext;
import com.chronocritters.lib.context.EffectContextType;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.EffectType;

public class EffectContextFactory {
    public static EffectContext createContext(EffectType type, BattleState battleState) {
        switch (type) {
            case DAMAGE:
                return EffectContext.builder()
                    .data(createDamageContext(battleState))
                    .build();
            case DAMAGE_OVER_TIME:
                return EffectContext.builder()
                    .data(createDamageOverTimeContext(battleState))
                    .build();
            default:
                throw new IllegalArgumentException("Unsupported EffectType: " + type);
        }
    }

    private static Map<EffectContextType, Object> createDamageContext(BattleState battleState) {
        return Map.of(
            EffectContextType.BATTLE_STATE, battleState,
            EffectContextType.CASTER_CRITTER, battleState.getPlayer().getActiveCritter(),
            EffectContextType.TARGET_CRITTER, battleState.getOpponent().getActiveCritter()
        );
    }

    private static Map<EffectContextType, Object> createDamageOverTimeContext(BattleState battleState) {
        return Map.of(
            EffectContextType.BATTLE_STATE, battleState
        );
    }
}
