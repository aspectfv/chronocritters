package com.chronocritters.lib.context;

import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.PlayerState;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder(toBuilder = true)
public class EffectContext {
    @NonNull
    private final BattleState battleState;

    @NonNull
    private final PlayerState player;

    @NonNull
    private final PlayerState opponent;

    @NonNull
    private final CritterState targetCritter;

    private final CritterState casterCritter;
    private final Ability sourceAbility;
}