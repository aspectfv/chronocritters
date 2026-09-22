package com.chronocritters.user.auth.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * Every login attempt costs a bcrypt verification, so an unthrottled endpoint is
 * both a credential brute force and a cheap way to saturate a single small VM.
 *
 * Attempts are counted per username. Limiting per source address belongs at the
 * reverse proxy, which is the only place that sees the real one.
 */
@Component
public class AuthAttemptLimiter {
    static final int MAX_ATTEMPTS_PER_WINDOW = 5;
    static final Duration WINDOW = Duration.ofMinutes(1);

    /** Attempts on usernames nobody is using must not grow the map without bound. */
    private static final int MAX_TRACKED_USERNAMES = 10_000;

    private final Map<String, Window> windowsByUsername = new ConcurrentHashMap<>();

    private record Window(Instant startedAt, int attempts) {
        Window next(Instant now) {
            return startedAt.plus(WINDOW).isAfter(now) ? new Window(startedAt, attempts + 1) : new Window(now, 1);
        }

        boolean isExpired(Instant now) {
            return !startedAt.plus(WINDOW).isAfter(now);
        }
    }

    /** Throws once a username has been tried too often inside the window. */
    public void recordAttempt(String username) {
        Instant now = Instant.now();
        evictExpiredWhenCrowded(now);

        Window window = windowsByUsername.compute(username, (key, current) ->
                current == null ? new Window(now, 1) : current.next(now));

        if (window.attempts() > MAX_ATTEMPTS_PER_WINDOW) {
            throw new IllegalStateException("Too many attempts for this username. Try again in a minute.");
        }
    }

    /** A successful sign-in clears the count, so one typo does not follow a player around. */
    public void clear(String username) {
        windowsByUsername.remove(username);
    }

    private void evictExpiredWhenCrowded(Instant now) {
        if (windowsByUsername.size() < MAX_TRACKED_USERNAMES) {
            return;
        }
        windowsByUsername.values().removeIf(window -> window.isExpired(now));
    }
}
