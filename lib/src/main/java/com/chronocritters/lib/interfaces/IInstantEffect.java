package com.chronocritters.lib.interfaces;

import com.chronocritters.lib.model.BattleState;

public interface IInstantEffect {
    void apply(BattleState battleState);
}
