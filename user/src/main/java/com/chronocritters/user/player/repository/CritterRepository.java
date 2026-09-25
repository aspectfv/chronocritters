package com.chronocritters.user.player.repository;

import java.util.Collection;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.chronocritters.lib.model.domain.Critter;

@Repository
public interface CritterRepository extends MongoRepository<Critter, String> {
    /** Drops whatever the seeder no longer declares, so the collection is exactly the seed. */
    void deleteByIdNotIn(Collection<String> ids);
}
