import { useEffect, useState } from 'react';

export const BattleSummary = ({ turnCount, battleStartTime, playerDamageDealt }: { turnCount: number, battleStartTime: number, playerDamageDealt: number }) => {
  const [duration, setDuration] = useState('0:00');

  useEffect(() => {
    if (battleStartTime) {
      const endTime = Date.now();
      const diffInSeconds = Math.floor((endTime - battleStartTime) / 1000);
      const minutes = Math.floor(diffInSeconds / 60);
      const seconds = diffInSeconds % 60;
      setDuration(`${minutes}:${seconds.toString().padStart(2, '0')}`);
    }
  }, [battleStartTime]);

  return (
    <div className="bg-green-50 border border-green-200 rounded-lg p-6">
      <h3 className="font-semibold text-green-800 mb-6 text-center">Battle Summary</h3>
      <div className="flex justify-around text-center">
        <div>
          <p className="text-3xl font-bold text-green-700">{duration}</p>
          <p className="text-sm text-gray-500">Duration</p>
        </div>
        <div>
          <p className="text-3xl font-bold text-green-700">{turnCount}</p>
          <p className="text-sm text-gray-500">Turns Played</p>
        </div>
        <div>
          <p className="text-3xl font-bold text-red-500">{playerDamageDealt}</p>
          <p className="text-sm text-gray-500">Damage Dealt</p>
        </div>
      </div>
    </div>
  );
};