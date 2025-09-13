import { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useBattleStore } from '@store/battle/useBattleStore';

import { ResultsHeader } from '@features/results/components/ResultsHeader';
import { ProgressSummary } from '@features/results/components/ProgressSummary';
import { RewardsSummary } from '@features/results/components/RewardsSummary';
import { BattleSummary } from '@features/results/components/BattleSummary';
import { AchievementNotification } from '@features/results/components/AchievementNotification';
import { ActionButtons } from '@features/results/components/ActionButtons';
import type { Result } from '@features/results/types';
import { useAuthStore } from '@store/auth/useAuthStore';

function ResultsPage() {
  const { state } = useLocation();
  const user = useAuthStore((state) => state.user);
  const navigate = useNavigate();
  const { resetBattleState } = useBattleStore();
  const [playerStats] = useState({ level: 15, currentXp: 2750 });

  const battleResult = state?.result as Result;
  const xpGained = state?.rewards.playersExp.get(user?.id) || 0;

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
        
        {battleResult === 'victory' && (
          <>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
              <ProgressSummary 
                initialLevel={playerStats.level}
                initialXp={playerStats.currentXp}
                xpGained={xpGained}
              />
              <RewardsSummary />
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