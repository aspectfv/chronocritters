package com.chronocritters.gamelogic.service;

import com.chronocritters.gamelogic.event.CritterFaintedEvent;
import com.chronocritters.lib.model.BattleOutcome;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.PlayerState;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

@Service
public class FaintingService {

    @EventListener
    public void onCritterFainted(CritterFaintedEvent event) {
        PlayerState faintedCritterOwner = event.getOwner();
        CritterState faintedCritter = event.getFaintedCritter();

        int nextCritterIndex = IntStream.range(0, faintedCritterOwner.getRoster().size())
            .filter(i -> faintedCritterOwner.getCritterByIndex(i).getStats().getCurrentHp() > 0)
            .findFirst()
            .orElse(-1);

        if (nextCritterIndex != -1) {
            CritterState nextCritter = faintedCritterOwner.getCritterByIndex(nextCritterIndex);
            faintedCritterOwner.setActiveCritterIndex(nextCritterIndex);

            String faintLog = faintedCritter.getName() + " fainted! " +
                faintedCritterOwner.getUsername() + " sent out " + nextCritter.getName() + "!";
            event.getBattleState().getActionLogHistory().add(faintLog);
        } else {
            String lossLog = faintedCritter.getName() + " fainted! " +
                faintedCritterOwner.getUsername() + " has no more critters! " +
                event.getOpponent().getUsername() + " wins the battle!";
            event.getBattleState().getActionLogHistory().add(lossLog);
            event.getBattleState().setBattleOutcome(BattleOutcome.BATTLE_WON); // The opponent won
        }
    }
}