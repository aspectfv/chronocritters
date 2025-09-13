import { useState, useEffect } from 'react';
import type { ProgressBarProps, ProgressSummaryProps } from '@features/results/types';

// Reusable progress bar component for player or critter
const ProgressBar = ({ name, finalStats, xpGained }: ProgressBarProps) => {
  const [currentXp, setCurrentXp] = useState((finalStats?.exp ?? 0) - xpGained);
  const [currentLevel, setCurrentLevel] = useState(finalStats?.level ?? 1);
  const [expToNextLevel, setExpToNextLevel] = useState(finalStats?.expToNextLevel ?? 0);

  const finalStatsExp = finalStats?.exp ?? 0;
  const finalStatsLevel = finalStats?.level ?? 0;
  const finalStatsExpToNextLevel = finalStats?.expToNextLevel ?? 0;

  useEffect(() => {
    let xp = finalStatsExp - xpGained;
    let level = finalStats?.level ?? 1;
    let expToNext = finalStats?.expToNextLevel ?? 0;

    while (xp < 0) {
      level--;
      const prevMaxXp = 500 + level * 150;
      xp += prevMaxXp;
    }

    setCurrentLevel(level);
    setCurrentXp(xp);
    setExpToNextLevel(expToNext);
    
    setTimeout(() => {
        setCurrentXp(finalStatsExp);
        setCurrentLevel(finalStatsLevel);
        setExpToNextLevel(finalStatsExpToNextLevel);
    }, 100);

  }, [finalStats, xpGained]);

  const xpPercentage = Math.min(100, (currentXp / expToNextLevel) * 100);

  return (
    <div className="mb-3">
      <div className="flex justify-between items-center text-sm text-gray-600 mb-1">
        <span className='font-bold'>{name} (Level {currentLevel})</span>
        <span>{Math.floor(currentXp)}/{expToNextLevel} XP</span>
      </div>
      <div className="w-full bg-green-200 rounded-full h-2.5">
        <div className="bg-green-600 h-2.5 rounded-full transition-all duration-500 ease-out" style={{ width: `${xpPercentage}%` }}></div>
      </div>
    </div>
  );
};

export const ProgressSummary = ({ player, critters, xpGained }: ProgressSummaryProps) => {
  return (
    <div className="bg-green-50 border border-green-200 rounded-lg p-6 h-full">
      <h3 className="font-semibold text-green-800 mb-4 flex items-center gap-2">
        Your Progress
      </h3>

      <ProgressBar name={player?.username ?? 'Trainer'} finalStats={player?.stats} xpGained={xpGained} />

      <hr className="my-4 border-gray-300"/>

      <h4 className="font-semibold text-gray-700 mb-2 text-sm">Critter Progression</h4>
      {critters?.map(critter => (
        <ProgressBar key={critter?.id} name={critter?.name ?? 'Unknown'} finalStats={critter?.baseStats} xpGained={xpGained} />
      ))}

      <p className="text-xs text-green-700 font-semibold mt-4 text-center">+{xpGained} XP gained for everyone!</p>
    </div>
  );
};