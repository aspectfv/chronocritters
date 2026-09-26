package com.chronocritters.gamelogic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

import com.chronocritters.gamelogic.client.LobbyWebClient;
import com.chronocritters.gamelogic.event.CritterFaintedEvent;
import com.chronocritters.gamelogic.exception.BattleNotFoundException;
import com.chronocritters.gamelogic.grpc.PlayerGrpcClient;
import com.chronocritters.lib.model.battle.BattleState;
import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.domain.BaseStats;
import com.chronocritters.lib.model.domain.Critter;
import com.chronocritters.lib.model.effects.DamageEffect;
import com.chronocritters.lib.model.effects.DamageOverTimeEffect;
import com.chronocritters.lib.model.enums.BattleOutcome;
import com.chronocritters.lib.model.enums.CritterType;
import com.chronocritters.lib.model.enums.Stat;
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
    private static final String COOLING_ABILITY_ID = "atk-cooling";

    @Mock private PlayerGrpcClient playerGrpcClient;
    @Mock private LobbyWebClient lobbyWebClient;
    @Mock private ApplicationEventPublisher eventPublisher;

    private BattleService battleService;

    @BeforeEach
    void setUp() {
        battleService = new BattleService(playerGrpcClient, lobbyWebClient, eventPublisher);

        // Fainting is handled by a listener in the running service, so the
        // publisher delegates to the real one rather than swallowing the event.
        FaintingService faintingService = new FaintingService();
        doAnswer(invocation -> {
            if (invocation.getArgument(0) instanceof CritterFaintedEvent faintedEvent) {
                faintingService.onCritterFainted(faintedEvent);
            }
            return null;
        }).when(eventPublisher).publishEvent(any(ApplicationEvent.class));

        when(lobbyWebClient.updateBattleState(anyString(), any())).thenReturn(Mono.empty());
        when(playerGrpcClient.getBattleRewards(anyString(), anyString(), anyList(), anyList()))
                .thenReturn(BattleRewardsResponse.getDefaultInstance());
    }

    private Critter critter(String id, CritterType type, int health, int attack, int defense, int speed) {
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
                .baseStats(BaseStats.builder().health(health).attack(attack).defense(defense).speed(speed).build())
                .abilities(List.of(ability))
                .build();
    }

    /** A critter with a free move and a second one that has to be waited out. */
    private Critter critterWithACoolingMove(String id, CritterType type) {
        Critter model = critter(id, type, 40, 3, 3, 5);
        Ability cooling = Ability.builder()
                .id(COOLING_ABILITY_ID)
                .name("Slow Burn")
                .description("worth the wait")
                .effects(List.of(DamageEffect.builder().id("eff-cooling").description("hurts a little").damage(1).build()))
                .cooldownTurns(3)
                .build();

        model.setAbilities(List.of(model.getAbilities().get(0), cooling));
        return model;
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
        stubPlayer(PLAYER_ONE_ID, critter("striker", CritterType.FIRE, 20, 6, 3, 5));
        stubPlayer(PLAYER_TWO_ID, critter("fodder", CritterType.FIRE, 3, 1, 3, 1));
        return battleService.createBattle(BATTLE_ID, PLAYER_ONE_ID, PLAYER_TWO_ID);
    }

    private BattleState twoRoundBattle() {
        stubPlayer(PLAYER_ONE_ID,
                critter("one-lead", CritterType.FIRE, 40, 3, 3, 5),
                critter("one-bench", CritterType.WATER, 40, 3, 3, 5));
        stubPlayer(PLAYER_TWO_ID, critter("two-lead", CritterType.FIRE, 40, 3, 3, 1));
        return battleService.createBattle(BATTLE_ID, PLAYER_ONE_ID, PLAYER_TWO_ID);
    }

    @Test
    @DisplayName("starts the battle with the faster lead to move and both rosters at full health")
    void createsBattleWithTheFasterLeadToMove() {
        BattleState battleState = twoRoundBattle();

        assertThat(battleState).as("returned so the lobby needs no second call").isSameAs(battleService.getBattleState(BATTLE_ID));

        assertThat(battleState.getActivePlayerId()).isEqualTo(PLAYER_ONE_ID);
        assertThat(battleState.getPlayerOne().getHasTurn()).isTrue();
        assertThat(battleState.getPlayerTwo().getHasTurn()).isFalse();
        assertThat(battleState.getBattleOutcome()).isEqualTo(BattleOutcome.CONTINUE);
        assertThat(battleState.getActionLogHistory()).as("the battle opens, then says who is faster").hasSize(2);
    }

    @Test
    @DisplayName("gives the opening turn to the faster lead, whichever seat holds it")
    void startsWithTheFasterLeadToMove() {
        stubPlayer(PLAYER_ONE_ID, critter("slow-lead", CritterType.FIRE, 40, 3, 3, 2));
        stubPlayer(PLAYER_TWO_ID, critter("quick-lead", CritterType.FIRE, 40, 3, 3, 9));

        BattleState battleState = battleService.createBattle(BATTLE_ID, PLAYER_ONE_ID, PLAYER_TWO_ID);

        assertThat(battleState.getActivePlayerId()).isEqualTo(PLAYER_TWO_ID);
        assertThat(battleState.getPlayerTwo().getHasTurn()).isTrue();
        assertThat(battleState.getPlayerOne().getHasTurn()).isFalse();
        assertThat(battleState.getActionLogHistory()).last().asString().contains("quick-lead is faster");
    }

    @Test
    @DisplayName("decides a speed tie by coin toss, so neither seat is the better one to hold")
    void breaksASpeedTieWithoutFavouringASeat() {
        stubPlayer(PLAYER_ONE_ID, critter("mirror-one", CritterType.FIRE, 40, 3, 3, 5));
        stubPlayer(PLAYER_TWO_ID, critter("mirror-two", CritterType.FIRE, 40, 3, 3, 5));

        Set<String> openers = new HashSet<>();
        for (int battle = 0; battle < 200; battle++) {
            BattleState tied = battleService.createBattle("tie-" + battle, PLAYER_ONE_ID, PLAYER_TWO_ID);
            openers.add(tied.getActivePlayerId());
            assertThat(tied.getActionLogHistory()).last().asString().contains("wins the toss");
        }

        assertThat(openers).containsExactlyInAnyOrder(PLAYER_ONE_ID, PLAYER_TWO_ID);
    }

    @Test
    @DisplayName("takes a critter's level into the battle, so a levelled critter hits harder")
    void scalesBattleStatsByLevel() {
        Critter levelled = critter("levelled", CritterType.FIRE, 40, 10, 10, 5);
        levelled.getBaseStats().setLevel(11);
        stubPlayer(PLAYER_ONE_ID, levelled);
        stubPlayer(PLAYER_TWO_ID, critter("level-one", CritterType.FIRE, 40, 10, 10, 1));

        BattleState battleState = battleService.createBattle(BATTLE_ID, PLAYER_ONE_ID, PLAYER_TWO_ID);

        CritterState scaled = battleState.getPlayerOne().getActiveCritter();
        CritterState unscaled = battleState.getPlayerTwo().getActiveCritter();

        assertThat(scaled.getStats().getCurrentAtk()).isGreaterThan(unscaled.getStats().getCurrentAtk());
        assertThat(scaled.getStats().getMaxHp()).isGreaterThan(unscaled.getStats().getMaxHp());
        assertThat(scaled.getStats().getCurrentHp()).isEqualTo(scaled.getStats().getMaxHp());
    }

    private BattleState coolingMoveBattle() {
        stubPlayer(PLAYER_ONE_ID,
                critterWithACoolingMove("one-lead", CritterType.FIRE),
                critter("one-bench", CritterType.WATER, 40, 3, 3, 1));
        stubPlayer(PLAYER_TWO_ID, critterWithACoolingMove("two-lead", CritterType.FIRE));
        return battleService.createBattle(BATTLE_ID, PLAYER_ONE_ID, PLAYER_TWO_ID);
    }

    /** Hands the turn back to player one, who opens on the speed tie or loses it. */
    private void playARound(BattleState battleState) {
        battleService.executeAbility(BATTLE_ID, battleState.getActivePlayerId(), ABILITY_ID);
        battleService.executeAbility(BATTLE_ID, battleState.getActivePlayerId(), ABILITY_ID);
    }

    @Test
    @DisplayName("refuses a move that is still cooling down, and says how long is left")
    void refusesACoolingMove() {
        BattleState battleState = coolingMoveBattle();
        String opener = battleState.getActivePlayerId();
        String other = battleState.getPlayerOne().getId().equals(opener) ? PLAYER_TWO_ID : PLAYER_ONE_ID;

        battleService.executeAbility(BATTLE_ID, opener, COOLING_ABILITY_ID);
        battleService.executeAbility(BATTLE_ID, other, ABILITY_ID);

        assertThatThrownBy(() -> battleService.executeAbility(BATTLE_ID, opener, COOLING_ABILITY_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Slow Burn is still cooling down");
    }

    @Test
    @DisplayName("frees the move again once its owner has taken the turns it costs")
    void freesTheMoveOnceTheOwnerHasWaited() {
        BattleState battleState = coolingMoveBattle();
        String opener = battleState.getActivePlayerId();
        String other = battleState.getPlayerOne().getId().equals(opener) ? PLAYER_TWO_ID : PLAYER_ONE_ID;

        battleService.executeAbility(BATTLE_ID, opener, COOLING_ABILITY_ID);
        battleService.executeAbility(BATTLE_ID, other, ABILITY_ID);

        // A stored 3 costs its owner two of their own turns, so they spend both
        // on the free move before the cooling one comes back.
        playARound(battleState);
        playARound(battleState);

        assertThat(battleState.getPlayerById(opener).getActiveCritter().isCoolingDown(COOLING_ABILITY_ID)).isFalse();
        battleService.executeAbility(BATTLE_ID, opener, COOLING_ABILITY_ID);

        assertThat(battleState.getPlayerById(opener).getActiveCritter().getAbilityCooldowns())
                .containsEntry(COOLING_ABILITY_ID, 2);
    }

    @Test
    @DisplayName("counts the cooldown down on its owner's turns, not on every turn in the battle")
    void countsTheCooldownOnTheOwnersTurns() {
        BattleState battleState = coolingMoveBattle();
        String opener = battleState.getActivePlayerId();
        String other = battleState.getPlayerOne().getId().equals(opener) ? PLAYER_TWO_ID : PLAYER_ONE_ID;

        battleService.executeAbility(BATTLE_ID, opener, COOLING_ABILITY_ID);
        assertThat(battleState.getPlayerById(opener).getActiveCritter().getAbilityCooldowns())
                .as("one turn is spent by using it").containsEntry(COOLING_ABILITY_ID, 2);

        battleService.executeAbility(BATTLE_ID, other, ABILITY_ID);
        assertThat(battleState.getPlayerById(opener).getActiveCritter().getAbilityCooldowns())
                .as("the opponent's turn is not the owner's").containsEntry(COOLING_ABILITY_ID, 2);
    }

    @Test
    @DisplayName("sends a critter back out with its cooldowns cleared, since the switch cost a turn")
    void clearsCooldownsWhenACritterLeavesTheField() {
        stubPlayer(PLAYER_ONE_ID,
                critterWithACoolingMove("one-lead", CritterType.FIRE),
                critter("one-bench", CritterType.WATER, 40, 3, 3, 1));
        stubPlayer(PLAYER_TWO_ID, critter("two-lead", CritterType.FIRE, 40, 3, 3, 1));
        BattleState battleState = battleService.createBattle(BATTLE_ID, PLAYER_ONE_ID, PLAYER_TWO_ID);

        CritterState lead = battleState.getPlayerOne().getActiveCritter();
        battleService.executeAbility(BATTLE_ID, PLAYER_ONE_ID, COOLING_ABILITY_ID);
        assertThat(lead.getAbilityCooldowns()).isNotEmpty();

        battleService.executeAbility(BATTLE_ID, PLAYER_TWO_ID, ABILITY_ID);
        battleService.switchCritter(BATTLE_ID, PLAYER_ONE_ID, 1);

        assertThat(lead.getAbilityCooldowns()).isEmpty();
    }

    @Test
    @DisplayName("sends a critter back out with its stat stages reset, so a debuff is escaped by switching")
    void resetsStagesWhenACritterLeavesTheField() {
        stubPlayer(PLAYER_ONE_ID,
                critter("one-lead", CritterType.FIRE, 40, 3, 3, 5),
                critter("one-bench", CritterType.WATER, 40, 3, 3, 1));
        stubPlayer(PLAYER_TWO_ID, critter("two-lead", CritterType.FIRE, 40, 3, 3, 1));
        BattleState battleState = battleService.createBattle(BATTLE_ID, PLAYER_ONE_ID, PLAYER_TWO_ID);

        CritterState lead = battleState.getPlayerOne().getActiveCritter();
        lead.getStats().shiftStage(Stat.DEFENSE, -2);
        assertThat(lead.getStats().getCurrentDef()).isEqualTo(2);

        battleService.switchCritter(BATTLE_ID, PLAYER_ONE_ID, 1);

        assertThat(lead.getStats().getStages()).isEmpty();
        assertThat(lead.getStats().getCurrentDef()).isEqualTo(3);
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
    @DisplayName("reports an unknown battle as gone rather than as a bad request")
    void rejectsUnknownBattle() {
        assertThatThrownBy(() -> battleService.executeAbility("nope", PLAYER_ONE_ID, ABILITY_ID))
                .isInstanceOf(BattleNotFoundException.class)
                .hasMessageContaining("no longer active");
    }

    @Test
    @DisplayName("a player who misses three turns in a row concedes")
    void forfeitsAfterRepeatedTimeouts() {
        twoRoundBattle();

        // Player one idles through every turn that comes back round to them.
        battleService.handleTurnTimeout(BATTLE_ID);
        battleService.handleTurnTimeout(BATTLE_ID);
        battleService.handleTurnTimeout(BATTLE_ID);
        battleService.handleTurnTimeout(BATTLE_ID);
        battleService.handleTurnTimeout(BATTLE_ID);

        BattleState battleState = battleService.getBattleState(BATTLE_ID);
        assertThat(battleState.getBattleOutcome()).isEqualTo(BattleOutcome.BATTLE_END);
        assertThat(battleState.getWinnerId()).isEqualTo(PLAYER_TWO_ID);
    }

    @Test
    @DisplayName("acting clears the missed-turn count, so an occasional timeout costs nothing")
    void actingResetsTheTimeoutCount() {
        twoRoundBattle();

        battleService.handleTurnTimeout(BATTLE_ID);
        battleService.handleTurnTimeout(BATTLE_ID);
        battleService.executeAbility(BATTLE_ID, PLAYER_ONE_ID, ABILITY_ID);

        assertThat(battleService.getBattleState(BATTLE_ID).getPlayerOne().getConsecutiveTimeouts()).isZero();
    }

    /** Player one's lead is knocked out, leaving them owing a replacement. */
    private BattleState battleAwaitingReplacement() {
        BattleState battleState = twoRoundBattle();
        battleState.getPlayerOne().getActiveCritter().getStats().setCurrentHp(1);

        battleService.executeAbility(BATTLE_ID, PLAYER_ONE_ID, ABILITY_ID);
        battleService.executeAbility(BATTLE_ID, PLAYER_TWO_ID, ABILITY_ID);

        return battleState;
    }

    @Test
    @DisplayName("a knocked-out active critter waits for its owner to choose the replacement")
    void waitsForTheOwnerToChooseAReplacement() {
        BattleState battleState = battleAwaitingReplacement();

        assertThat(battleState.getAwaitingSwitchPlayerIds()).containsExactly(PLAYER_ONE_ID);
        assertThat(battleState.getPlayerOne().getActiveCritterIndex()).as("nobody is sent out for them").isZero();
    }

    @Test
    @DisplayName("replacing a fainted critter is free and leaves the turn with its owner")
    void replacingAFaintedCritterIsFree() {
        battleAwaitingReplacement();

        BattleState battleState = battleService.switchCritter(BATTLE_ID, PLAYER_ONE_ID, 1);

        assertThat(battleState.getAwaitingSwitchPlayerIds()).isEmpty();
        assertThat(battleState.getPlayerOne().getActiveCritterIndex()).isEqualTo(1);
        assertThat(battleState.getActivePlayerId()).isEqualTo(PLAYER_ONE_ID);
    }

    @Test
    @DisplayName("refuses an ability until the replacement is sent out")
    void refusesAnAbilityWhileAReplacementIsOwed() {
        battleAwaitingReplacement();

        assertThatThrownBy(() -> battleService.executeAbility(BATTLE_ID, PLAYER_ONE_ID, ABILITY_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("replacement critter first");
    }

    @Test
    @DisplayName("refuses an ability from the opponent while a replacement is owed")
    void refusesTheOpponentsAbilityWhileAReplacementIsOwed() {
        BattleState battleState = battleAwaitingReplacement();
        battleState.setActivePlayerId(PLAYER_TWO_ID);

        assertThatThrownBy(() -> battleService.executeAbility(BATTLE_ID, PLAYER_TWO_ID, ABILITY_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Waiting for a replacement critter");
    }

    @Test
    @DisplayName("the clock running out on a replacement sends out the first living critter")
    void timingOutOnAReplacementFallsBackToRosterOrder() {
        BattleState battleState = battleAwaitingReplacement();

        battleService.handleTurnTimeout(BATTLE_ID);

        assertThat(battleState.getAwaitingSwitchPlayerIds()).isEmpty();
        assertThat(battleState.getPlayerOne().getActiveCritterIndex()).isEqualTo(1);
    }

    /** Both actives fall on the same turn: the hit takes one, the burn the other. */
    private BattleState doubleFaintBattle() {
        stubPlayer(PLAYER_ONE_ID,
                critter("one-lead", CritterType.FIRE, 40, 3, 3, 5),
                critter("one-bench", CritterType.WATER, 40, 3, 3, 5));
        stubPlayer(PLAYER_TWO_ID,
                critter("two-lead", CritterType.FIRE, 40, 3, 3, 1),
                critter("two-bench", CritterType.WATER, 40, 3, 3, 5));
        BattleState battleState = battleService.createBattle(BATTLE_ID, PLAYER_ONE_ID, PLAYER_TWO_ID);

        battleState.getPlayerTwo().getActiveCritter().getStats().setCurrentHp(1);

        CritterState burningLead = battleState.getPlayerOne().getActiveCritter();
        burningLead.getStats().setCurrentHp(1);
        burningLead.getActiveStatusEffects().add(DamageOverTimeEffect.builder()
                .id("eff-burn").description("burns").casterId("two-lead").damagePerTurn(2).duration(2).build());

        battleService.executeAbility(BATTLE_ID, PLAYER_ONE_ID, ABILITY_ID);
        return battleState;
    }

    @Test
    @DisplayName("asks both players for a replacement when the two actives fall on the same turn")
    void asksBothPlayersForAReplacementOnADoubleFaint() {
        BattleState battleState = doubleFaintBattle();

        assertThat(battleState.getAwaitingSwitchPlayerIds()).containsExactlyInAnyOrder(PLAYER_ONE_ID, PLAYER_TWO_ID);
    }

    @Test
    @DisplayName("holds every move until both replacements are on the field, then passes the turn on")
    void holdsTheBoardUntilBothReplacementsAreSentOut() {
        BattleState battleState = doubleFaintBattle();

        assertThatThrownBy(() -> battleService.executeAbility(BATTLE_ID, PLAYER_TWO_ID, ABILITY_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("replacement critter first");

        battleService.switchCritter(BATTLE_ID, PLAYER_TWO_ID, 1);

        assertThatThrownBy(() -> battleService.executeAbility(BATTLE_ID, PLAYER_TWO_ID, ABILITY_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Waiting for a replacement critter");

        battleService.switchCritter(BATTLE_ID, PLAYER_ONE_ID, 1);

        assertThat(battleState.getAwaitingSwitchPlayerIds()).isEmpty();
        assertThat(battleState.getActivePlayerId()).as("the free replacement costs player one no turn and wins them none")
                .isEqualTo(PLAYER_TWO_ID);
        assertThat(battleState.getTimeRemaining()).isEqualTo(battleState.getTurnDuration());

        battleService.executeAbility(BATTLE_ID, PLAYER_TWO_ID, ABILITY_ID);

        assertThat(battleState.getPlayerOne().getActiveCritter().getStats().getCurrentHp()).isLessThan(40);
    }

    @Test
    @DisplayName("the clock running out on a double faint sends out both replacements")
    void timingOutOnADoubleFaintSendsOutBothReplacements() {
        BattleState battleState = doubleFaintBattle();

        battleService.handleTurnTimeout(BATTLE_ID);

        assertThat(battleState.getAwaitingSwitchPlayerIds()).isEmpty();
        assertThat(battleState.getPlayerOne().getActiveCritterIndex()).isEqualTo(1);
        assertThat(battleState.getPlayerTwo().getActiveCritterIndex()).isEqualTo(1);
        assertThat(battleState.getTimeRemaining()).isEqualTo(battleState.getTurnDuration());
    }

    @Test
    @DisplayName("lists only the battles still waiting on a move")
    void listsBattlesAwaitingATurn() {
        oneHitBattle();

        assertThat(battleService.getBattlesAwaitingATurn()).extracting(BattleState::getBattleId).containsExactly(BATTLE_ID);

        battleService.executeAbility(BATTLE_ID, PLAYER_ONE_ID, ABILITY_ID);

        assertThat(battleService.getBattlesAwaitingATurn()).as("a finished battle needs no clock").isEmpty();
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
