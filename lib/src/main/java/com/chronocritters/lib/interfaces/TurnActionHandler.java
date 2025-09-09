package com.chronocritters.lib.interfaces;

import com.chronocritters.lib.model.BattleState;

public interface TurnActionHandler {
    TurnActionHandler setNext(TurnActionHandler next);
    void handle(BattleState battleState);
}