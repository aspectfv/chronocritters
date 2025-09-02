package com.chronocritters.lobby.client;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.chronocritters.lib.dto.BattleRequest;
import com.chronocritters.lib.dto.ExecuteAbilityRequest;
import com.chronocritters.lib.model.BattleState;

import reactor.core.publisher.Mono;

@Service
public class GameLogicWebClient {
    
    private final WebClient webClient;
    
    public GameLogicWebClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8082")
                .defaultHeader("X-Service-Auth", "lobby-service")
                .build();
    }

    public Mono<BattleState> getBattle(String battleId) {
        return webClient.get()
                .uri("/battle/{battleId}", battleId)
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.NOT_FOUND), 
                    response -> Mono.error(new IllegalArgumentException("Battle not found: " + battleId)))
                .onStatus(status -> status.is4xxClientError(),
                    response -> Mono.error(new IllegalArgumentException("Invalid request for battle: " + battleId)))
                .onStatus(status -> status.is5xxServerError(),
                    response -> Mono.error(new RuntimeException("Game logic service is unavailable")))
                .bodyToMono(BattleState.class);
    }    

    public Mono<Void> createBattle(String battleId, String playerOneId, String playerTwoId) {
        BattleRequest battleRequest = new BattleRequest(playerOneId, playerTwoId);
        
        return webClient.post()
                .uri("/battle/{battleId}", battleId)
                .bodyValue(battleRequest)
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.CONFLICT),
                    response -> Mono.error(new IllegalStateException("Battle already exists: " + battleId)))
                .onStatus(status -> status.is4xxClientError(),
                    response -> Mono.error(new IllegalArgumentException("Invalid battle creation request")))
                .onStatus(status -> status.is5xxServerError(),
                    response -> Mono.error(new RuntimeException("Failed to create battle - service unavailable")))
                .bodyToMono(Void.class);
    }

    public Mono<BattleState> executeAbility(String battleId, String playerId, String abilityId) {
        ExecuteAbilityRequest request = new ExecuteAbilityRequest(playerId, abilityId);
        
        return webClient.post()
                .uri("/battle/{battleId}/ability", battleId)
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.NOT_FOUND),
                    response -> Mono.error(new IllegalArgumentException("Battle or ability not found")))
                .onStatus(status -> status.equals(HttpStatus.FORBIDDEN),
                    response -> Mono.error(new IllegalStateException("Player cannot execute this ability")))
                .onStatus(status -> status.is4xxClientError(),
                    response -> Mono.error(new IllegalArgumentException("Invalid ability execution request")))
                .onStatus(status -> status.is5xxServerError(),
                    response -> Mono.error(new RuntimeException("Failed to execute ability - service unavailable")))
                .bodyToMono(BattleState.class);
    }
}