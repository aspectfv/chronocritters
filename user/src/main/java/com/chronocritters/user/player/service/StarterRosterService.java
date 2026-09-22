package com.chronocritters.user.player.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chronocritters.lib.model.domain.Critter;
import com.chronocritters.user.player.repository.CritterRepository;

import lombok.RequiredArgsConstructor;

/**
 * Every new trainer starts with the same three critters, chosen so that no
 * starter has a type advantage over another.
 */
@Service
@RequiredArgsConstructor
public class StarterRosterService {
    static final List<String> STARTER_CRITTER_IDS = List.of("water-aqualing", "fire-searfiend", "metal-cogling");

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
