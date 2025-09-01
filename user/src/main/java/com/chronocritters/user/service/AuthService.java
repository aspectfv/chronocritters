package com.chronocritters.user.service;

import org.springframework.stereotype.Service;

import com.chronocritters.user.repository.PlayerRepository;

import lombok.RequiredArgsConstructor;

import com.chronocritters.lib.model.Player;
import com.chronocritters.lib.util.JwtUtil;
import com.chronocritters.lib.util.PasswordUtil;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PlayerRepository playerRepository;

    public String register(String username, String password) {
        if (playerRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        Player player = new Player();
        player.setUsername(username);
        player.setPassword(PasswordUtil.hashPassword(password));
        playerRepository.save(player);
        return "User registered successfully";
    }

    public String login(String username, String password) {
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!PasswordUtil.checkPassword(password, player.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        return JwtUtil.generateToken(username, username);
    }
}
