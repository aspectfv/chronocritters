package com.chronocritters.lib.mapper;

import com.chronocritters.lib.model.battle.BattleStats;
import com.chronocritters.lib.model.domain.TurnActionEntry;
import com.chronocritters.proto.player.PlayerProto.BattleStatsProto;
import com.chronocritters.proto.player.PlayerProto.TurnActionEntryProto;

public final class BattleStatsMapper {
    private BattleStatsMapper() {}

    public static BattleStats toModel(BattleStatsProto proto) {
        return BattleStats.builder()
            .turnCount(proto.getTurnCount())
            .battleStartTime(proto.getBattleStartTime())
            .duration(proto.getDuration())
            .playersDamageDealt(new java.util.HashMap<>(proto.getPlayersDamageDealtMap()))
            .turnActionHistory(proto.getTurnActionHistoryList().stream().map(BattleStatsMapper::toModel).toList())
            .build();
    }

    public static BattleStatsProto toProto(BattleStats model) {
        BattleStatsProto.Builder builder = BattleStatsProto.newBuilder()
            .setTurnCount(model.getTurnCount())
            .setBattleStartTime(model.getBattleStartTime())
            .setDuration(model.getDuration())
            .putAllPlayersDamageDealt(model.getPlayersDamageDealt());
        model.getTurnActionHistory().forEach(entry -> builder.addTurnActionHistory(toProto(entry)));
        return builder.build();
    }

    private static TurnActionEntry toModel(TurnActionEntryProto proto) {
        return TurnActionEntry.builder()
            .playerId(proto.getPlayerId())
            .playerHasTurn(proto.getPlayerHasTurn())
            .turn(proto.getTurn())
            .turnActionLog(proto.getTurnActionLog())
            .build();
    }

    private static TurnActionEntryProto toProto(TurnActionEntry model) {
        return TurnActionEntryProto.newBuilder()
            .setPlayerId(model.getPlayerId())
            .setPlayerHasTurn(model.isPlayerHasTurn())
            .setTurn(model.getTurn())
            .setTurnActionLog(model.getTurnActionLog())
            .build();
    }
}