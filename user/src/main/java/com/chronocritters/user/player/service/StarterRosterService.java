package com.chronocritters.user.player.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chronocritters.lib.model.domain.Critter;
import com.chronocritters.user.player.repository.CritterRepository;

import lombok.RequiredArgsConstructor;

/**
 * Every new trainer starts with the whole roster. The three critters sit on a
 * closed type triangle, so no starter holds an advantage over the others and
 * the opening choice is about how you want to play rather than what you drew.
 */
@Service
@RequiredArgsConstructor
public class StarterRosterService {
    static final List<String> STARTER_CRITTER_IDS = List.of("grass-sylvansentinel", "fire-searfiend", "water-aqualing");

    private final CritterRepository critterRepository;

    public List<Critter> newRoster() {
        List<Critter> roster = critterRepository.findAllById(STARTER_CRITTER_IDS);

        if (roster.size() != STARTER_CRITTER_IDS.size()) {
            throw new IllegalStateException("Starter critters are missing from the database: expected "
                    + STARTER_CRITTER_IDS + " but found " + roster.stream().map(Critter::getId).toList());
        }

        return roster;
    }
}
