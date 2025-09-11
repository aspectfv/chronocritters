package com.chronocritters.lib.factory;

import java.util.EnumMap;
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
            case SKIP_TURN:
                return EffectContext.builder()
                    .data(createSkipTurnContext(battleState))
                    .build();
        }
        throw new IllegalArgumentException("Unsupported effect type: " + type);
    }

    private static Map<EffectContextType, Object> createDamageContext(BattleState battleState) {
        return new EnumMap<>(Map.of(
            EffectContextType.BATTLE_STATE, battleState,
            EffectContextType.PLAYER, battleState.getPlayer(),
            EffectContextType.OPPONENT, battleState.getOpponent(),
            EffectContextType.CASTER_CRITTER, battleState.getPlayer().getActiveCritter(),
            EffectContextType.TARGET_CRITTER, battleState.getOpponent().getActiveCritter(),
            EffectContextType.ABILITY, battleState.getPlayer().getActiveCritter().getAbilityById(battleState.getPlayer().getLastSelectedAbilityId())
        ));
    }

    private static Map<EffectContextType, Object> createDamageOverTimeContext(BattleState battleState) {
        return new EnumMap<>(Map.of(
            EffectContextType.BATTLE_STATE, battleState,
            EffectContextType.PLAYER, battleState.getPlayer(),
            EffectContextType.OPPONENT, battleState.getOpponent(),
            EffectContextType.CASTER_CRITTER, battleState.getPlayer().getActiveCritter(),
            EffectContextType.TARGET_CRITTER, battleState.getOpponent().getActiveCritter(),
            EffectContextType.ABILITY, battleState.getPlayer().getActiveCritter().getAbilityById(battleState.getPlayer().getLastSelectedAbilityId())
        ));
    }

    private static Map<EffectContextType, Object> createSkipTurnContext(BattleState battleState) {
        return new EnumMap<>(Map.of(
            EffectContextType.BATTLE_STATE, battleState,
            EffectContextType.PLAYER, battleState.getPlayer(),
            EffectContextType.OPPONENT, battleState.getOpponent(),
            EffectContextType.CASTER_CRITTER, battleState.getPlayer().getActiveCritter(),
            EffectContextType.TARGET_CRITTER, battleState.getOpponent().getActiveCritter(),
            EffectContextType.ABILITY, battleState.getPlayer().getActiveCritter().getAbilityById(battleState.getPlayer().getLastSelectedAbilityId())
        ));
    }
}
