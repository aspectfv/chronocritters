package com.chronocritters.gamelogic.handler;

import com.chronocritters.lib.interfaces.effects.IPersistentEffect;
import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.battle.PlayerState;
import com.chronocritters.lib.model.domain.Effect;

import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
public class TurnEffectsHandler extends AbstractTurnActionHandler {

    @Override
    public void handle(BattleState battleState) {
        for (PlayerState playerState : List.of(battleState.getPlayerOne(), battleState.getPlayerTwo())) {
            for (CritterState critter : playerState.getRoster()) {
                if (critter.getStats().getCurrentHp() > 0) {
                    applyCritterStatusEffects(battleState, critter);
                }
            }
        }

        handleNext(battleState);
    }

    private void applyCritterStatusEffects(BattleState battleState, CritterState critter) {
        Iterator<Effect> iterator = critter.getActiveStatusEffects().iterator();
        while (iterator.hasNext()) {
            Effect effect = iterator.next();

            if (effect instanceof IPersistentEffect persistentEffect) {
                boolean isExpired = persistentEffect.onTick(battleState, critter);
                if (isExpired) iterator.remove();
            }
        }
    }
}