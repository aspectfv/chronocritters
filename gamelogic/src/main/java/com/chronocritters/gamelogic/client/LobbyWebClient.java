package com.chronocritters.gamelogic.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.util.ServiceAuth;
import com.chronocritters.lib.util.ServiceRetry;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class LobbyWebClient {
    private final WebClient webClient;
    private final Retry defaultRetrySpec;
    private static final Logger logger = LoggerFactory.getLogger(LobbyWebClient.class);

    public LobbyWebClient(@Value("${services.lobby.url}") String lobbyUrl,
                          @Value("${services.internal.token}") String internalToken) {
        this.webClient = WebClient.builder()
                .baseUrl(lobbyUrl)
                .defaultHeader(ServiceAuth.HEADER, internalToken)
                .build();

        this.defaultRetrySpec = ServiceRetry.backoff("the lobby service");
    }

    public Mono<Void> updateBattleState(String battleId, BattleState battleState) {
        return webClient.post()
                .uri("/battle/{battleId}/update", battleId)
                .bodyValue(battleState)
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(defaultRetrySpec)
                .onErrorResume(error -> {
                    logger.warn("Failed to update battle state for battleId '{}' in Lobby. The lobby might be down. Reason: {}",
                            battleId, error.getMessage());
                    return Mono.empty();
                });
    }
}