import { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useBattleStore } from '@store/battle/useBattleStore';

import { VictoryHeader } from '@features/results/components/VictoryHeader';
import { ProgressSummary } from '@features/results/components/ProgressSummary';
import { RewardsSummary } from '@features/results/components/RewardsSummary';
import { BattleSummary } from '@features/results/components/BattleSummary';
import { AchievementNotification } from '@features/results/components/AchievementNotification';
import { ActionButtons } from '@features/results/components/ActionButtons';

function ResultsPage() {
  const { state } = useLocation();
  const navigate = useNavigate();
  const { resetBattleState } = useBattleStore();

  const battleResult = state?.result; 

  useEffect(() => {
    if (!battleResult) {
      navigate('/menu');
    }
    
    return () => {
      resetBattleState();
    };
  }, [battleResult, navigate, resetBattleState]);

  if (battleResult !== 'victory') {
    return (
      <div>
        <h1>Defeat!</h1>
        <ActionButtons />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-white p-4 sm:p-6 md:p-8">
      <div className="max-w-4xl mx-auto space-y-8">
        <VictoryHeader opponentName="StormCaller" />
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          <ProgressSummary />
          <RewardsSummary />
        </div>
        <BattleSummary />
        <ActionButtons />
        <AchievementNotification />
      </div>
    </div>
  );
}

export default ResultsPage;