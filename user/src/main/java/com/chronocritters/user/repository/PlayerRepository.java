package com.chronocritters.user.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.chronocritters.lib.model.Player;

@Repository
public interface PlayerRepository extends MongoRepository<Player, String> {
    Optional<Player> findByUsername(String username);

    @Query(value = "{ '_id': ?0, 'matchHistory.battleId': ?1 }", fields = "{ 'matchHistory.$': 1 }")
    Optional<Player> findMatchHistoryEntryByPlayerIdAndBattleId(String playerId, String battleId);
}
