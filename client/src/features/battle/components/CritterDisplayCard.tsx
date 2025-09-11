import type { CritterDisplayCardProps } from '@features/battle/types';
import { getCritterImageUrl, getCritterTypeStyle } from '@utils/utils';

export function CritterDisplayCard({ playerName, critter }: CritterDisplayCardProps) {
  const healthPercentage = (critter.stats.currentHp / critter.stats.maxHp) * 100;
  
  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
      <div className="flex justify-between items-start text-sm mb-4">
        <span className="font-bold text-xl text-green-800">{playerName}</span>
        <span className={`text-xs font-semibold px-3 py-1 rounded-full border ${getCritterTypeStyle(critter.type)}`}>
          {critter.type}
        </span>
      </div>
      <div className="text-center">
        <div className="mx-auto flex items-center justify-center mb-3">
          <img
            src={getCritterImageUrl(critter.name)}
            alt={critter.name}
            className="w-32 h-32 object-cover rounded-full"
            onError={e => {
              const target = e.target as HTMLImageElement;
              target.src = getCritterImageUrl('Unknown');
            }}
          />
        </div>
        <h2 className="text-2xl font-bold text-gray-800">{critter.name}</h2>
      </div>
      <div className="my-5">
        <div className="flex justify-between items-center text-sm text-gray-600 mb-1">
          <span className="flex items-center gap-1 font-medium">
            <span className="text-red-500">♡</span> Health Points
          </span>
          <span>{critter.stats.currentHp}/{critter.stats.maxHp}</span>
        </div>
        <div className="w-full bg-gray-200 rounded-full h-2.5">
          <div className="bg-green-600 h-2.5 rounded-full" style={{ width: `${healthPercentage}%` }}></div>
        </div>
      </div>
      <div className="grid grid-cols-2 gap-3 text-center">
        <div className="bg-blue-50/60 p-3 rounded-lg">
          <p className="font-bold text-xl text-gray-800">{critter.stats.currentAtk}</p>
          <p className="text-sm text-gray-500">Attack</p>
        </div>
        <div className="bg-blue-50/60 p-3 rounded-lg">
          <p className="font-bold text-xl text-gray-800">{critter.stats.currentDef}</p>
          <p className="text-sm text-gray-500">Defense</p>
        </div>
      </div>
    </div>
  );
}