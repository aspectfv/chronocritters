package com.chronocritters.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.chronocritters.lib.model.Player;

@Repository
public interface PlayerRepository extends MongoRepository<Player, String> {
}
