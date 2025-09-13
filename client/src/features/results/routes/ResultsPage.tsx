import { useEffect } from 'react';
import { useLoaderData, useLocation, useNavigate } from 'react-router-dom';
import { useBattleStore } from '@store/battle/useBattleStore';

import { ResultsHeader } from '@features/results/components/ResultsHeader';
import { ProgressSummary } from '@features/results/components/ProgressSummary';
import { RewardsSummary } from '@features/results/components/RewardsSummary';
import { BattleSummary } from '@features/results/components/BattleSummary';
import { AchievementNotification } from '@features/results/components/AchievementNotification';
import { ActionButtons } from '@features/results/components/ActionButtons';
import type { LocationState, Result } from '@features/results/types';
import type { Critter, GetPlayerResultsQuery } from '@/gql/graphql';
import { useAuthStore } from '@store/auth/useAuthStore';

function ResultsPage() {
  const locationData = useLocation();
  const user = useAuthStore((store) => store.user);
  const navigate = useNavigate();
  const { resetBattleState } = useBattleStore();
  const loaderData = useLoaderData() as GetPlayerResultsQuery;

  const state = locationData.state as LocationState;
  const battleResult = state?.result as Result;
  const xpGained = state?.battleRewards?.playersExpGained[user?.id || ''] || 0;

  const finalPlayer = loaderData?.getPlayer ?? null;
  const finalRoster = (loaderData?.getPlayer?.roster || [])
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
        <ResultsHeader result={battleResult} opponentName="StormCaller" />
        
        {battleResult === 'victory' && finalPlayer && (
          <>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
               <ProgressSummary 
                player={finalPlayer}
                critters={finalRoster}
                expGained={xpGained}
              />
              <RewardsSummary expGained={xpGained} />
            </div>
            <BattleSummary />
            <AchievementNotification />
          </>
        )}
        
        <ActionButtons />
      </div>
    </div>
  );
}

export default ResultsPage;