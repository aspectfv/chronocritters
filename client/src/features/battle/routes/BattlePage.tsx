import { useEffect, useCallback, useState } from 'react';
import { useParams, useNavigate, useLoaderData } from 'react-router-dom';
import { useAuthStore } from '@store/auth/useAuthStore';
import { useLobbyStore } from '@store/lobby/useLobbyStore';
import { useBattleStore } from '@store/battle/useBattleStore';
import type { BattleData } from '@store/battle/types';

import { BattleHeader } from '@features/battle/components/BattleHeader';
import { ChronoDial } from '@features/battle/components/ChronoDial';
import { CritterCell } from '@features/battle/components/CritterCell';
import { TeamRail } from '@features/battle/components/TeamRail';
import { BattleTextBox } from '@features/battle/components/BattleTextBox';
import { MoveGrid } from '@features/battle/components/MoveGrid';
import { OpponentStatusBanner } from '@features/battle/components/OpponentStatusBanner';
import { ForcedSwitchPanel } from '@features/battle/components/ForcedSwitchPanel';
import { playBattleSound } from '@features/battle/sound';
import { executeAbility, getBattleState, switchCritter } from '@api/gamelogic';
import { ConnectionStatus } from '@store/lobby/types';
import type { BattleOutcomeSummary } from '@features/results/types';

function errorMessage(error: unknown, fallback: string): string {
  const response = (error as { response?: { data?: { message?: string } } })?.response;
  return response?.data?.message ?? fallback;
}

function BattlePage() {
  const { battleId } = useParams<{ battleId: string }>();
  const navigate = useNavigate();
  const user = useAuthStore((state) => state.user);
  const initialBattleState = useLoaderData() as BattleData;

  const isConnected = useLobbyStore((state) => state.connectionStatus === ConnectionStatus.CONNECTED);
  const publish = useLobbyStore((state) => state.publish);
  const {
    player,
    opponent,
    actionLogHistory,
    timeRemaining,
    turnDuration,
    lastTurnResult,
    awaitingSwitchPlayerId,
    disconnectedPlayerId,
    reconnectSecondsRemaining,
    battleId: storeBattleId,
    setBattleState,
  } = useBattleStore();

  const [actionError, setActionError] = useState<string | null>(null);
  const [isActionPending, setIsActionPending] = useState(false);

  useEffect(() => {
    if (initialBattleState && user?.id && storeBattleId !== battleId) {
      setBattleState(initialBattleState, user.id);
    }
  }, [initialBattleState, user?.id, battleId, storeBattleId, setBattleState]);

  useEffect(() => {
    const { subscribe } = useLobbyStore.getState();
    const { setBattleState } = useBattleStore.getState();

    if (!isConnected || !battleId || !user?.id) return;

    const subscription = subscribe(`/topic/battle/${battleId}`, (newBattleState: BattleData) => {
      setBattleState(newBattleState, user.id);
      setIsActionPending(false);
    });

    // A reconnect can land several turns after the drop, so the board is
    // refetched rather than resumed from whatever the store still holds.
    getBattleState(battleId)
      .then((response) => setBattleState(response.data, user.id))
      .catch(() => setActionError('Could not reload this battle. It may have already ended.'));

    return () => {
      subscription?.unsubscribe();
    };
  }, [isConnected, battleId, user?.id]);

  useEffect(() => {
    const { battleStats, battleRewards } = useBattleStore.getState();
    const outcomeSummary: BattleOutcomeSummary = { battleStats, battleRewards, opponent };

    if (opponent.roster.length > 0 && opponent.roster.every(critter => critter.stats.currentHp <= 0)) {
      navigate(`/results/${battleId}`, { state: { result: 'victory', battleState: outcomeSummary } });
    } else if (player.roster.length > 0 && player.roster.every(critter => critter.stats.currentHp <= 0)) {
      navigate(`/results/${battleId}`, { state: { result: 'defeat', battleState: outcomeSummary } });
    }
  }, [player, opponent, navigate, battleId]);

  const hitTurn = lastTurnResult?.turn;
  const hitWasSuperEffective = (lastTurnResult?.effectiveness ?? 1) > 1;

  useEffect(() => {
    if (hitTurn === undefined) return;
    playBattleSound(hitWasSuperEffective ? 'superEffective' : 'hit');
  }, [hitTurn, hitWasSuperEffective]);

  useEffect(() => {
    if (actionLogHistory.at(-1)?.includes('fainted!')) {
      playBattleSound('faint');
    }
  }, [actionLogHistory]);

  const handleAbilityClick = useCallback(async (abilityId: string) => {
    const { player } = useBattleStore.getState();

    if (!player.hasTurn || !battleId || isActionPending) {
      return;
    }

    setActionError(null);
    setIsActionPending(true);
    playBattleSound('select');

    try {
      await executeAbility(battleId, abilityId);
    } catch (error) {
      setActionError(errorMessage(error, 'That move could not be played. Please try again.'));
      setIsActionPending(false);
    }
  }, [battleId, isActionPending]);

  const handleSwitchCritter = useCallback(async (targetCritterIndex: number) => {
    const isReplacingAFaintedCritter = awaitingSwitchPlayerId === user?.id;

    if ((!player.hasTurn && !isReplacingAFaintedCritter) || !battleId || isActionPending) {
      return;
    }

    setActionError(null);
    setIsActionPending(true);
    playBattleSound('select');

    try {
      await switchCritter(battleId, targetCritterIndex);
    } catch (error) {
      setActionError(errorMessage(error, 'That critter could not be sent out. Please try again.'));
      setIsActionPending(false);
    }
  }, [battleId, player.hasTurn, isActionPending, awaitingSwitchPlayerId, user?.id]);

  const handleForfeit = useCallback(() => {
    if (!battleId || !window.confirm('Forfeit this battle? Your opponent will be awarded the win.')) {
      return;
    }
    publish(`/app/battle/${battleId}/forfeit`, {});
  }, [battleId, publish]);

  const mustReplaceFaintedCritter = awaitingSwitchPlayerId === user?.id;
  const canAct = player.hasTurn && !isActionPending && !mustReplaceFaintedCritter;
  const isOpponentReconnecting = Boolean(disconnectedPlayerId) && disconnectedPlayerId !== user?.id;

  const playerHit = lastTurnResult?.targetCritterId === player.activeCritter.id ? lastTurnResult : undefined;
  const opponentHit = lastTurnResult?.targetCritterId === opponent.activeCritter.id ? lastTurnResult : undefined;

  return (
    <main className="min-h-screen bg-arena text-arena-ink">
      <div className="relative mx-auto flex min-h-screen max-w-5xl flex-col gap-3 p-3 sm:p-5">
        <BattleHeader isPlayerTurn={player.hasTurn} onForfeit={handleForfeit} />

        {isOpponentReconnecting && (
          <OpponentStatusBanner opponentName={opponent.username} secondsRemaining={reconnectSecondsRemaining ?? 0} />
        )}

        {actionError && (
          <div role="alert" className="rounded-lg border border-danger/40 bg-danger/15 px-4 py-2 text-center text-sm text-arena-ink">
            {actionError}
          </div>
        )}

        {/* The two sides are laid out in flow rather than pinned to the plate's
            corners, so a tall stack makes the plate taller instead of running
            off the top of it. The clock and the ground stay absolute: they are
            keyed to the plate's centre, not to either side. */}
        <div className="arena-plate relative flex min-h-[22rem] flex-1 gap-3 overflow-hidden rounded-2xl p-3 sm:min-h-[25rem] sm:p-5">
          <div className="order-3 flex min-w-0 flex-1 flex-col items-end justify-start gap-2">
            <TeamRail
              title="Opponent's bench"
              team={opponent.roster}
              activeCritterId={opponent.activeCritter.id}
              align="right"
            />
            <CritterCell
              playerName={opponent.username}
              critter={opponent.activeCritter}
              side="opponent"
              hitTurn={opponentHit?.turn}
              hitDamage={opponentHit?.damage}
              hitEffectiveness={opponentHit?.effectiveness}
            />
          </div>

          {/* Room reserved down the middle so neither side runs into the clock. */}
          <div className="order-2 w-20 shrink-0 sm:w-32" aria-hidden="true" />

          {/* The horizon line, and the clock's own dial struck around it. Both
              sit on the plate's centre by construction, so nothing has to be
              lined up by eye. */}
          <div className="arena-floor pointer-events-none absolute inset-0" aria-hidden="true" />
          <div className="arena-horizon pointer-events-none absolute left-0 top-1/2 h-px w-full" aria-hidden="true" />
          <div className="pointer-events-none absolute left-1/2 top-1/2 h-[19rem] w-[19rem] -translate-x-1/2 -translate-y-1/2" aria-hidden="true">
            <div className="chrono-guilloche absolute inset-0" />
          </div>
          <div className="pointer-events-none absolute left-1/2 top-1/2 h-44 w-44 -translate-x-1/2 -translate-y-1/2" aria-hidden="true">
            <div className="chrono-chapter absolute inset-0" />
          </div>
          <div className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2">
            <ChronoDial timeRemaining={timeRemaining} turnDuration={turnDuration} />
          </div>

          <div className="order-1 flex min-w-0 flex-1 flex-col items-start justify-end gap-2">
            <CritterCell
              playerName={player.username}
              critter={player.activeCritter}
              side="player"
              showNumericHp
              hitTurn={playerHit?.turn}
              hitDamage={playerHit?.damage}
              hitEffectiveness={playerHit?.effectiveness}
            />
            <TeamRail
              title="Your bench"
              team={player.roster}
              activeCritterId={player.activeCritter.id}
              align="left"
              canSwitch={canAct}
              onCritterClick={handleSwitchCritter}
            />
          </div>
        </div>

        <div className="mt-auto flex flex-col gap-2">
          <BattleTextBox log={actionLogHistory} />
          <MoveGrid
            abilities={player.activeCritter.abilities}
            casterType={player.activeCritter.type}
            onAbilityClick={handleAbilityClick}
            isPlayerTurn={canAct}
            isResolving={isActionPending}
          />
        </div>
      </div>

      <ForcedSwitchPanel
        open={mustReplaceFaintedCritter}
        team={player.roster}
        onCritterClick={handleSwitchCritter}
        disabled={isActionPending}
      />
    </main>
  );
}

export default BattlePage;
