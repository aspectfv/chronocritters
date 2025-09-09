package com.chronocritters.lib.context;

import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.PlayerState;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExecuteAbilityContext {
    private final BattleState battleState;
    private final PlayerState player;
    private final PlayerState opponent;
    private final CritterState activeCritter;
    private final Ability ability;
}
