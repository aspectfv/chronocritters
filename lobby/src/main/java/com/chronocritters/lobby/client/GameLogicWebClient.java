package com.chronocritters.lobby.client;

import com.chronocritters.lib.dto.BattleRequest;
import com.chronocritters.lib.model.battle.BattleState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class GameLogicWebClient {

    private final WebClient webClient;
    private final Retry defaultRetrySpec;
    private static final Logger logger = LoggerFactory.getLogger(GameLogicWebClient.class);


    public GameLogicWebClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8082")
                .defaultHeader("X-Service-Auth", "lobby-service")
                .build();

        this.defaultRetrySpec = Retry.backoff(3, Duration.ofMillis(500))
                .filter(this::isRetryableException)
                .doBeforeRetry(retrySignal -> logger.warn("Retrying request after failure: attempt #{}, error: {}",
                        retrySignal.totalRetries() + 1,
                        retrySignal.failure().getMessage()));
    }

    public Mono<BattleState> getBattleState(String battleId) {
        return webClient.get()
                .uri("/battle/{battleId}", battleId)
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.NOT_FOUND),
                        response -> Mono.error(new IllegalArgumentException("Battle not found: " + battleId)))
                .onStatus(status -> status.is4xxClientError() && status != HttpStatus.NOT_FOUND,
                        response -> Mono.error(new IllegalArgumentException("Invalid request for battle: " + battleId)))
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new IllegalStateException("Server error while fetching battle state for: " + battleId)))
                .bodyToMono(BattleState.class)
                .retryWhen(defaultRetrySpec)
                .onErrorResume(error -> {
                    logger.warn("Could not retrieve battle state for battleId '{}'. Reason: {}", battleId, error.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Void> createBattle(String battleId, String playerOneId, String playerTwoId) {
        BattleRequest battleRequest = new BattleRequest(playerOneId, playerTwoId);

        return webClient.post()
                .uri("/battle/{battleId}", battleId)
                .bodyValue(battleRequest)
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.CONFLICT),
                        response -> Mono.error(new IllegalStateException("Battle already exists: " + battleId)))
                .onStatus(status -> status.is4xxClientError() && status != HttpStatus.CONFLICT,
                        response -> Mono.error(new IllegalArgumentException("Invalid battle creation request")))
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new IllegalStateException("Server error while creating battle")))
                .bodyToMono(Void.class)
                .retryWhen(defaultRetrySpec)
                .onErrorResume(error -> {
                    logger.warn("Could not create battle for battleId '{}'. Reason: {}", battleId, error.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Void> handleTurnTimeout(String battleId) {
        return webClient.post()
                .uri("/battle/{battleId}/timeout", battleId)
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.NOT_FOUND),
                        response -> Mono.error(new IllegalArgumentException("Battle not found: " + battleId)))
                .onStatus(status -> status.is4xxClientError() && status != HttpStatus.NOT_FOUND,
                        response -> Mono.error(new IllegalArgumentException("Invalid turn timeout request")))
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new IllegalStateException("Server error while handling turn timeout")))
                .bodyToMono(Void.class)
                .retryWhen(defaultRetrySpec)
                .onErrorResume(error -> {
                    logger.warn("Could not handle turn timeout for battleId '{}'. The battle may have ended. Reason: {}", battleId, error.getMessage());
                    return Mono.empty();
                });
    }

    private boolean isRetryableException(Throwable throwable) {
        return throwable instanceof WebClientRequestException ||
               (throwable instanceof WebClientResponseException && 
                ((WebClientResponseException) throwable).getStatusCode().is5xxServerError());
    }
}