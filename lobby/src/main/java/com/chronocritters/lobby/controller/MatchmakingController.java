package com.chronocritters.lobby.controller;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lobby.client.GameLogicWebClient;
import com.chronocritters.lobby.dto.Match;
import com.chronocritters.lobby.service.BattleSessionService;
import com.chronocritters.lobby.service.BattleTimerService;
import com.chronocritters.lobby.service.MatchmakingService;
import com.chronocritters.lobby.session.StompSession;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MatchmakingController {
    private static final Logger logger = LoggerFactory.getLogger(MatchmakingController.class);

    private final MatchmakingService matchmakingService;
    private final BattleSessionService battleSessionService;
    private final BattleTimerService battleTimerService;
    private final GameLogicWebClient gameLogicWebClient;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/matchmaking/join")
    public void joinMatchmaking(SimpMessageHeaderAccessor headerAccessor) {
        String userId = StompSession.requireUserId(headerAccessor);

        Optional<Match> match = matchmakingService.join(userId);
        if (match.isEmpty()) {
            return;
        }

        Match foundMatch = match.get();

        BattleState battleState;
        try {
            battleState = gameLogicWebClient.createBattle(
                    foundMatch.battleId(), foundMatch.playerOneId(), foundMatch.playerTwoId()).block();
        } catch (RuntimeException e) {
            battleState = null;
            logger.warn("Battle creation failed for battleId '{}': {}", foundMatch.battleId(), e.getMessage());
        }

        if (battleState == null) {
            // Both players were already taken off the queue, so tell them both
            // rather than leaving their clients spinning on "Searching".
            notifyMatchFailed(foundMatch);
            return;
        }

        battleSessionService.register(foundMatch);
        battleTimerService.startOrResetTimer(battleState);

        messagingTemplate.convertAndSendToUser(foundMatch.playerOneId(), "/matchmaking/status", foundMatch);
        messagingTemplate.convertAndSendToUser(foundMatch.playerTwoId(), "/matchmaking/status", foundMatch);
    }

    @MessageMapping("/matchmaking/leave")
    public void leaveMatchmaking(SimpMessageHeaderAccessor headerAccessor) {
        matchmakingService.leave(StompSession.requireUserId(headerAccessor));
    }

    private void notifyMatchFailed(Match match) {
        Map<String, Object> error = Map.of(
            "error", true,
            "message", "Could not start the battle. Please try again.",
            "type", "MATCH_FAILED"
        );
        messagingTemplate.convertAndSendToUser(match.playerOneId(), "/error", error);
        messagingTemplate.convertAndSendToUser(match.playerTwoId(), "/error", error);
    }
}
