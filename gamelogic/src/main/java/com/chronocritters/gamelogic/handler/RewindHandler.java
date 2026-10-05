package com.chronocritters.gamelogic.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.BattleStats;
import com.chronocritters.lib.model.battle.CritterSnapshot;
import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.battle.PlayerState;
import com.chronocritters.lib.model.domain.Ability;
import com.chronocritters.lib.model.domain.TurnActionEntry;
import com.chronocritters.lib.model.effects.StatusEffect;

import lombok.RequiredArgsConstructor;

/**
 * Puts the acting player's critter back the way it stood two turns ago: its
 * health, its status condition and its cooldowns. Nothing on the opponent's
 * side moves, their cooldowns included.
 *
 * Rolling back numbers nobody can see would read as the game glitching, so the
 * log names every thing that changed, in the order a player would look for it:
 * the health bar, then the ailment, then the moves.
 */
@RequiredArgsConstructor
public class RewindHandler extends AbstractTurnActionHandler {
    private final CritterSnapshot target;

    @Override
    public void handle(BattleState battleState) {
        PlayerState player = battleState.getPlayer();
        CritterState critter = player.getActiveCritter();
        List<String> log = battleState.getActionLogHistory();

        log.add(String.format("%s rewinds %s %d turns!", player.getUsername(), critter.getName(), BattleState.REWIND_TURNS));

        List<String> changes = new ArrayList<>();
        changes.addAll(restoreHealth(critter));
        changes.addAll(restoreStatus(critter));
        changes.addAll(restoreCooldowns(critter));

        if (changes.isEmpty()) {
            log.add(String.format("Nothing about %s had changed since then.", critter.getName()));
        } else {
            log.addAll(changes);
        }

        player.setRewindUsed(true);
        battleState.setLastTurnResult(null);

        BattleStats battleStats = battleState.getBattleStats();
        battleStats.getTurnActionHistory().add(TurnActionEntry.builder()
                .playerId(player.getId())
                .playerHasTurn(true)
                .turn(battleStats.getTurnCount())
                .turnActionLog(String.format("%s rewound %d turns", critter.getName(), BattleState.REWIND_TURNS))
                .build());

        handleNext(battleState);
    }

    private List<String> restoreHealth(CritterState critter) {
        int now = critter.getStats().getCurrentHp();
        int then = Math.min(target.currentHp(), critter.getStats().getMaxHp());
        if (now == then) return List.of();

        critter.getStats().setCurrentHp(then);
        return List.of(String.format("%s's health %s from %d to %d.",
                critter.getName(), then > now ? "flows back" : "drains back", now, then));
    }

    private List<String> restoreStatus(CritterState critter) {
        StatusEffect now = critter.getStatus();
        StatusEffect then = target.status();
        critter.setStatus(then == null ? null : then.copy());

        List<String> changes = new ArrayList<>();
        if (now != null && (then == null || now.getCondition() != then.getCondition())) {
            changes.add(String.format("%s is no longer %s.", critter.getName(), now.getCondition().adjective()));
        }
        if (then != null && (now == null || now.getCondition() != then.getCondition())) {
            changes.add(String.format("%s is %s again.", critter.getName(), then.getCondition().adjective()));
        }
        return changes;
    }

    private List<String> restoreCooldowns(CritterState critter) {
        Map<String, Integer> now = critter.getAbilityCooldowns();
        Map<String, Integer> then = target.abilityCooldowns();

        List<String> changes = new ArrayList<>();
        for (Ability ability : critter.getAbilities()) {
            boolean coolingNow = now.containsKey(ability.getId());
            boolean coolingThen = then.containsKey(ability.getId());
            if (coolingNow && !coolingThen) {
                changes.add(String.format("%s is ready again.", ability.getName()));
            } else if (!coolingNow && coolingThen) {
                changes.add(String.format("%s is cooling down again.", ability.getName()));
            }
        }

        critter.setAbilityCooldowns(new HashMap<>(then));
        return changes;
    }
}
