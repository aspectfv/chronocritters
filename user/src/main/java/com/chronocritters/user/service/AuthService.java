package com.chronocritters.user.service;

import org.springframework.stereotype.Service;

import com.chronocritters.lib.model.domain.Player;
import com.chronocritters.lib.util.JwtUtil;
import com.chronocritters.lib.util.PasswordUtil;
import com.chronocritters.user.dto.LoginResponse;
import com.chronocritters.user.dto.User;
import com.chronocritters.user.repository.PlayerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PlayerRepository playerRepository;

    public LoginResponse register(String username, String password) {
        if (playerRepository.findByUsername(username.trim()).isPresent()) throw new IllegalArgumentException("Username already taken");

        Player player = new Player();
        player.setUsername(username.trim());
        player.setPassword(PasswordUtil.hashPassword(password));
        playerRepository.save(player);
        
        LoginResponse loginResponse = new LoginResponse(
            new User(player.getId(), player.getUsername()),
            JwtUtil.generateToken(player.getId(), player.getUsername())
        );

        return loginResponse;
    }

    public LoginResponse login(String username, String password) {
        Player player = playerRepository.findByUsername(username.trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!PasswordUtil.checkPassword(password, player.getPassword())) throw new IllegalArgumentException("Invalid username or password");

        User user = new User(player.getId(), player.getUsername());
        return new LoginResponse(user, JwtUtil.generateToken(player.getId(), player.getUsername()));
    }
}
