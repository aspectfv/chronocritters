package com.chronocritters.gamelogic.client;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.chronocritters.lib.model.BattleState;

import reactor.core.publisher.Mono;

@Service
public class LobbyWebClient {
    private final WebClient webClient;

    public LobbyWebClient(WebClient.Builder webClientBuilder) {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8081")
                .defaultHeader("X-Service-Auth", "gamelogic-service")
                .build();
    }

    public Mono<Void> updateBattleState(String battleId, BattleState battleState) {
        return webClient.post()
                .uri("/battle/{battleId}/update", battleId)
                .bodyValue(battleState)
                .retrieve()
                .bodyToMono(Void.class);
    }
}