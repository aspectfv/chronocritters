package com.chronocritters.gamelogic.handler;

import com.chronocritters.lib.interfaces.handler.ITurnActionHandler;
import com.chronocritters.lib.model.battle.BattleState;

public abstract class AbstractTurnActionHandler implements ITurnActionHandler {
    protected ITurnActionHandler next;

    @Override
    public ITurnActionHandler setNext(ITurnActionHandler next) {
        this.next = next;
        return next;
    }

    protected void handleNext(BattleState battleState) {
        if (next != null) {
            next.handle(battleState);
        }
    }
}