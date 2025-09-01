package com.chronocritters.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.chronocritters.lib.Ability;

@Repository
public interface AbilityRepository extends MongoRepository<Ability, String> {
}
