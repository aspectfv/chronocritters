package com.chronocritters.gamelogic.grpc;

import org.springframework.stereotype.Service;

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

    public MatchHistoryResponse updateMatchHistory(String winnerId, String loserId) {
        MatchHistoryRequest request = MatchHistoryRequest.newBuilder()
            .setWinningPlayerId(winnerId)
            .setLosingPlayerId(loserId)
            .build();
        
        return playerServiceStub.updateMatchHistory(request);
    }
}