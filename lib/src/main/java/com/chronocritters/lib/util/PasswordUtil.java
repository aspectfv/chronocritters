package com.chronocritters.lib.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String hashPassword(String password) {
        return encoder.encode(password);
    }

    public static boolean checkPassword(String rawPassword, String hashedPassword) {
        if (hashedPassword == null) throw new IllegalArgumentException("Hashed password cannot be null");
        if (rawPassword == null) return false;
        return encoder.matches(rawPassword, hashedPassword);
    }
}
