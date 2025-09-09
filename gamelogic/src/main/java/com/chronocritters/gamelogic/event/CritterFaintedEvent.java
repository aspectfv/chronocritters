package com.chronocritters.gamelogic.event;

import org.springframework.context.ApplicationEvent;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.PlayerState;
import lombok.Getter;

@Getter
public class CritterFaintedEvent extends ApplicationEvent {
    private final BattleState battleState;
    private final PlayerState owner;
    private final PlayerState opponent;
    private final CritterState faintedCritter;

    public CritterFaintedEvent(Object source, BattleState battleState, PlayerState owner, PlayerState opponent, CritterState faintedCritter) {
        super(source);
        this.battleState = battleState;
        this.owner = owner;
        this.opponent = opponent;
        this.faintedCritter = faintedCritter;
    }
}