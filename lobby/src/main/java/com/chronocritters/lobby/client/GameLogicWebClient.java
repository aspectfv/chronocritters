package com.chronocritters.lobby.client;

import com.chronocritters.lib.dto.BattleRequest;
import com.chronocritters.lib.dto.ForfeitRequest;
import com.chronocritters.lib.util.ServiceAuth;
import com.chronocritters.lib.util.ServiceRetry;
import com.chronocritters.lib.model.battle.BattleState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class GameLogicWebClient {

    private final WebClient webClient;
    private final Retry defaultRetrySpec;
    private static final Logger logger = LoggerFactory.getLogger(GameLogicWebClient.class);


    public GameLogicWebClient(@Value("${services.gamelogic.url}") String gameLogicUrl,
                              @Value("${services.internal.token}") String internalToken) {
        this.webClient = WebClient.builder()
                .baseUrl(gameLogicUrl)
                .defaultHeader(ServiceAuth.HEADER, internalToken)
                .build();

        this.defaultRetrySpec = ServiceRetry.backoff("the gamelogic service");
    }

    public Mono<BattleState> createBattle(String battleId, BattleRequest battleRequest) {
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
                .bodyToMono(BattleState.class)
                .retryWhen(defaultRetrySpec)
                .doOnError(error -> logger.warn("Could not create battle for battleId '{}'. Reason: {}", battleId, error.getMessage()));
    }

    /** The battles gamelogic is still waiting on, so a restarted lobby can re-arm their clocks. */
    public Flux<BattleState> activeBattles() {
        return webClient.get()
                .uri("/battle/active")
                .retrieve()
                .bodyToFlux(BattleState.class)
                .retryWhen(defaultRetrySpec)
                .onErrorResume(error -> {
                    logger.warn("Could not list the battles in progress. Their turn timers stay unset. Reason: {}", error.getMessage());
                    return Flux.empty();
                });
    }

    public Mono<Void> handleTurnTimeout(String battleId) {
        return webClient.post()
                .uri("/battle/{battleId}/timeout", battleId)
                .retrieve()
                // Mapped off the retryable exceptions on purpose: replaying a
                // timeout would advance the turn a second time.
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new IllegalStateException("Server error while handling turn timeout")))
                .bodyToMono(Void.class)
                .retryWhen(defaultRetrySpec)
                .onErrorResume(error -> {
                    logger.warn("Could not handle turn timeout for battleId '{}'. The battle may have ended. Reason: {}", battleId, error.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Void> forfeit(String battleId, String playerId) {
        return webClient.post()
                .uri("/battle/{battleId}/forfeit", battleId)
                .bodyValue(new ForfeitRequest(playerId))
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(defaultRetrySpec)
                .onErrorResume(error -> {
                    logger.warn("Could not forfeit battle '{}' for player '{}'. The battle may have ended. Reason: {}",
                            battleId, playerId, error.getMessage());
                    return Mono.empty();
                });
    }
}