package com.chronocritters.user.service;

import org.springframework.stereotype.Service;

import com.chronocritters.lib.model.Critter;
import com.chronocritters.lib.model.Player;
import com.chronocritters.proto.player.PlayerProto.CritterProto;
import com.chronocritters.proto.player.PlayerProto.MatchHistoryRequest;
import com.chronocritters.proto.player.PlayerProto.MatchHistoryResponse;
import com.chronocritters.proto.player.PlayerProto.PlayerRequest;
import com.chronocritters.proto.player.PlayerProto.PlayerResponse;
import com.chronocritters.proto.player.PlayerServiceGrpc.PlayerServiceImplBase;
import com.chronocritters.user.converter.PlayerProtoConverter;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class PlayerGrpcService extends PlayerServiceImplBase {
    
    private final PlayerService playerService;

    @Override
    public void getPlayer(PlayerRequest request, StreamObserver<PlayerResponse> responseObserver) {
        try {
            String playerId = request.getPlayerId();
            Player player = playerService.findById(playerId);
            
            if (player == null) {
                responseObserver.onError(new RuntimeException("Player not found with ID: " + playerId));
                return;
            }

            PlayerResponse.Builder responseBuilder = PlayerResponse.newBuilder()
                    .setId(player.getId())
                    .setUsername(player.getUsername());

            // Convert roster to proto format
            if (player.getRoster() != null) {
                for (Critter critter : player.getRoster()) {
                    CritterProto critterProto = PlayerProtoConverter.convertCritterToProto(critter);
                    responseBuilder.addRoster(critterProto);
                }
            }

            PlayerResponse response = responseBuilder.build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void updateMatchHistory(MatchHistoryRequest request, StreamObserver<MatchHistoryResponse> responseObserver) {
        try {
            String winningPlayerId = request.getWinningPlayerId();
            String losingPlayerId = request.getLosingPlayerId();

            // Update winning player
            Player winningPlayer = playerService.findById(winningPlayerId);
            if (winningPlayer == null) {
                responseObserver.onNext(MatchHistoryResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("Winning player not found with ID: " + winningPlayerId)
                        .build());
                responseObserver.onCompleted();
                return;
            }

            // Update losing player
            Player losingPlayer = playerService.findById(losingPlayerId);
            if (losingPlayer == null) {
                responseObserver.onNext(MatchHistoryResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("Losing player not found with ID: " + losingPlayerId)
                        .build());
                responseObserver.onCompleted();
                return;
            }

            // Update statistics
            winningPlayer.getStats().setWins(winningPlayer.getStats().getWins() + 1);
            losingPlayer.getStats().setLosses(losingPlayer.getStats().getLosses() + 1);

            // Save both players
            playerService.save(winningPlayer);
            playerService.save(losingPlayer);

            MatchHistoryResponse response = MatchHistoryResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Player stats updated successfully.")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            MatchHistoryResponse response = MatchHistoryResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Error updating match history: " + e.getMessage())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}