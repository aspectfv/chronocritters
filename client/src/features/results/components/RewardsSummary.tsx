export const RewardsSummary = ({ expGained }: { expGained: number }) => {
  return (
    <div className="bg-green-50 border border-green-200 rounded-lg p-6 h-full">
      <h3 className="font-semibold text-green-800 mb-4 flex items-center gap-2">
        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        Battle Rewards
      </h3>
      <div className="space-y-3">
        <div className="flex justify-between items-center bg-white p-3 rounded-lg">
          <span className="font-medium text-gray-700 text-sm">Experience</span>
          <span className="font-bold text-green-600">+{expGained}</span>
        </div>
        <div className="flex justify-between items-center bg-white p-3 rounded-lg">
          <span className="font-medium text-gray-700 text-sm">Coins</span>
          <span className="font-bold text-yellow-500">+75</span>
        </div>
        <div>
          <p className="text-sm font-medium text-gray-700 mt-4 mb-2">Items Received:</p>
          <div className="space-y-2 text-sm text-gray-600">
            <p>⭐ Potion</p>
            <p>⭐ Fire Stone Fragment</p>
          </div>
        </div>
      </div>
    </div>
  );
};