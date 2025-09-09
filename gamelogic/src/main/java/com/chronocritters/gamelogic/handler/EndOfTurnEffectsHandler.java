package com.chronocritters.gamelogic.handler;

import com.chronocritters.lib.context.ApplyEffectContext;
import com.chronocritters.lib.interfaces.EffectStrategy;
import com.chronocritters.lib.model.ActiveEffect;
import com.chronocritters.lib.model.BattleState;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.EffectType;
import com.chronocritters.lib.model.PlayerState;

import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class EndOfTurnEffectsHandler extends AbstractTurnActionHandler {
    private final Map<EffectType, EffectStrategy> effectStrategies;

    @Override
    public void handle(BattleState battleState) {
        for (PlayerState playerState : List.of(battleState.getPlayerOne(), battleState.getPlayerTwo())) {
            for (CritterState critter : playerState.getRoster()) {
                if (critter.getStats().getCurrentHp() > 0) {
                    applyEffectsToCritter(battleState, critter);
                }
            }
        }

        if (next != null) {
            next.handle(battleState);
        }
    }

    private void applyEffectsToCritter(BattleState battleState, CritterState critter) {
        Iterator<ActiveEffect> iterator = critter.getActiveEffects().iterator();
        while (iterator.hasNext()) {
            ActiveEffect effect = iterator.next();
            EffectStrategy strategy = effectStrategies.get(effect.getType());
            if (strategy != null) {
                ApplyEffectContext context = ApplyEffectContext.builder()
                        .battleState(battleState)
                        .targetCritter(critter)
                        .effect(effect)
                        .build();
                strategy.applyActiveEffect(context);
            }

            effect.setRemainingDuration(effect.getRemainingDuration() - 1);
            if (effect.getRemainingDuration() <= 0) {
                String effectEndLog = String.format("The %s on %s wore off.", effect.getName(), critter.getName());
                battleState.getActionLogHistory().add(effectEndLog);
                iterator.remove();
            }
        }
    }
}