package com.chronocritters.lib.mapper;

import com.chronocritters.lib.model.battle.BattleRewards;
import com.chronocritters.proto.player.PlayerProto.BattleRewardsResponse;

public final class BattleRewardsMapper {
    private BattleRewardsMapper() {}

    public static BattleRewards toModel(BattleRewardsResponse proto) {
        return BattleRewards.builder()
            .playersExpGained(proto.getPlayersExpGainedMap())
            .crittersExpGained(proto.getCrittersExpGainedMap())
            .build();
    }
}
