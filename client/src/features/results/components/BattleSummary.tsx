import { formatDuration } from "@utils/utils";

export const BattleSummary = ({ turnCount, playerDamageDealt, duration }: { turnCount: number, playerDamageDealt: number, duration: number }) => {
  return (
    <div className="bg-green-50 border border-green-200 rounded-lg p-6">
      <h3 className="font-semibold text-green-800 mb-6 text-center">Battle Summary</h3>
      <div className="flex justify-around text-center">
        <div>
          <p className="text-3xl font-bold text-green-700">{formatDuration(duration)}</p>
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