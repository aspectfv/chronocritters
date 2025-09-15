package com.chronocritters.lib.interfaces;

import com.chronocritters.lib.model.battle.BattleState;

public interface ITurnActionHandler {
    ITurnActionHandler setNext(ITurnActionHandler next);
    void handle(BattleState battleState);
}