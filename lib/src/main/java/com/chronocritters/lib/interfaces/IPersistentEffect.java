package com.chronocritters.lib.interfaces;

import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;

public interface IPersistentEffect {
    void onApply(BattleState battleState);
    boolean onTick(BattleState battleState, CritterState target);
}
