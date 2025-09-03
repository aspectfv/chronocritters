package com.chronocritters.user.service;

import org.springframework.stereotype.Service;

import com.chronocritters.lib.model.Player;
import com.chronocritters.lib.util.JwtUtil;
import com.chronocritters.lib.util.PasswordUtil;
import com.chronocritters.user.dto.User;
import com.chronocritters.user.repository.PlayerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PlayerRepository playerRepository;

    public String register(String username, String password) {
        // Validation
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        
        if (playerRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        // Create and save player
        Player player = new Player();
        player.setUsername(username.trim());
        player.setPassword(PasswordUtil.hashPassword(password));
        playerRepository.save(player);
        
        return "User registered successfully";
    }

    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        
        Player player = playerRepository.findByUsername(username.trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!PasswordUtil.checkPassword(password, player.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        
        return new User(player.getId(), player.getUsername(), JwtUtil.generateToken(player.getId(), player.getUsername()));
    }
}
