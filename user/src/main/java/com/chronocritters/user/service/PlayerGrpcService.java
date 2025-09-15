package com.chronocritters.user.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chronocritters.lib.mapper.PlayerProtoMapper;
import com.chronocritters.lib.model.Critter;
import com.chronocritters.lib.model.MatchHistoryEntry;
import com.chronocritters.lib.model.Player;
import com.chronocritters.lib.model.battle.BattleStats;
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
            String battleId = request.getBattleId();
            String winnerId = request.getWinnerId();
            String loserId = request.getLoserId();

            Player winningPlayer = playerService.findById(winnerId);
            if (winningPlayer == null) {
                responseObserver.onNext(MatchHistoryResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("Winning player not found with ID: " + winnerId)
                        .build());
                responseObserver.onCompleted();
                return;
            }

            Player losingPlayer = playerService.findById(loserId);
            if (losingPlayer == null) {
                responseObserver.onNext(MatchHistoryResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("Losing player not found with ID: " + loserId)
                        .build());
                responseObserver.onCompleted();
                return;
            }

            winningPlayer.getStats().setWins(winningPlayer.getStats().getWins() + 1);
            losingPlayer.getStats().setLosses(losingPlayer.getStats().getLosses() + 1);

            List<String> winnerCritterNames = new ArrayList<>();
            for (String critterId : request.getWinnerCritterIdsList()) {
                winningPlayer.getRoster().stream()
                    .filter(c -> c.getId().equals(critterId))
                    .findFirst()
                    .ifPresent(c -> winnerCritterNames.add(c.getName()));
            }

            List<String> loserCritterNames = new ArrayList<>();
            for (String critterId : request.getLoserCritterIdsList()) {
                losingPlayer.getRoster().stream()
                    .filter(c -> c.getId().equals(critterId))
                    .findFirst()
                    .ifPresent(c -> loserCritterNames.add(c.getName()));
            }

            BattleStats battleStats = PlayerProtoMapper.convertToBattleStats(request.getBattleStats());

            MatchHistoryEntry winnerHistoryEntry = MatchHistoryEntry.builder()
                    .battleId(battleId)
                    .winnerId(winnerId)
                    .loserId(loserId)
                    .opponentUsername(losingPlayer.getUsername())
                    .timestamp(Instant.now())
                    .usedCrittersNames(winnerCritterNames)
                    .opponentCrittersNames(loserCritterNames)
                    .turnCount(battleStats.getTurnCount())
                    .duration(battleStats.getDuration())
                    .damageDealt(battleStats.getPlayersDamageDealt().get(winnerId))
                    .damageReceived(battleStats.getPlayersDamageDealt().get(loserId))
                    .turnActionHistory(battleStats.getTurnActionHistory())
                    .build();

            MatchHistoryEntry loserHistoryEntry = MatchHistoryEntry.builder()
                    .battleId(battleId)
                    .winnerId(winnerId)
                    .loserId(loserId)
                    .opponentUsername(winningPlayer.getUsername())
                    .timestamp(Instant.now())
                    .usedCrittersNames(loserCritterNames)
                    .opponentCrittersNames(winnerCritterNames)
                    .turnCount(battleStats.getTurnCount())
                    .duration(battleStats.getDuration())
                    .damageDealt(battleStats.getPlayersDamageDealt().get(loserId))
                    .damageReceived(battleStats.getPlayersDamageDealt().get(winnerId))
                    .turnActionHistory(battleStats.getTurnActionHistory())
                    .build();

            
            winningPlayer.getMatchHistory().add(winnerHistoryEntry);
            losingPlayer.getMatchHistory().add(loserHistoryEntry);
                    
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
            
            long totalWinnerExp = winner.getStats().getExperience() + winnerExpGain;
            while (totalWinnerExp >= ExperienceUtil.getRequiredExpForPlayerLevel(winner.getStats().getLevel() + 1)) {
                totalWinnerExp -= ExperienceUtil.getRequiredExpForPlayerLevel(winner.getStats().getLevel() + 1);
                winner.getStats().setLevel(winner.getStats().getLevel() + 1);
            }
            winner.getStats().setExperience(totalWinnerExp);

            long totalLoserExp = loser.getStats().getExperience() + loserExpGain;
            while (totalLoserExp >= ExperienceUtil.getRequiredExpForPlayerLevel(loser.getStats().getLevel() + 1)) {
                totalLoserExp -= ExperienceUtil.getRequiredExpForPlayerLevel(loser.getStats().getLevel() + 1);
                loser.getStats().setLevel(loser.getStats().getLevel() + 1);
            }
            loser.getStats().setExperience(totalLoserExp);

            Map<String, Long> playersExpGain = Map.of(
                winnerId, winnerExpGain,
                loserId, loserExpGain
            );

            Map<String, Long> crittersExpGain = new HashMap<>();

            for (Critter critter : winner.getRoster()) {
                long critterExpGain = ExperienceUtil.calculateCritterXpForWin(critter, loser);
                long totalCritterExp = critter.getBaseStats().getExperience() + critterExpGain;
                while (totalCritterExp >= ExperienceUtil.getRequiredExpForCritterLevel(critter.getBaseStats().getLevel() + 1)) {
                    totalCritterExp -= ExperienceUtil.getRequiredExpForCritterLevel(critter.getBaseStats().getLevel() + 1);
                    critter.getBaseStats().setLevel(critter.getBaseStats().getLevel() + 1);
                }
                critter.getBaseStats().setExperience(totalCritterExp);
                crittersExpGain.put(critter.getId(), critterExpGain);
            }

            for (Critter critter : loser.getRoster()) {
                long critterExpGain = ExperienceUtil.calculateCritterXpForLoss(critter);
                long totalCritterExp = critter.getBaseStats().getExperience() + critterExpGain;
                while (totalCritterExp >= ExperienceUtil.getRequiredExpForCritterLevel(critter.getBaseStats().getLevel() + 1)) {
                    totalCritterExp -= ExperienceUtil.getRequiredExpForCritterLevel(critter.getBaseStats().getLevel() + 1);
                    critter.getBaseStats().setLevel(critter.getBaseStats().getLevel() + 1);
                }
                critter.getBaseStats().setExperience(totalCritterExp);
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
