package com.chronocritters.lobby.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.chronocritters.lobby.client.GameLogicWebClient;
import com.chronocritters.lobby.dto.Match;

import lombok.RequiredArgsConstructor;

/**
 * Turn timers and the player-to-battle map live in this service's memory, so a
 * restart used to leave every battle in progress without a clock: neither player
 * could be timed out and the match stalled for good. Both are rebuilt from
 * gamelogic, which is the authority on what is still being played.
 */
@Component
@RequiredArgsConstructor
public class BattleTimerRestorer {
    private static final Logger logger = LoggerFactory.getLogger(BattleTimerRestorer.class);

    private final GameLogicWebClient gameLogicWebClient;
    private final BattleSessionService battleSessionService;
    private final BattleTimerService battleTimerService;

    @EventListener(ApplicationReadyEvent.class)
    public void restoreBattlesInProgress() {
        gameLogicWebClient.activeBattles().subscribe(battleState -> {
            battleSessionService.register(new Match(
                battleState.getPlayerOne().getId(),
                battleState.getPlayerTwo().getId(),
                battleState.getBattleId()));

            battleTimerService.startOrResetTimer(battleState);

            logger.info("Re-armed the turn timer for battle {} after startup.", battleState.getBattleId());
        });
    }
}
