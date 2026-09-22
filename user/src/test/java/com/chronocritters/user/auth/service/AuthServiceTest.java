package com.chronocritters.user.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chronocritters.lib.model.domain.BaseStats;
import com.chronocritters.lib.model.domain.Critter;
import com.chronocritters.lib.model.domain.Player;
import com.chronocritters.lib.model.domain.PlayerStats;
import com.chronocritters.lib.model.enums.CritterType;
import com.chronocritters.lib.util.JwtUtil;
import com.chronocritters.lib.util.PasswordUtil;
import com.chronocritters.user.auth.dto.LoginResponse;
import com.chronocritters.user.player.repository.PlayerRepository;
import com.chronocritters.user.player.service.StarterRosterService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String USERNAME = "NewTrainer";
    private static final String PASSWORD = "hunter2secret";

    @Mock private PlayerRepository playerRepository;
    @Mock private StarterRosterService starterRosterService;
    @Captor private ArgumentCaptor<Player> playerCaptor;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(playerRepository, starterRosterService);
    }

    private List<Critter> starterRoster() {
        return List.of(Critter.builder()
                .id("water-aqualing")
                .name("Aqualing")
                .type(CritterType.WATER)
                .baseStats(BaseStats.builder().health(5).attack(3).defense(4).build())
                .build());
    }

    private Player existingPlayer() {
        return Player.builder()
                .id("p1")
                .username(USERNAME)
                .password(PasswordUtil.hashPassword(PASSWORD))
                .stats(PlayerStats.builder().build())
                .roster(starterRoster())
                .build();
    }

    @Test
    @DisplayName("a new trainer is given a starter roster so they can battle immediately")
    void registrationGrantsAStarterRoster() {
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(starterRosterService.newRoster()).thenReturn(starterRoster());

        authService.register(USERNAME, PASSWORD);

        verify(playerRepository).save(playerCaptor.capture());
        assertThat(playerCaptor.getValue().getRoster()).hasSize(1);
    }

    @Test
    @DisplayName("a new trainer starts at level one with a zeroed record, not a null one")
    void registrationInitialisesStats() {
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(starterRosterService.newRoster()).thenReturn(starterRoster());

        authService.register(USERNAME, PASSWORD);

        verify(playerRepository).save(playerCaptor.capture());
        PlayerStats stats = playerCaptor.getValue().getStats();
        assertThat(stats).isNotNull();
        assertThat(stats.getLevel()).isEqualTo(1);
        assertThat(stats.getExperience()).isZero();
        assertThat(stats.getWins()).isZero();
        assertThat(stats.getLosses()).isZero();
        assertThat(playerCaptor.getValue().getMatchHistory()).isEmpty();
    }

    @Test
    @DisplayName("the password is stored hashed, never in plain text")
    void registrationHashesThePassword() {
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(starterRosterService.newRoster()).thenReturn(starterRoster());

        authService.register(USERNAME, PASSWORD);

        verify(playerRepository).save(playerCaptor.capture());
        String stored = playerCaptor.getValue().getPassword();
        assertThat(stored).isNotEqualTo(PASSWORD);
        assertThat(PasswordUtil.checkPassword(PASSWORD, stored)).isTrue();
    }

    @Test
    @DisplayName("registration trims the username and returns a usable session token")
    void registrationReturnsASession() {
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(starterRosterService.newRoster()).thenReturn(starterRoster());

        LoginResponse response = authService.register("  " + USERNAME + "  ", PASSWORD);

        verify(playerRepository).save(playerCaptor.capture());
        assertThat(playerCaptor.getValue().getUsername()).isEqualTo(USERNAME);
        assertThat(response.user().username()).isEqualTo(USERNAME);
        assertThat(JwtUtil.validateToken(response.token()).get("username", String.class)).isEqualTo(USERNAME);
    }

    @Test
    @DisplayName("a taken username is rejected without touching the database")
    void registrationRejectsADuplicateUsername() {
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(existingPlayer()));

        assertThatThrownBy(() -> authService.register(USERNAME, PASSWORD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already taken");

        verify(playerRepository, never()).save(any());
    }

    @Test
    @DisplayName("logging in with the right password returns a token for that player")
    void loginSucceedsWithCorrectPassword() {
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(existingPlayer()));

        LoginResponse response = authService.login(USERNAME, PASSWORD);

        assertThat(response.user().id()).isEqualTo("p1");
        assertThat(JwtUtil.validateToken(response.token()).getSubject()).isEqualTo("p1");
    }

    @Test
    @DisplayName("a wrong password and an unknown user fail identically, so neither can be probed")
    void loginFailuresAreIndistinguishable() {
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(existingPlayer()));
        when(playerRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(USERNAME, "wrong-password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid username or password");

        assertThatThrownBy(() -> authService.login("ghost", PASSWORD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    @DisplayName("a username taken between the check and the write is still rejected cleanly")
    void registrationHandlesTheUniqueIndexLosingRace() {
        when(playerRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(starterRosterService.newRoster()).thenReturn(starterRoster());
        when(playerRepository.save(any(Player.class)))
                .thenThrow(new org.springframework.dao.DuplicateKeyException("username already exists"));

        assertThatThrownBy(() -> authService.register(USERNAME, PASSWORD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already taken");
    }

    @Test
    @DisplayName("registration fails loudly if the starter critters are missing from the database")
    void registrationFailsWithoutStarterCritters() {
        when(playerRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(starterRosterService.newRoster()).thenThrow(new IllegalStateException("Starter critters are missing"));

        assertThatThrownBy(() -> authService.register(USERNAME, PASSWORD))
                .isInstanceOf(IllegalStateException.class);

        verify(playerRepository, never()).save(any());
    }
}
