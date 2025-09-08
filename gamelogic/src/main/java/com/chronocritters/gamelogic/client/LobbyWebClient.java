// In: gamelogic/src/main/java/com/chronocritters/gamelogic/client/LobbyWebClient.java

package com.chronocritters.gamelogic.client;

import com.chronocritters.lib.model.BattleState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class LobbyWebClient {
    private final WebClient webClient;
    private final Retry defaultRetrySpec;
    private static final Logger log = LoggerFactory.getLogger(LobbyWebClient.class);

    public LobbyWebClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8081")
                .defaultHeader("X-Service-Auth", "gamelogic-service")
                .build();

        this.defaultRetrySpec = Retry.backoff(3, Duration.ofMillis(500))
                .filter(this::isRetryableException)
                .doBeforeRetry(retrySignal -> log.warn("Retrying request to Lobby service after failure: attempt #{}, error: {}",
                        retrySignal.totalRetries() + 1,
                        retrySignal.failure().getMessage()));
    }

    public Mono<Void> updateBattleState(String battleId, BattleState battleState) {
        return webClient.post()
                .uri("/battle/{battleId}/update", battleId)
                .bodyValue(battleState)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> Mono.error(new IllegalArgumentException("Invalid battle state update request")))
                .bodyToMono(Void.class)
                .retryWhen(defaultRetrySpec);
    }

    private boolean isRetryableException(Throwable throwable) {
        // retry on network errors (e.g., Connection Refused)
        if (throwable instanceof WebClientRequestException) {
            return true;
        }
        // retry on 5xx server errors
        if (throwable instanceof WebClientResponseException) {
            return ((WebClientResponseException) throwable).getStatusCode().is5xxServerError();
        }
        return false;
    }
}