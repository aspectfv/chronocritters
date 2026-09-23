package com.chronocritters.gamelogic.service;

import com.chronocritters.gamelogic.event.CritterFaintedEvent;
import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.battle.PlayerState;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Losing your active critter used to send out whichever replacement happened to
 * sit first in the roster, which threw away the one decision the type chart
 * makes interesting. The owner is asked instead, and the choice costs no turn.
 */
@Service
public class FaintingService {

    @EventListener
    public void onCritterFainted(CritterFaintedEvent event) {
        BattleState battleState = event.getBattleState();
        PlayerState owner = event.getOwner();
        CritterState faintedCritter = event.getFaintedCritter();

        battleState.getActionLogHistory().add(String.format("%s fainted!", faintedCritter.getName()));

        boolean isActiveCritterFainted = faintedCritter.getId().equals(owner.getActiveCritter().getId());
        boolean hasOtherCritters = owner.getRoster().stream().anyMatch(critter -> critter.getStats().getCurrentHp() > 0);

        if (isActiveCritterFainted && hasOtherCritters) {
            battleState.setAwaitingSwitchPlayerId(owner.getId());
            battleState.getActionLogHistory().add(String.format("%s must send out another critter!", owner.getUsername()));
        }
    }

    /** Used when a player lets the clock run out on their replacement choice. */
    public static int firstLivingCritterIndex(PlayerState player) {
        for (int index = 0; index < player.getRoster().size(); index++) {
            if (player.getCritterByIndex(index).getStats().getCurrentHp() > 0) {
                return index;
            }
        }
        return -1;
    }
}
