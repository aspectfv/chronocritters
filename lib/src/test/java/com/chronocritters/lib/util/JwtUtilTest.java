package com.chronocritters.lib.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {

    private final String userId = UUID.randomUUID().toString();
    private final String username = "testUser";

    @Test
    @DisplayName("generateToken should create a valid, non-empty JWT string")
    void generateToken_shouldReturnValidTokenString() {
        // When
        String token = JwtUtil.generateToken(userId, username);

        // Then
        assertThat(token).isNotNull().isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // A JWT must have three parts
    }

    @Test
    @DisplayName("validateToken should successfully validate a correct token and return its claims")
    void validateToken_withValidToken_shouldReturnCorrectClaims() {
        // Given
        String token = JwtUtil.generateToken(userId, username);

        // When
        Claims claims = JwtUtil.validateToken(token);

        // Then
        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isEqualTo(userId);
        assertThat(claims.get("username", String.class)).isEqualTo(username);
    }

    @Test
    @DisplayName("validateToken should throw SignatureException for a token signed with a different key")
    void validateToken_withInvalidSignature_shouldThrowSignatureException() {
        // Given
        SecretKey differentKey = Keys.hmacShaKeyFor("another-secret-key-that-is-definitely-long-enough".getBytes());
        
        String tokenWithWrongSignature = Jwts.builder()
                .subject(userId)
                .claim("username", username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60)) // Expires in 1 minute
                .signWith(differentKey)
                .compact();

        // Then
        assertThatThrownBy(() -> JwtUtil.validateToken(tokenWithWrongSignature))
                .isInstanceOf(SignatureException.class);
    }

    @Test
    @DisplayName("validateToken should throw ExpiredJwtException for an expired token")
    void validateToken_withExpiredToken_shouldThrowExpiredJwtException() {
        // Given
        long now = System.currentTimeMillis();
        Date pastIssueDate = new Date(now - 1000 * 60 * 10); // Issued 10 minutes ago
        Date pastExpirationDate = new Date(now - 1000 * 60 * 5); // Expired 5 minutes ago

        String expiredToken = Jwts.builder()
                .subject(userId)
                .claim("username", username)
                .issuedAt(pastIssueDate)
                .expiration(pastExpirationDate)
                .signWith(Keys.hmacShaKeyFor("replace-this-with-a-very-long-random-secret-key-32-bytes-min".getBytes()))
                .compact();

        // Then
        assertThatThrownBy(() -> JwtUtil.validateToken(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("validateToken should throw MalformedJwtException for a malformed token string")
    void validateToken_withMalformedToken_shouldThrowMalformedJwtException() {
        // Given
        String malformedToken = "this.is.not.a.valid.jwt";

        // Then
        assertThatThrownBy(() -> JwtUtil.validateToken(malformedToken))
                .isInstanceOf(MalformedJwtException.class);
    }

    @Test
    @DisplayName("validateToken should throw IllegalArgumentException for a null token")
    void validateToken_withNullToken_shouldThrowIllegalArgumentException() {
        // Given
        String nullToken = null;

        // Then
        assertThatThrownBy(() -> JwtUtil.validateToken(nullToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CharSequence cannot be null or empty");
    }
}