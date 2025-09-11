package com.chronocritters.lib.factory;

import com.chronocritters.lib.context.EffectContext;
import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.EffectType;
import com.chronocritters.lib.model.PlayerState;

public class EffectContextFactory {
    public static EffectContext createContext(EffectType type, BattleState battleState) {
        switch (type) {
            case DAMAGE:
                return createDamageContext(battleState);
            case DAMAGE_OVER_TIME:
                return createDamageOverTimeContext(battleState);
            case SKIP_TURN:
                return createSkipTurnContext(battleState);
        }
        throw new IllegalArgumentException("Unsupported effect type: " + type);
    }

    public static EffectContext createOnTickContext(BattleState battleState, CritterState affectedCritter) {
        boolean isPlayerOneCritter = battleState.getPlayer().getRoster().contains(affectedCritter);

        PlayerState player = isPlayerOneCritter ? battleState.getPlayer() : battleState.getOpponent();
        if (player == null) throw new IllegalStateException("Cannot create EffectContext: Player is not set in BattleState.");
        ;
        PlayerState opponent = isPlayerOneCritter ? battleState.getOpponent() : battleState.getPlayer();
        if (opponent == null) throw new IllegalStateException("Cannot create EffectContext: Opponent player is not set in BattleState.");

        return EffectContext.builder()
            .battleState(battleState)
            .player(player)
            .opponent(opponent)
            .targetCritter(affectedCritter)
            .build();
    }

    private static EffectContext createDamageContext(BattleState battleState) {
        PlayerState player = battleState.getPlayer();
        if (player == null) throw new IllegalStateException("Cannot create EffectContext: Active player is not set in BattleState.");

        PlayerState opponent = battleState.getOpponent();
        if (opponent == null) throw new IllegalStateException("Cannot create EffectContext: Opponent player is not set in BattleState.");

        CritterState casterCritter = player.getActiveCritter();
        if (casterCritter == null) throw new IllegalStateException("Cannot create EffectContext: Active critter is not set in PlayerState.");
        
        CritterState targetCritter = opponent.getActiveCritter();
        if (targetCritter == null) throw new IllegalStateException("Cannot create EffectContext: Active critter is not set in Opponent PlayerState.");

        Ability sourceAbility = player.getActiveCritter().getAbilityById(player.getLastSelectedAbilityId());
        if (sourceAbility == null) throw new IllegalStateException("Cannot create EffectContext: Source ability is not set in PlayerState.");

        return EffectContext.builder()
            .battleState(battleState)
            .player(player)
            .opponent(opponent)
            .casterCritter(casterCritter)
            .targetCritter(targetCritter)
            .sourceAbility(sourceAbility)
            .build();
    }

    private static EffectContext createDamageOverTimeContext(BattleState battleState) {
        PlayerState player = battleState.getPlayer();
        if (player == null) throw new IllegalStateException("Cannot create EffectContext: Active player is not set in BattleState.");

        PlayerState opponent = battleState.getOpponent();
        if (opponent == null) throw new IllegalStateException("Cannot create EffectContext: Opponent player is not set in BattleState.");

        CritterState casterCritter = player.getActiveCritter();
        if (casterCritter == null) throw new IllegalStateException("Cannot create EffectContext: Active critter is not set in PlayerState.");
        
        CritterState targetCritter = opponent.getActiveCritter();
        if (targetCritter == null) throw new IllegalStateException("Cannot create EffectContext: Active critter is not set in Opponent PlayerState.");

        Ability sourceAbility = player.getActiveCritter().getAbilityById(player.getLastSelectedAbilityId());
        if (sourceAbility == null) throw new IllegalStateException("Cannot create EffectContext: Source ability is not set in PlayerState.");

        return EffectContext.builder()
            .battleState(battleState)
            .player(player)
            .opponent(opponent)
            .casterCritter(casterCritter)
            .targetCritter(targetCritter)
            .sourceAbility(sourceAbility)
            .build();
    }

    private static EffectContext createSkipTurnContext(BattleState battleState) {
        PlayerState player = battleState.getPlayer();
        if (player == null) throw new IllegalStateException("Cannot create EffectContext: Active player is not set in BattleState.");

        PlayerState opponent = battleState.getOpponent();
        if (opponent == null) throw new IllegalStateException("Cannot create EffectContext: Opponent player is not set in BattleState.");

        CritterState casterCritter = player.getActiveCritter();
        if (casterCritter == null) throw new IllegalStateException("Cannot create EffectContext: Active critter is not set in PlayerState.");
        
        CritterState targetCritter = opponent.getActiveCritter();
        if (targetCritter == null) throw new IllegalStateException("Cannot create EffectContext: Active critter is not set in Opponent PlayerState.");

        Ability sourceAbility = player.getActiveCritter().getAbilityById(player.getLastSelectedAbilityId());
        if (sourceAbility == null) throw new IllegalStateException("Cannot create EffectContext: Source ability is not set in PlayerState.");

        return EffectContext.builder()
            .battleState(battleState)
            .player(player)
            .opponent(opponent)
            .casterCritter(casterCritter)
            .targetCritter(targetCritter)
            .sourceAbility(sourceAbility)
            .build();
    }
}
