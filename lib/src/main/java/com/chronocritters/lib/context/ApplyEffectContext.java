package com.chronocritters.lib.context;

import lombok.Builder;
import lombok.Data;

import com.chronocritters.lib.model.ActiveEffect;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.PlayerState;

@Data
@Builder
public class ApplyEffectContext {
    private BattleState battleState;
    private PlayerState player;
    private PlayerState opponent;
    private CritterState targetCritter;
    private ActiveEffect effect;
}
