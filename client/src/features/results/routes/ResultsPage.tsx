import { useEffect } from 'react';
import { useLoaderData, useLocation, useNavigate } from 'react-router-dom';
import { useBattleStore } from '@store/battle/useBattleStore';

import { ResultsHeader } from '@features/results/components/ResultsHeader';
import { ProgressSummary } from '@features/results/components/ProgressSummary';
import { RewardsSummary } from '@features/results/components/RewardsSummary';
import { BattleSummary } from '@features/results/components/BattleSummary';
import { ActionButtons } from '@features/results/components/ActionButtons';
import type { LocationState, Result, ResultsLoaderData } from '@features/results/types';
import type { Critter } from '@/gql/graphql';
import { useAuthStore } from '@store/auth/useAuthStore';

function ResultsPage() {
  const locationData = useLocation();
  const user = useAuthStore((store) => store.user);
  const navigate = useNavigate();
  const { resetBattleState } = useBattleStore();
  const { playerResults, matchHistoryEntry } = useLoaderData() as ResultsLoaderData;

  const state = locationData.state as LocationState | undefined;
  const battleState = state?.battleState;

  // Arriving straight from the battle carries the richer payload in router
  // state; a reload has only the recorded match to go on.
  const recordedResult: Result = matchHistoryEntry ? (matchHistoryEntry.winnerId === user?.id ? 'victory' : 'defeat') : null;
  const battleResult: Result = state?.result ?? recordedResult;

  const expGained = battleState?.battleRewards?.playersExpGained?.[user?.id || ''] || 0;
  const playerDamageDealt = battleState?.battleStats?.playersDamageDealt?.[user?.id || ''] ?? matchHistoryEntry?.damageDealt ?? 0;
  const turnCount = battleState?.battleStats?.turnCount ?? matchHistoryEntry?.turnCount ?? 0;
  const duration = battleState?.battleStats?.duration ?? matchHistoryEntry?.duration ?? 0;
  const opponentName = battleState?.opponent?.username ?? matchHistoryEntry?.opponentUsername ?? 'your opponent';
  const critterExpGained = battleState?.battleRewards?.crittersExpGained ?? {};

  const finalPlayer = playerResults?.getPlayer ?? null;
  const finalRoster = (playerResults?.getPlayer?.roster || [])
    .filter((c): c is Critter => c !== null && typeof c.name === 'string');

  useEffect(() => {
    if (!battleResult) {
      navigate('/menu');
    }
    return () => {
      resetBattleState();
    };
  }, [battleResult, navigate, resetBattleState]);

  if (!battleResult) {
    return null;
  }

  return (
    <div className="min-h-screen bg-white p-4 sm:p-6 md:p-8">
      <div className="max-w-4xl mx-auto space-y-8">
        <ResultsHeader result={battleResult} opponentName={opponentName} />

        {finalPlayer && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            <ProgressSummary
              player={finalPlayer}
              critters={finalRoster}
              expGained={expGained}
              critterExpGained={critterExpGained}
            />
            <RewardsSummary expGained={expGained} />
          </div>
        )}

        <BattleSummary turnCount={turnCount} playerDamageDealt={playerDamageDealt} duration={duration} />

        <ActionButtons />
      </div>
    </div>
  );
}

export default ResultsPage;
