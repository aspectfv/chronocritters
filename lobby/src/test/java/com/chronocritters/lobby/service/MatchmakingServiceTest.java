package com.chronocritters.lobby.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.chronocritters.lib.dto.BattleRequest;
import com.chronocritters.lobby.dto.Match;
import com.chronocritters.lobby.dto.Pairing;

class MatchmakingServiceTest {

    private static final List<String> TEAM = List.of("fire-searfiend", "water-aqualing", "grass-sylvansentinel");

    private MatchmakingService matchmakingService;

    @BeforeEach
    void setUp() {
        matchmakingService = new MatchmakingService();
    }

    @Test
    @DisplayName("the first player waits until an opponent arrives")
    void firstPlayerWaits() {
        assertThat(matchmakingService.join("p1", TEAM)).isEmpty();
        assertThat(matchmakingService.isQueued("p1")).isTrue();
    }

    @Test
    @DisplayName("pairs two waiting players into a battle and clears the queue")
    void pairsTwoPlayers() {
        matchmakingService.join("p1", TEAM);

        Optional<Match> match = matchmakingService.join("p2", TEAM).map(Pairing::match);

        assertThat(match).isPresent();
        assertThat(match.get().playerOneId()).isEqualTo("p1");
        assertThat(match.get().playerTwoId()).isEqualTo("p2");
        assertThat(match.get().battleId()).isNotBlank();
        assertThat(matchmakingService.isQueued("p1")).isFalse();
        assertThat(matchmakingService.isQueued("p2")).isFalse();
    }

    @Test
    @DisplayName("never matches a player against themselves when they join twice")
    void doesNotMatchAPlayerWithThemselves() {
        matchmakingService.join("p1", TEAM);

        assertThat(matchmakingService.join("p1", TEAM)).isEmpty();
        assertThat(matchmakingService.isQueued("p1")).isTrue();
    }

    @Test
    @DisplayName("cancelling a search removes the player from the queue")
    void leaveRemovesThePlayer() {
        matchmakingService.join("p1", TEAM);

        matchmakingService.leave("p1");

        assertThat(matchmakingService.isQueued("p1")).isFalse();
        assertThat(matchmakingService.join("p2", TEAM)).as("no ghost left to pair with").isEmpty();
    }

    @Test
    @DisplayName("leaving when not queued is harmless")
    void leaveIsIdempotent() {
        matchmakingService.leave("never-queued");

        assertThat(matchmakingService.isQueued("never-queued")).isFalse();
    }

    @Test
    @DisplayName("rejects a blank player id")
    void rejectsBlankPlayerId() {
        assertThatThrownBy(() -> matchmakingService.join(" ", TEAM))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> matchmakingService.join(null, TEAM))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("hands each player's team, in the order they picked it, to the battle it pairs them into")
    void carriesEachTeamIntoTheBattle() {
        List<String> otherTeam = List.of("metal-cogling", "toxic-miasmite", "kinetic-strikon");
        matchmakingService.join("p1", TEAM);

        BattleRequest request = matchmakingService.join("p2", otherTeam).orElseThrow().battleRequest();

        assertThat(request.playerOneId()).isEqualTo("p1");
        assertThat(request.playerOneTeam()).containsExactlyElementsOf(TEAM);
        assertThat(request.playerTwoId()).isEqualTo("p2");
        assertThat(request.playerTwoTeam()).containsExactlyElementsOf(otherTeam);
    }

    @Test
    @DisplayName("a player who queues again goes in with the team they queued with last")
    void queueingAgainReplacesTheTeam() {
        List<String> changedMind = List.of("water-aqualing", "fire-searfiend", "grass-sylvansentinel");
        matchmakingService.join("p1", TEAM);
        matchmakingService.join("p1", changedMind);

        BattleRequest request = matchmakingService.join("p2", TEAM).orElseThrow().battleRequest();

        assertThat(request.playerOneTeam()).containsExactlyElementsOf(changedMind);
    }

    @Test
    @DisplayName("rejects a team that is empty, too big or picks a critter twice")
    void rejectsAMalformedTeam() {
        assertThatThrownBy(() -> matchmakingService.join("p1", List.of()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> matchmakingService.join("p1", null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> matchmakingService.join("p1", List.of("a", "b", "c", "d")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> matchmakingService.join("p1", List.of("a", "a", "b")))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(matchmakingService.isQueued("p1")).as("a rejected team does not queue the player").isFalse();
    }

    @Test
    @DisplayName("concurrent joins pair every player exactly once")
    void concurrentJoinsPairEveryPlayerOnce() throws Exception {
        int playerCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(16);
        CountDownLatch startGate = new CountDownLatch(1);
        ConcurrentLinkedQueue<Match> matches = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < playerCount; i++) {
            String playerId = "player-" + i;
            executor.submit(() -> {
                startGate.await();
                matchmakingService.join(playerId, TEAM).map(Pairing::match).ifPresent(matches::add);
                return null;
            });
        }

        startGate.countDown();
        executor.shutdown();
        assertThat(executor.awaitTermination(10, TimeUnit.SECONDS)).isTrue();

        assertThat(matches).hasSize(playerCount / 2);
        assertThat(matches.stream().flatMap(match -> java.util.stream.Stream.of(match.playerOneId(), match.playerTwoId())))
                .doesNotHaveDuplicates()
                .hasSize(playerCount);
    }
}
