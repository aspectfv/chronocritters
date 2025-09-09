package com.chronocritters.gamelogic.handler;

import com.chronocritters.gamelogic.event.CritterFaintedEvent;
import com.chronocritters.lib.model.BattleOutcome;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.PlayerState;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public class FaintingHandler extends AbstractTurnActionHandler {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void handle(BattleState battleState) {
        checkPlayerFaint(battleState, battleState.getPlayerOne(), battleState.getPlayerTwo());
        checkPlayerFaint(battleState, battleState.getPlayerTwo(), battleState.getPlayerOne());

        if (battleState.getBattleOutcome() != BattleOutcome.CONTINUE) {
            return;
        }

        handleNext(battleState);
    }

    private void checkPlayerFaint(BattleState battleState, PlayerState player, PlayerState opponent) {
        for (CritterState critter : player.getRoster()) {
            if (critter.getStats().getCurrentHp() <= 0 && !critter.isFainted()) {
                critter.setFainted(true);
                eventPublisher.publishEvent(new CritterFaintedEvent(this, battleState, player, opponent, critter));
            }
        }
    }
}