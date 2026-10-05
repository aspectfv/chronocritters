package com.chronocritters.lib.util;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.util.retry.Retry;

/**
 * The retry policy every service-to-service call uses: a service that is still
 * starting up, or briefly unreachable, should not cost a battle its turn.
 * Client errors are never retried; the caller sent something wrong.
 */
public final class ServiceRetry {
    private static final Logger logger = LoggerFactory.getLogger(ServiceRetry.class);

    private static final int MAX_ATTEMPTS = 3;
    private static final Duration FIRST_BACKOFF = Duration.ofMillis(500);

    private ServiceRetry() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Retry backoff(String serviceName) {
        return Retry.backoff(MAX_ATTEMPTS, FIRST_BACKOFF)
                .filter(ServiceRetry::isRetryable)
                .doBeforeRetry(retrySignal -> logger.warn("Retrying request to {} after failure: attempt #{}, error: {}",
                        serviceName,
                        retrySignal.totalRetries() + 1,
                        retrySignal.failure().getMessage()));
    }

    private static boolean isRetryable(Throwable throwable) {
        if (throwable instanceof WebClientResponseException response) {
            return response.getStatusCode().is5xxServerError();
        }
        return throwable instanceof WebClientRequestException;
    }
}
