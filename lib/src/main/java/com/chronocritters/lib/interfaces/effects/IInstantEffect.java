package com.chronocritters.lib.interfaces.effects;

import com.chronocritters.lib.model.battle.BattleState;

public interface IInstantEffect {
    void apply(BattleState battleState);
}
