package com.chronocritters.lobby.client;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.chronocritters.lib.dto.BattleRequest;
import com.chronocritters.lib.model.BattleState;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GameLogicWebClient {
    
    private final WebClient webClient;
    
    public GameLogicWebClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8082") // gamelogic service port
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
}