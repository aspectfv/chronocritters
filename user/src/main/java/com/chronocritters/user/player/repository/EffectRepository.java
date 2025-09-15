package com.chronocritters.user.player.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.chronocritters.lib.model.domain.Effect;

@Repository
public interface EffectRepository extends MongoRepository<Effect, String> {
}
