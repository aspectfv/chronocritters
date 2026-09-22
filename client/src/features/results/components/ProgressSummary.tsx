import { useState, useEffect } from 'react';
import type { ProgressBarProps, ProgressSummaryProps } from '@features/results/types';

const ProgressBar = ({ name, finalStats, expGained }: ProgressBarProps) => {
  const finalExp = finalStats?.experience ?? 0;
  const finalLevel = finalStats?.level ?? 1;
  const expToNextLevel = finalStats?.expToNextLevel ?? 0;

  // The backend already applied the gain, so animate from where the bar stood
  // before it. A level-up resets the bar, so clamp rather than trying to
  // reconstruct the previous level's requirement on the client.
  const [currentExp, setCurrentExp] = useState(Math.max(0, finalExp - expGained));

  useEffect(() => {
    setCurrentExp(Math.max(0, finalExp - expGained));
    const timer = setTimeout(() => setCurrentExp(finalExp), 500);
    return () => clearTimeout(timer);
  }, [finalExp, expGained]);

  const expPercentage = expToNextLevel > 0 ? Math.min(100, (currentExp / expToNextLevel) * 100) : 0;

  return (
    <div className="mb-3">
      <div className="flex justify-between items-center text-sm text-gray-600 mb-1">
        <span className="font-bold">{name} (Level {finalLevel})</span>
        <span>{Math.floor(currentExp)}/{expToNextLevel} XP</span>
      </div>
      <div className="w-full bg-green-200 rounded-full h-2.5">
        <div className="bg-green-600 h-2.5 rounded-full transition-all duration-1000 ease-out" style={{ width: `${expPercentage}%` }}></div>
      </div>
      <p className="text-xs text-green-700 font-semibold text-right mt-1">+{expGained} XP</p>
    </div>
  );
};

export const ProgressSummary = ({ player, critters, expGained, critterExpGained }: ProgressSummaryProps) => {
  return (
    <div className="bg-green-50 border border-green-200 rounded-lg p-6 h-full">
      <h3 className="font-semibold text-green-800 mb-4">Your Progress</h3>

      <ProgressBar name={player?.username ?? 'Trainer'} finalStats={player?.stats} expGained={expGained} />

      <hr className="my-4 border-gray-300" />

      <h4 className="font-semibold text-gray-700 mb-2 text-sm">Critter Progression</h4>
      {critters?.map((critter) => (
        <ProgressBar
          key={critter?.id}
          name={critter?.name ?? 'Unknown'}
          finalStats={critter?.baseStats}
          expGained={critterExpGained[critter?.id ?? ''] ?? 0}
        />
      ))}
    </div>
  );
};
