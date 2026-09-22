package com.chronocritters.gamelogic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;

import com.chronocritters.gamelogic.client.LobbyWebClient;
import com.chronocritters.gamelogic.grpc.PlayerGrpcClient;
import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.domain.BaseStats;
import com.chronocritters.lib.model.domain.Critter;
import com.chronocritters.lib.model.effects.DamageEffect;
import com.chronocritters.lib.model.enums.BattleOutcome;
import com.chronocritters.lib.model.enums.CritterType;
import com.chronocritters.lib.mapper.CritterMapper;
import com.chronocritters.lib.model.domain.Ability;
import com.chronocritters.proto.player.PlayerProto.BattleRewardsResponse;
import com.chronocritters.proto.player.PlayerProto.PlayerResponse;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BattleServiceTest {

    private static final String BATTLE_ID = "battle-1";
    private static final String PLAYER_ONE_ID = "p1";
    private static final String PLAYER_TWO_ID = "p2";
    private static final String ABILITY_ID = "atk-test";

    @Mock private PlayerGrpcClient playerGrpcClient;
    @Mock private LobbyWebClient lobbyWebClient;
    @Mock private ApplicationEventPublisher eventPublisher;

    private BattleService battleService;

    @BeforeEach
    void setUp() {
        battleService = new BattleService(playerGrpcClient, lobbyWebClient, eventPublisher);

        when(lobbyWebClient.updateBattleState(anyString(), any())).thenReturn(Mono.empty());
        when(playerGrpcClient.getBattleRewards(anyString(), anyString(), anyList(), anyList()))
                .thenReturn(BattleRewardsResponse.getDefaultInstance());
    }

    private Critter critter(String id, CritterType type, int health, int attack, int defense) {
        Ability ability = Ability.builder()
                .id(ABILITY_ID)
                .name("Test Strike")
                .description("hits hard")
                .effects(List.of(DamageEffect.builder().id("eff-damage").description("hurts").damage(4).build()))
                .build();

        return Critter.builder()
                .id(id)
                .name(id)
                .description(id)
                .type(type)
                .baseStats(BaseStats.builder().health(health).attack(attack).defense(defense).build())
                .abilities(List.of(ability))
                .build();
    }

    private void stubPlayer(String playerId, Critter... roster) {
        PlayerResponse.Builder builder = PlayerResponse.newBuilder().setId(playerId).setUsername(playerId + "-trainer");
        for (Critter c : roster) {
            builder.addRoster(CritterMapper.toProto(c));
        }
        when(playerGrpcClient.getPlayer(playerId)).thenReturn(builder.build());
    }

    /** Player one can knock out player two's single critter in one hit. */
    private BattleState oneHitBattle() {
        stubPlayer(PLAYER_ONE_ID, critter("striker", CritterType.FIRE, 20, 6, 3));
        stubPlayer(PLAYER_TWO_ID, critter("fodder", CritterType.FIRE, 3, 1, 3));
        return battleService.createBattle(BATTLE_ID, PLAYER_ONE_ID, PLAYER_TWO_ID);
    }

    private BattleState twoRoundBattle() {
        stubPlayer(PLAYER_ONE_ID,
                critter("one-lead", CritterType.FIRE, 40, 3, 3),
                critter("one-bench", CritterType.WATER, 40, 3, 3));
        stubPlayer(PLAYER_TWO_ID, critter("two-lead", CritterType.FIRE, 40, 3, 3));
        return battleService.createBattle(BATTLE_ID, PLAYER_ONE_ID, PLAYER_TWO_ID);
    }

    @Test
    @DisplayName("starts the battle with player one to move and both rosters at full health")
    void createsBattleWithPlayerOneToMove() {
        BattleState battleState = twoRoundBattle();

        assertThat(battleState).as("returned so the lobby needs no second call").isSameAs(battleService.getBattleState(BATTLE_ID));

        assertThat(battleState.getActivePlayerId()).isEqualTo(PLAYER_ONE_ID);
        assertThat(battleState.getPlayerOne().getHasTurn()).isTrue();
        assertThat(battleState.getPlayerTwo().getHasTurn()).isFalse();
        assertThat(battleState.getBattleOutcome()).isEqualTo(BattleOutcome.CONTINUE);
        assertThat(battleState.getActionLogHistory()).hasSize(1);
    }

    @Test
    @DisplayName("rejects an action from the player whose turn it is not")
    void rejectsActionOutOfTurn() {
        twoRoundBattle();

        assertThatThrownBy(() -> battleService.executeAbility(BATTLE_ID, PLAYER_TWO_ID, ABILITY_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not the player's turn");
    }

    @Test
    @DisplayName("rejects an action against an unknown battle")
    void rejectsUnknownBattle() {
        assertThatThrownBy(() -> battleService.executeAbility("nope", PLAYER_ONE_ID, ABILITY_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid battle ID");
    }

    @Test
    @DisplayName("only lets the two participants read the battle state")
    void restrictsBattleStateToParticipants() {
        twoRoundBattle();

        assertThat(battleService.getBattleStateFor(BATTLE_ID, PLAYER_ONE_ID)).isNotNull();
        assertThatThrownBy(() -> battleService.getBattleStateFor(BATTLE_ID, "someone-else"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not part of this battle");
    }

    @Test
    @DisplayName("passes the turn and broadcasts the new state after an ability")
    void passesTurnAfterAbility() {
        twoRoundBattle();

        BattleState battleState = battleService.executeAbility(BATTLE_ID, PLAYER_ONE_ID, ABILITY_ID);

        assertThat(battleState.getActivePlayerId()).isEqualTo(PLAYER_TWO_ID);
        assertThat(battleState.getPlayerTwo().getActiveCritter().getStats().getCurrentHp()).isLessThan(40);
        verify(lobbyWebClient).updateBattleState(eq(BATTLE_ID), any());
    }

    @Test
    @DisplayName("refuses to switch to the active critter, an unknown index, or a fainted critter")
    void validatesSwitchTargets() {
        BattleState battleState = twoRoundBattle();

        assertThatThrownBy(() -> battleService.switchCritter(BATTLE_ID, PLAYER_ONE_ID, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("currently active");

        assertThatThrownBy(() -> battleService.switchCritter(BATTLE_ID, PLAYER_ONE_ID, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid critter index");

        battleState.getPlayerOne().getCritterByIndex(1).getStats().setCurrentHp(0);
        assertThatThrownBy(() -> battleService.switchCritter(BATTLE_ID, PLAYER_ONE_ID, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fainted critter");
    }

    @Test
    @DisplayName("switching sends out the chosen critter and ends the turn")
    void switchingEndsTheTurn() {
        twoRoundBattle();

        BattleState battleState = battleService.switchCritter(BATTLE_ID, PLAYER_ONE_ID, 1);

        assertThat(battleState.getPlayerOne().getActiveCritterIndex()).isEqualTo(1);
        assertThat(battleState.getActivePlayerId()).isEqualTo(PLAYER_TWO_ID);
    }

    @Test
    @DisplayName("ends the battle, records the result and awards rewards when a roster is wiped out")
    void endsBattleWhenRosterIsWipedOut() {
        oneHitBattle();

        BattleState battleState = battleService.executeAbility(BATTLE_ID, PLAYER_ONE_ID, ABILITY_ID);

        assertThat(battleState.getBattleOutcome()).isEqualTo(BattleOutcome.BATTLE_END);
        assertThat(battleState.getWinnerId()).isEqualTo(PLAYER_ONE_ID);
        assertThat(battleState.getActivePlayerId()).as("signals the lobby to stop the turn timer").isNull();
        assertThat(battleState.getBattleRewards()).isNotNull();

        verify(playerGrpcClient).updateMatchHistory(eq(BATTLE_ID), eq(PLAYER_ONE_ID), eq(PLAYER_TWO_ID), any(), anyList(), anyList());
        verify(playerGrpcClient).getBattleRewards(eq(PLAYER_ONE_ID), eq(PLAYER_TWO_ID), anyList(), anyList());
    }

    @Test
    @DisplayName("forfeiting awards the win to the opponent and ends the battle")
    void forfeitAwardsTheWinToTheOpponent() {
        BattleState battleState = twoRoundBattle();

        battleService.forfeit(BATTLE_ID, PLAYER_ONE_ID);

        assertThat(battleState.getBattleOutcome()).isEqualTo(BattleOutcome.BATTLE_END);
        assertThat(battleState.getWinnerId()).isEqualTo(PLAYER_TWO_ID);
        assertThat(battleState.getPlayerOne().getRoster()).allMatch(critter -> critter.getStats().getCurrentHp() == 0);
        assertThat(battleState.getActionLogHistory()).anyMatch(log -> log.contains("forfeited"));
        verify(playerGrpcClient).updateMatchHistory(eq(BATTLE_ID), eq(PLAYER_TWO_ID), eq(PLAYER_ONE_ID), any(), anyList(), anyList());
    }

    @Test
    @DisplayName("forfeiting a battle you are not in is rejected")
    void forfeitRejectsNonParticipants() {
        twoRoundBattle();

        assertThatThrownBy(() -> battleService.forfeit(BATTLE_ID, "someone-else"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not part of this battle");
    }

    @Test
    @DisplayName("a second forfeit on a finished battle changes nothing")
    void forfeitIsIgnoredOnceTheBattleHasEnded() {
        BattleState battleState = twoRoundBattle();
        battleService.forfeit(BATTLE_ID, PLAYER_ONE_ID);

        battleService.forfeit(BATTLE_ID, PLAYER_TWO_ID);

        assertThat(battleState.getWinnerId()).isEqualTo(PLAYER_TWO_ID);
        verify(playerGrpcClient).updateMatchHistory(anyString(), anyString(), anyString(), any(), anyList(), anyList());
    }

    @Test
    @DisplayName("a turn timeout passes the turn to the opponent")
    void timeoutPassesTheTurn() {
        twoRoundBattle();

        battleService.handleTurnTimeout(BATTLE_ID);

        BattleState battleState = battleService.getBattleState(BATTLE_ID);
        assertThat(battleState.getActivePlayerId()).isEqualTo(PLAYER_TWO_ID);
        assertThat(battleState.getActionLogHistory()).anyMatch(log -> log.contains("ran out of time"));
    }

    @Test
    @DisplayName("a late timeout on a finished battle is ignored")
    void timeoutIsIgnoredOnceTheBattleHasEnded() {
        BattleState battleState = twoRoundBattle();
        battleService.forfeit(BATTLE_ID, PLAYER_ONE_ID);
        int logSize = battleState.getActionLogHistory().size();

        battleService.handleTurnTimeout(BATTLE_ID);

        assertThat(battleState.getActionLogHistory()).hasSize(logSize);
        verify(playerGrpcClient, never()).updateMatchHistory(anyString(), eq(PLAYER_ONE_ID), anyString(), any(), anyList(), anyList());
    }
}
