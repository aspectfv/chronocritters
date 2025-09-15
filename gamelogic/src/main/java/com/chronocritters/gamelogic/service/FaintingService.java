package com.chronocritters.gamelogic.service;

import com.chronocritters.gamelogic.event.CritterFaintedEvent;
import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.battle.PlayerState;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

@Service
public class FaintingService {

    @EventListener
    public void onCritterFainted(CritterFaintedEvent event) {
        PlayerState faintedCritterOwner = event.getOwner();
        CritterState faintedCritter = event.getFaintedCritter();

        String faintLog = String.format("%s fainted!", faintedCritter.getName());
        event.getBattleState().getActionLogHistory().add(faintLog);

        boolean isActiveCritterFainted = faintedCritter.getId().equals(faintedCritterOwner.getActiveCritter().getId());
        boolean hasOtherCritters = faintedCritterOwner.getRoster().stream().anyMatch(c -> c.getStats().getCurrentHp() > 0);

        if (isActiveCritterFainted && hasOtherCritters) {
            int nextCritterIndex = IntStream.range(0, faintedCritterOwner.getRoster().size())
                .filter(i -> faintedCritterOwner.getCritterByIndex(i).getStats().getCurrentHp() > 0)
                .findFirst()
                .orElse(-1);

            if (nextCritterIndex != -1) {
                faintedCritterOwner.setActiveCritterIndex(nextCritterIndex);
                String switchLog = String.format("%s's %s is sent out!", faintedCritterOwner.getUsername(), faintedCritter.getName());
                event.getBattleState().getActionLogHistory().add(switchLog);
            }
        }
    }
}