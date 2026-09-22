package com.chronocritters.lib.testsupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.BattleStats;
import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.battle.CurrentStats;
import com.chronocritters.lib.model.battle.PlayerState;
import com.chronocritters.lib.model.domain.Ability;
import com.chronocritters.lib.model.domain.Effect;
import com.chronocritters.lib.model.enums.CritterType;

/** Builders for the battle objects the engine tests operate on. */
public final class BattleFixtures {
    public static final String PLAYER_ONE_ID = "p1";
    public static final String PLAYER_TWO_ID = "p2";

    private BattleFixtures() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static CritterState critter(String id, CritterType type, int hp, int attack, int defense, Ability... abilities) {
        return CritterState.builder()
                .id(id)
                .name(id)
                .type(type)
                .stats(CurrentStats.builder().maxHp(hp).currentHp(hp).currentAtk(attack).currentDef(defense).build())
                .abilities(new ArrayList<>(Arrays.asList(abilities)))
                .activeStatusEffects(new ArrayList<>())
                .build();
    }

    public static Ability ability(String id, Effect... effects) {
        return Ability.builder()
                .id(id)
                .name(id)
                .description(id)
                .effects(new ArrayList<>(Arrays.asList(effects)))
                .build();
    }

    public static PlayerState player(String id, CritterState... roster) {
        return PlayerState.builder()
                .id(id)
                .username(id + "-trainer")
                .hasTurn(false)
                .activeCritterIndex(0)
                .roster(new ArrayList<>(Arrays.asList(roster)))
                .build();
    }

    /** A battle with playerOne to move. */
    public static BattleState battle(PlayerState playerOne, PlayerState playerTwo) {
        playerOne.setHasTurn(true);
        playerTwo.setHasTurn(false);

        Map<String, Integer> damageDealt = new HashMap<>();
        damageDealt.put(playerOne.getId(), 0);
        damageDealt.put(playerTwo.getId(), 0);

        return BattleState.builder()
                .battleId("battle-1")
                .activePlayerId(playerOne.getId())
                .playerOne(playerOne)
                .playerTwo(playerTwo)
                .actionLogHistory(new ArrayList<>())
                .timeRemaining(30)
                .battleStats(BattleStats.builder()
                        .playersDamageDealt(damageDealt)
                        .turnActionHistory(new ArrayList<>())
                        .build())
                .build();
    }

    public static String lastLog(BattleState battleState) {
        List<String> log = battleState.getActionLogHistory();
        return log.isEmpty() ? "" : log.get(log.size() - 1);
    }
}
