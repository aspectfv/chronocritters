package com.chronocritters.user.player.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chronocritters.lib.model.domain.BaseStats;
import com.chronocritters.lib.model.domain.Critter;
import com.chronocritters.user.player.repository.CritterRepository;

@ExtendWith(MockitoExtension.class)
class StarterRosterServiceTest {

    @Mock private CritterRepository critterRepository;
    @InjectMocks private StarterRosterService starterRosterService;

    private Critter critter(String id) {
        return Critter.builder()
                .id(id)
                .name(id)
                .baseStats(BaseStats.builder().health(5).attack(3).defense(3).build())
                .build();
    }

    @Test
    @DisplayName("hands out the three seeded starter critters")
    void returnsTheSeededStarters() {
        List<Critter> seeded = StarterRosterService.STARTER_CRITTER_IDS.stream().map(this::critter).toList();
        when(critterRepository.findAllById(StarterRosterService.STARTER_CRITTER_IDS)).thenReturn(seeded);

        List<Critter> roster = starterRosterService.newRoster();

        assertThat(roster).extracting(Critter::getId)
                .containsExactlyElementsOf(StarterRosterService.STARTER_CRITTER_IDS);
    }

    @Test
    @DisplayName("fails loudly rather than handing out a partial roster")
    void rejectsAnIncompleteRoster() {
        when(critterRepository.findAllById(StarterRosterService.STARTER_CRITTER_IDS))
                .thenReturn(List.of(critter(StarterRosterService.STARTER_CRITTER_IDS.get(0))));

        assertThatThrownBy(() -> starterRosterService.newRoster())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Starter critters are missing");
    }
}
