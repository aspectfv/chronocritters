package com.chronocritters.lobby.client;

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
                .defaultHeader("X-Service-Auth", "lobby-service") // Service-to-service auth
                .build();
    }

    public Mono<BattleState> getBattle(String battleId) {
        return webClient.get()
                .uri("/battle/{battleId}", battleId)
                .retrieve()
                .bodyToMono(BattleState.class);
    }    

    public Mono<Void> createBattle(String battleId, String playerOneId, String playerTwoId) {
        BattleRequest battleRequest = new BattleRequest(playerOneId, playerTwoId);
        
        return webClient.post()
                .uri("/battle/{battleId}", battleId)
                .bodyValue(battleRequest)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<BattleState> executeAbility(String battleId, String playerId, String abilityId) {
        ExecuteAbilityRequest request = new ExecuteAbilityRequest(playerId, abilityId);
        
        return webClient.post()
                .uri("/battle/{battleId}/ability", battleId)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(BattleState.class);
    }
}