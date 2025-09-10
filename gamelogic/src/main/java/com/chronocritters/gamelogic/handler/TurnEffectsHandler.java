package com.chronocritters.gamelogic.handler;

import com.chronocritters.lib.context.EffectContext;
import com.chronocritters.lib.factory.EffectContextFactory;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.EffectType;
import com.chronocritters.lib.model.PlayerState;
import com.chronocritters.lib.model.effects.Effect;
import com.chronocritters.lib.model.effects.ExecutionType;

import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

            if (effect.getExecutionType() == ExecutionType.PERSISTENT) {
                EffectContext context = EffectContextFactory.createContext(effect.getType(), battleState);
                effect.apply(context);
            }
        }
    }
}