package com.chronocritters.user.player.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.chronocritters.lib.model.domain.Critter;
import com.chronocritters.lib.model.enums.CritterType;
import com.chronocritters.lib.util.TypeAdvantageUtil;
import com.chronocritters.user.player.dto.TypeMatchup;
import com.chronocritters.user.player.repository.CritterRepository;

import lombok.RequiredArgsConstructor;

/**
 * Every critter in the game, in a fixed order.
 *
 * Mongo returns documents in whatever order it happens to hold them, which is
 * stable enough to look correct and unstable enough to reorder a catalogue on a
 * reseed. Type first, then name, so the entries group the way the type chart
 * does.
 */
@Service
@RequiredArgsConstructor
public class CritterCatalogService {
    private static final Comparator<Critter> CATALOG_ORDER =
            Comparator.comparing(Critter::getType).thenComparing(Critter::getName);

    private final CritterRepository critterRepository;

    public List<Critter> findAll() {
        return critterRepository.findAll().stream().sorted(CATALOG_ORDER).toList();
    }

    /**
     * The chart's advantages, read back out of the engine rather than restated.
     * Only the winning side of each pair is returned: the reverse is the same
     * edge seen from the other end, and sending both would let a client draw a
     * chart that disagrees with itself.
     */
    public List<TypeMatchup> typeAdvantages() {
        return Arrays.stream(CritterType.values())
                .flatMap(attacker -> Arrays.stream(CritterType.values())
                        .map(defender -> new TypeMatchup(attacker, defender,
                                TypeAdvantageUtil.getMultiplier(attacker, defender))))
                .filter(matchup -> matchup.multiplier() > 1.0)
                .toList();
    }
}
