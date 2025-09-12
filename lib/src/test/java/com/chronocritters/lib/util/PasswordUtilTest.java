package com.chronocritters.lib.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordUtilTest {

    private final String rawPassword = "mySecurePassword123!";

    @Test
    @DisplayName("hashPassword should return a non-empty, valid BCrypt hash string")
    void hashPassword_shouldReturnValidHash() {
        // When
        String hashedPassword = PasswordUtil.hashPassword(rawPassword);

        // Then
        assertThat(hashedPassword).isNotNull().isNotEmpty();
        // A standard BCrypt hash starts with a prefix like $2a$, $2b$, or $2y$
        assertThat(hashedPassword).startsWith("$2a$");
        // Ensure the original password is not stored in plain text
        assertThat(hashedPassword).isNotEqualTo(rawPassword);
    }

    @Test
    @DisplayName("hashPassword should produce different hashes for the same password due to salting")
    void hashPassword_shouldProduceDifferentHashesForSameInput() {
        // When
        String hashedPassword1 = PasswordUtil.hashPassword(rawPassword);
        String hashedPassword2 = PasswordUtil.hashPassword(rawPassword);

        // Then
        assertThat(hashedPassword1).isNotNull();
        assertThat(hashedPassword2).isNotNull();
        assertThat(hashedPassword1).isNotEqualTo(hashedPassword2);
    }

    @Test
    @DisplayName("hashPassword should throw IllegalArgumentException for a null password")
    void hashPassword_withNullPassword_shouldThrowException() {
        // Then
        assertThatThrownBy(() -> PasswordUtil.hashPassword(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("checkPassword should return true for a correct password and its hash")
    void checkPassword_withCorrectPassword_shouldReturnTrue() {
        // Given
        String hashedPassword = PasswordUtil.hashPassword(rawPassword);

        // When
        boolean isMatch = PasswordUtil.checkPassword(rawPassword, hashedPassword);

        // Then
        assertThat(isMatch).isTrue();
    }

    @Test
    @DisplayName("checkPassword should return false for an incorrect password")
    void checkPassword_withIncorrectPassword_shouldReturnFalse() {
        // Given
        String hashedPassword = PasswordUtil.hashPassword(rawPassword);
        String wrongPassword = "wrongPassword!";

        // When
        boolean isMatch = PasswordUtil.checkPassword(wrongPassword, hashedPassword);

        // Then
        assertThat(isMatch).isFalse();
    }

    @Test
    @DisplayName("checkPassword should return false for a null or empty raw password")
    void checkPassword_withNullOrEmptyRawPassword_shouldReturnFalse() {
        // Given
        String hashedPassword = PasswordUtil.hashPassword(rawPassword);

        // When
        boolean isMatchWithNull = PasswordUtil.checkPassword(null, hashedPassword);
        boolean isMatchWithEmpty = PasswordUtil.checkPassword("", hashedPassword);

        // Then
        assertThat(isMatchWithNull).isFalse();
        assertThat(isMatchWithEmpty).isFalse();
    }

    @Test
    @DisplayName("checkPassword should throw IllegalArgumentException for a null hashed password")
    void checkPassword_withNullHashedPassword_shouldThrowException() {
        // Then
        assertThatThrownBy(() -> PasswordUtil.checkPassword(rawPassword, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Hashed password cannot be null");
    }
}