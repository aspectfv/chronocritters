import type { CritterDisplayCardProps } from '@features/battle/types';
import { getCritterImageUrl, getCritterTypeStyle } from '@utils/utils';

export function CritterDisplayCard({ playerName, critter }: CritterDisplayCardProps) {
  const healthPercentage = (critter.stats.currentHp / critter.stats.maxHp) * 100;
  
  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
      <div className="flex justify-between items-center text-sm mb-3">
        <span className="font-bold text-lg text-green-700">{playerName}</span>
        <span className={`text-xs font-semibold px-3 py-1 rounded-full border ${getCritterTypeStyle(critter.type)}`}>
          {critter.type}
        </span>
      </div>
      <div className="text-center">
        <div className="mx-auto flex items-center justify-center mb-2">
          <img
            src={getCritterImageUrl(critter.name)}
            alt={critter.name}
            className="w-28 h-28 object-cover rounded-md"
            onError={e => {
              const target = e.target as HTMLImageElement;
              target.src = getCritterImageUrl('Unknown');
            }}
          />
        </div>
        <h2 className="text-2xl font-bold text-gray-800">{critter.name}</h2>
      </div>
      <div className="my-4">
        <div className="flex justify-between items-center text-xs text-gray-600 mb-1">
          <span className="flex items-center gap-1 font-medium">
            <span className="text-red-500">♡</span> HP
          </span>
          <span>{critter.stats.currentHp}/{critter.stats.maxHp}</span>
        </div>
        <div className="w-full bg-gray-200 rounded-full h-2">
          <div className="bg-green-500 h-2 rounded-full" style={{ width: `${healthPercentage}%` }}></div>
        </div>
      </div>
      <div className="grid grid-cols-2 gap-2 text-center">
        <div className="bg-gray-100 p-2 rounded-lg">
          <p className="font-bold text-lg text-gray-700">{critter.stats.currentAtk}</p>
          <p className="text-xs text-gray-500">ATK</p>
        </div>
        <div className="bg-gray-100 p-2 rounded-lg">
          <p className="font-bold text-lg text-gray-700">{critter.stats.currentDef}</p>
          <p className="text-xs text-gray-500">DEF</p>
        </div>
      </div>
    </div>
  );
}