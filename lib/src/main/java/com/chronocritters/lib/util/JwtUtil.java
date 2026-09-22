package com.chronocritters.lib.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

public final class JwtUtil {
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(resolveSecret().getBytes());
    private static final long EXPIRATION_MS = 86400000; // 1 day

    private static final String BEARER_PREFIX = "Bearer ";

    private JwtUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Every service signing or verifying tokens must share the same JWT_SECRET value.
    private static String resolveSecret() {
        String secret = System.getenv("JWT_SECRET");
        if (secret == null || secret.isBlank()) {
            return "replace-this-with-a-very-long-random-secret-key-32-bytes-min";
        }
        if (secret.getBytes().length < 32) {
            throw new IllegalStateException("JWT_SECRET must be at least 32 bytes long");
        }
        return secret;
    }

    /** Exposed so tests can sign tokens with the same key the running service uses. */
    static SecretKey secretKey() {
        return SECRET_KEY;
    }

    public static String generateToken(String userId, String username) {
        return Jwts.builder()
                .subject(userId)
                .claim("username", username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Resolves the authenticated player id from an {@code Authorization} header.
     * Throws when the header is missing, malformed, or carries an invalid token.
     */
    public static String playerIdFromAuthHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new IllegalArgumentException("Authorization header must start with 'Bearer '");
        }

        String playerId = validateToken(authorizationHeader.substring(BEARER_PREFIX.length())).getSubject();

        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("JWT token must contain a valid user ID");
        }

        return playerId;
    }
}
