export const ProgressSummary = () => {
  const currentXP = 2750;
  const maxXP = 3000;
  const xpGained = 150;
  const xpPercentage = (currentXP / maxXP) * 100;

  return (
    <div className="bg-green-50 border border-green-200 rounded-lg p-6 h-full">
      <h3 className="font-semibold text-green-800 mb-4 flex items-center gap-2">
        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
        </svg>
        Your Progress
      </h3>
      <div className="mb-4">
        <div className="flex justify-between items-center text-sm text-gray-600 mb-1">
          <span>Level 15</span>
          <span>{currentXP}/{maxXP} XP</span>
        </div>
        <div className="w-full bg-green-200 rounded-full h-2.5">
          <div className="bg-green-600 h-2.5 rounded-full" style={{ width: `${xpPercentage}%` }}></div>
        </div>
        <p className="text-xs text-green-700 font-semibold mt-1">+{xpGained} XP gained!</p>
      </div>
      <div className="flex justify-around text-center mt-6">
        <div>
          <p className="text-3xl font-bold text-green-700">1</p>
          <p className="text-sm text-gray-500">Wins Added</p>
        </div>
        <div>
          <p className="text-3xl font-bold text-green-700">3</p>
          <p className="text-sm text-gray-500">Win Streak</p>
        </div>
      </div>
    </div>
  );
};