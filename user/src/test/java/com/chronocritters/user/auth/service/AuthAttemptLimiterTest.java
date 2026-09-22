package com.chronocritters.user.auth.service;

import static com.chronocritters.user.auth.service.AuthAttemptLimiter.MAX_ATTEMPTS_PER_WINDOW;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthAttemptLimiterTest {

    private static final String USERNAME = "NewTrainer";

    private AuthAttemptLimiter authAttemptLimiter;

    @BeforeEach
    void setUp() {
        authAttemptLimiter = new AuthAttemptLimiter();
    }

    private void exhaustTheWindow() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS_PER_WINDOW; attempt++) {
            authAttemptLimiter.recordAttempt(USERNAME);
        }
    }

    @Test
    @DisplayName("allows a handful of attempts before refusing")
    void allowsAHandfulOfAttempts() {
        assertThatCode(this::exhaustTheWindow).doesNotThrowAnyException();

        assertThatThrownBy(() -> authAttemptLimiter.recordAttempt(USERNAME))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Too many attempts");
    }

    @Test
    @DisplayName("a successful sign-in clears the count")
    void successClearsTheCount() {
        exhaustTheWindow();

        authAttemptLimiter.clear(USERNAME);

        assertThatCode(() -> authAttemptLimiter.recordAttempt(USERNAME)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("one username running out does not lock anybody else out")
    void limitsEachUsernameSeparately() {
        exhaustTheWindow();

        assertThatCode(() -> authAttemptLimiter.recordAttempt("SomeoneElse")).doesNotThrowAnyException();
    }
}
