import type { CritterDisplayCardProps } from '@features/battle/types';
import { getCritterImageUrl, getCritterTypeStyle } from '@utils/utils';

export function CritterDisplayCard({ playerName, critter }: CritterDisplayCardProps) {
  const healthPercentage = (critter.stats.currentHp / critter.stats.maxHp) * 100;
  
  return (
    <div className="bg-green-50 border border-green-200 rounded-lg p-4">
      <div className="flex justify-between items-center text-sm mb-4">
        <span className="font-bold text-green-800">{playerName}</span>
        <span
          className={`text-xs font-semibold px-2.5 py-0.5 rounded-full whitespace-nowrap 
            ${getCritterTypeStyle(critter.type)}`}
        >
          {critter.type}
        </span>
      </div>
      <div className="text-center">
        <div className="mx-auto flex items-center justify-center mb-2">
          <img
            src={getCritterImageUrl(critter.name)}
            alt={critter.name}
            className="w-26 h-26 object-cover rounded-full"
            onError={e => {
              const target = e.target as HTMLImageElement;
              target.src = getCritterImageUrl('Unknown');
            }}
          />
        </div>
        <h2 className="text-xl font-semibold text-green-900">{critter.name}</h2>
      </div>
      <div className="my-4">
        <div className="flex justify-between items-center text-xs text-gray-600 mb-1">
          <span>HP</span>
          <span>{critter.stats.currentHp}/{critter.stats.maxHp}</span>
        </div>
        <div className="w-full bg-green-200 rounded-full h-2">
          <div className="bg-gradient-to-r from-green-600 to-yellow-400 h-2 rounded-full" style={{ width: `${healthPercentage}%` }}></div>
        </div>
      </div>
      <div className="flex justify-around text-center text-sm text-gray-700">
        <div>
          <p className="font-bold">{critter.stats.currentAtk}</p>
          <p className="text-xs">ATK</p>
        </div>
        <div>
          <p className="font-bold">{critter.stats.currentDef}</p>
          <p className="text-xs">DEF</p>
        </div>
      </div>
    </div>
  );
}