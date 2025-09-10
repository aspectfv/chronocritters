package com.chronocritters.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.chronocritters.lib.model.effects.Effect;

@Repository
public interface EffectRepository extends MongoRepository<Effect, String> {
}
