package com.chronocritters.lib.interfaces;

import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.CritterState;

public interface IPersistentEffect {
    void onApply(BattleState battleState);
    boolean onTick(BattleState battleState, CritterState target);
}
