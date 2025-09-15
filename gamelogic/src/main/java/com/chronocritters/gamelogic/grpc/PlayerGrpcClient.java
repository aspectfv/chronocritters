package com.chronocritters.gamelogic.grpc;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chronocritters.lib.mapper.PlayerProtoMapper;
import com.chronocritters.lib.model.BattleStats;
import com.chronocritters.proto.player.PlayerProto.BattleRewardsRequest;
import com.chronocritters.proto.player.PlayerProto.BattleRewardsResponse;
import com.chronocritters.proto.player.PlayerProto.MatchHistoryRequest;
import com.chronocritters.proto.player.PlayerProto.MatchHistoryResponse;
import com.chronocritters.proto.player.PlayerProto.PlayerRequest;
import com.chronocritters.proto.player.PlayerProto.PlayerResponse;
import com.chronocritters.proto.player.PlayerServiceGrpc.PlayerServiceBlockingStub;

import net.devh.boot.grpc.client.inject.GrpcClient;

@Service
public class PlayerGrpcClient {
    
    @GrpcClient("user-service")
    private PlayerServiceBlockingStub playerServiceStub;

    public PlayerResponse getPlayer(String playerId) {
        PlayerRequest request = PlayerRequest.newBuilder()
            .setPlayerId(playerId)
            .build();
        
        return playerServiceStub.getPlayer(request);
    }

    public MatchHistoryResponse updateMatchHistory(String winnerId, String loserId, BattleStats battleStats) {
        MatchHistoryRequest request = MatchHistoryRequest.newBuilder()
            .setWinnerId(winnerId)
            .setLoserId(loserId)
            .setBattleStats(PlayerProtoMapper.convertBattleStatsModelToProto(battleStats))
            .build();
        
        return playerServiceStub.updateMatchHistory(request);
    }

    public BattleRewardsResponse getBattleRewards(String winnerId, String loserId, List<String> winnerCrittersIds, List<String> loserCrittersIds) {
        BattleRewardsRequest request = BattleRewardsRequest.newBuilder()
            .setWinnerId(winnerId)
            .setLoserId(loserId)
            .addAllWinnerCritterIds(winnerCrittersIds)
            .addAllLoserCritterIds(loserCrittersIds)
            .build();
        
        return playerServiceStub.getBattleRewards(request);
    }
}