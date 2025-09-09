package com.chronocritters.gamelogic.handler;

import com.chronocritters.lib.interfaces.TurnActionHandler;
import com.chronocritters.lib.model.BattleState;

public abstract class AbstractTurnActionHandler implements TurnActionHandler {
    protected TurnActionHandler next;

    @Override
    public TurnActionHandler setNext(TurnActionHandler next) {
        this.next = next;
        return next;
    }

    protected void handleNext(BattleState battleState) {
        if (next != null) {
            next.handle(battleState);
        }
    }
}