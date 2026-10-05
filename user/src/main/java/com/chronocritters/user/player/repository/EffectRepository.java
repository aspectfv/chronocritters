package com.chronocritters.user.player.repository;

import java.util.Collection;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.chronocritters.lib.model.domain.Effect;

@Repository
public interface EffectRepository extends MongoRepository<Effect, String> {
    /** Drops whatever the seeder no longer declares, so the collection is exactly the seed. */
    void deleteByIdNotIn(Collection<String> ids);
}
