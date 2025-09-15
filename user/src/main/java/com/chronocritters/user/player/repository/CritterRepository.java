package com.chronocritters.user.player.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.chronocritters.lib.model.domain.Critter;

@Repository
public interface CritterRepository extends MongoRepository<Critter, String> {
}
