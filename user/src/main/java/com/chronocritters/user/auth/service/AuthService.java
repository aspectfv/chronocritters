package com.chronocritters.user.auth.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.chronocritters.lib.model.domain.Player;
import com.chronocritters.lib.model.domain.PlayerStats;
import com.chronocritters.lib.util.JwtUtil;
import com.chronocritters.lib.util.PasswordUtil;
import com.chronocritters.user.auth.dto.LoginResponse;
import com.chronocritters.user.auth.dto.User;
import com.chronocritters.user.player.repository.PlayerRepository;
import com.chronocritters.user.player.service.StarterRosterService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PlayerRepository playerRepository;
    private final StarterRosterService starterRosterService;

    public LoginResponse register(String username, String password) {
        String trimmedUsername = username.trim();
        if (playerRepository.findByUsername(trimmedUsername).isPresent()) throw new IllegalArgumentException("Username already taken");

        Player player = Player.builder()
                .username(trimmedUsername)
                .password(PasswordUtil.hashPassword(password))
                .stats(PlayerStats.builder().build())
                .roster(starterRosterService.newRoster())
                .build();

        try {
            playerRepository.save(player);
        } catch (DuplicateKeyException e) {
            // The unique index on username is the authority; the lookup above only
            // saves a round trip in the common case.
            throw new IllegalArgumentException("Username already taken");
        }

        return toLoginResponse(player);
    }

    public LoginResponse login(String username, String password) {
        Player player = playerRepository.findByUsername(username.trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!PasswordUtil.checkPassword(password, player.getPassword())) throw new IllegalArgumentException("Invalid username or password");

        return toLoginResponse(player);
    }

    private LoginResponse toLoginResponse(Player player) {
        return new LoginResponse(
            new User(player.getId(), player.getUsername()),
            JwtUtil.generateToken(player.getId(), player.getUsername())
        );
    }
}
