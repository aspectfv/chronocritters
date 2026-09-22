import { useEffect, useCallback, useState } from 'react';
import { useParams, useNavigate, useLoaderData } from 'react-router-dom';
import { useAuthStore } from '@store/auth/useAuthStore';
import { useLobbyStore } from '@store/lobby/useLobbyStore';
import { useBattleStore } from '@store/battle/useBattleStore';
import type { BattleData } from '@store/battle/types';

import { BattleHeader } from '@features/battle/components/BattleHeader';
import { TimerBar } from '@features/battle/components/TimerBar';
import { CritterDisplayCard } from '@features/battle/components/CritterDisplayCard';
import { TeamDisplay } from '@features/battle/components/TeamDisplay';
import { BattleLog } from '@features/battle/components/BattleLog';
import { AbilitySelector } from '@features/battle/components/AbilitySelector';
import { executeAbility, switchCritter } from '@api/gamelogic';
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
  const { player, opponent, actionLogHistory, timeRemaining, battleId: storeBattleId, setBattleState } = useBattleStore();

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

  const handleAbilityClick = useCallback(async (abilityId: string) => {
    const { player } = useBattleStore.getState();

    if (!player.hasTurn || !battleId || isActionPending) {
      return;
    }

    setActionError(null);
    setIsActionPending(true);

    try {
      await executeAbility(battleId, abilityId);
    } catch (error) {
      setActionError(errorMessage(error, 'That move could not be played. Please try again.'));
      setIsActionPending(false);
    }
  }, [battleId, isActionPending]);

  const handleSwitchCritter = useCallback(async (targetCritterIndex: number) => {
    if (!player.hasTurn || !battleId || isActionPending) {
      return;
    }

    setActionError(null);
    setIsActionPending(true);

    try {
      await switchCritter(battleId, targetCritterIndex);
    } catch (error) {
      setActionError(errorMessage(error, 'That critter could not be sent out. Please try again.'));
      setIsActionPending(false);
    }
  }, [battleId, player.hasTurn, isActionPending]);

  const handleForfeit = useCallback(() => {
    if (!battleId || !window.confirm('Forfeit this battle? Your opponent will be awarded the win.')) {
      return;
    }
    publish(`/app/battle/${battleId}/forfeit`, {});
  }, [battleId, publish]);

  const canAct = player.hasTurn && !isActionPending;

  return (
    <div className="min-h-screen bg-[#f0f7f3] p-4">
      <div className="max-w-screen-xl mx-auto relative">
        <BattleHeader isPlayerTurn={player.hasTurn} onForfeit={handleForfeit} />
        <TimerBar timeRemaining={timeRemaining} />

        {actionError && (
          <div className="mt-4 bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg px-4 py-3 text-center">
            {actionError}
          </div>
        )}

        <div className="grid grid-cols-1 lg:grid-cols-[2.5fr_3fr_2.5fr] gap-4 mt-4">
          <div className="flex flex-col gap-4">
            <CritterDisplayCard playerName={player.username} critter={player.activeCritter} />
            <TeamDisplay title="Your Team" team={player.roster} activeCritterId={player.activeCritter.id} isPlayerTurn={canAct} onCritterClick={handleSwitchCritter} />
          </div>

          <div className="flex flex-col gap-4">
            <BattleLog log={actionLogHistory} />
            <AbilitySelector
              abilities={player.activeCritter.abilities}
              onAbilityClick={handleAbilityClick}
              isPlayerTurn={canAct}
            />
          </div>

          <div className="flex flex-col gap-4">
            <CritterDisplayCard playerName={opponent.username} critter={opponent.activeCritter} />
            <TeamDisplay title="Opponent's Team" team={opponent.roster} activeCritterId={opponent.activeCritter.id} isPlayerTurn={false} onCritterClick={() => {}} />
          </div>
        </div>
      </div>
    </div>
  );
}

export default BattlePage;
