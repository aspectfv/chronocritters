export const AchievementNotification = () => {
  return (
    <div className="bg-green-50 border border-green-200 rounded-lg p-4 flex items-center gap-4">
      <svg xmlns="http://www.w3.org/2000/svg" className="h-8 w-8 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
      <div>
        <h4 className="font-bold text-green-800">Achievement Unlocked!</h4>
        <p className="text-sm text-gray-600">Win Streak: 3 battles in a row</p>
      </div>
    </div>
  );
};