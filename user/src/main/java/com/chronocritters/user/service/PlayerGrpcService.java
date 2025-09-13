package com.chronocritters.user.service;

import java.util.HashMap;
import java.util.Map;

import com.chronocritters.lib.mapper.PlayerProtoMapper;
import com.chronocritters.lib.model.Critter;
import com.chronocritters.lib.model.Player;
import com.chronocritters.lib.util.ExperienceUtil;
import com.chronocritters.proto.player.PlayerProto.BattleRewardsRequest;
import com.chronocritters.proto.player.PlayerProto.BattleRewardsResponse;
import com.chronocritters.proto.player.PlayerProto.CritterProto;
import com.chronocritters.proto.player.PlayerProto.MatchHistoryRequest;
import com.chronocritters.proto.player.PlayerProto.MatchHistoryResponse;
import com.chronocritters.proto.player.PlayerProto.PlayerRequest;
import com.chronocritters.proto.player.PlayerProto.PlayerResponse;
import com.chronocritters.proto.player.PlayerServiceGrpc.PlayerServiceImplBase;

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
                    CritterProto critterProto = PlayerProtoMapper.convertCritterModelToProto(critter);
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

            Player winningPlayer = playerService.findById(winningPlayerId);
            if (winningPlayer == null) {
                responseObserver.onNext(MatchHistoryResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("Winning player not found with ID: " + winningPlayerId)
                        .build());
                responseObserver.onCompleted();
                return;
            }

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

    @Override
    public void getBattleRewards(BattleRewardsRequest request, StreamObserver<BattleRewardsResponse> responseObserver) {
        try {
            String winnerId = request.getWinnerId();
            String loserId = request.getLoserId();

            Player winner = playerService.findById(winnerId);
            if (winner == null) {
                responseObserver.onError(new RuntimeException("Winner not found with ID: " + winnerId));
                return;
            }

            Player loser = playerService.findById(loserId);
            if (loser == null) {
                responseObserver.onError(new RuntimeException("Loser not found with ID: " + loserId));
                return;
            }

            long winnerExpGain = ExperienceUtil.calculatePlayerXpForWin(winner, loser);
            long loserExpGain = ExperienceUtil.calculatePlayerXpForLoss(loser);
            
            long totalWinnerExp = winner.getExperience() + winnerExpGain;
            while (totalWinnerExp >= ExperienceUtil.getRequiredExpForPlayerLevel(winner.getLevel() + 1)) {
                totalWinnerExp -= ExperienceUtil.getRequiredExpForPlayerLevel(winner.getLevel() + 1);
                winner.setLevel(winner.getLevel() + 1);
            }
            winner.setExperience(totalWinnerExp);

            long totalLoserExp = loser.getExperience() + loserExpGain;
            while (totalLoserExp >= ExperienceUtil.getRequiredExpForPlayerLevel(loser.getLevel() + 1)) {
                totalLoserExp -= ExperienceUtil.getRequiredExpForPlayerLevel(loser.getLevel() + 1);
                loser.setLevel(loser.getLevel() + 1);
            }
            loser.setExperience(totalLoserExp);

            Map<String, Long> playersExpGain = Map.of(
                winnerId, winnerExpGain,
                loserId, loserExpGain
            );

            Map<String, Long> crittersExpGain = new HashMap<>();

            for (Critter critter : winner.getRoster()) {
                long critterExpGain = ExperienceUtil.calculateCritterXpForWin(critter, loser);
                long totalCritterExp = critter.getExperience() + critterExpGain;
                while (totalCritterExp >= ExperienceUtil.getRequiredExpForCritterLevel(critter.getLevel() + 1)) {
                    totalCritterExp -= ExperienceUtil.getRequiredExpForCritterLevel(critter.getLevel() + 1);
                    critter.setLevel(critter.getLevel() + 1);
                }
                critter.setExperience(totalCritterExp);
                crittersExpGain.put(critter.getId(), critterExpGain);
            }

            for (Critter critter : loser.getRoster()) {
                long critterExpGain = ExperienceUtil.calculateCritterXpForLoss(critter);
                long totalCritterExp = critter.getExperience() + critterExpGain;
                while (totalCritterExp >= ExperienceUtil.getRequiredExpForCritterLevel(critter.getLevel() + 1)) {
                    totalCritterExp -= ExperienceUtil.getRequiredExpForCritterLevel(critter.getLevel() + 1);
                    critter.setLevel(critter.getLevel() + 1);
                }
                critter.setExperience(totalCritterExp);
                crittersExpGain.put(critter.getId(), critterExpGain);
            }

            playerService.save(winner);
            playerService.save(loser);

            BattleRewardsResponse response = BattleRewardsResponse.newBuilder()
                    .putAllPlayersExpGained(playersExpGain)
                    .putAllCrittersExpGained(crittersExpGain)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}
