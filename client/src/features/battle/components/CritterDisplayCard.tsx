import type { CritterDisplayCardProps } from '@features/battle/types';
import { getCritterImageUrl, getCritterTypeStyle } from '@utils/utils';

export function CritterDisplayCard({ playerName, critter }: CritterDisplayCardProps) {
  const healthPercentage = (critter.stats.currentHp / critter.stats.maxHp) * 100;
  
  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
      <div className="flex justify-between items-start text-sm mb-4">
        <span className="font-bold text-2xl text-green-800">{playerName}</span>
        <span className={`text-sm font-semibold px-3 py-1 rounded-full border ${getCritterTypeStyle(critter.type)}`}>
          {critter.type}
        </span>
      </div>
      <div className="text-center">
        <div className="mx-auto flex items-center justify-center mb-3">
          <img
            src={getCritterImageUrl(critter.name)}
            alt={critter.name}
            className="w-40 h-40 object-cover rounded-md"
            onError={e => {
              const target = e.target as HTMLImageElement;
              target.src = getCritterImageUrl('Unknown');
            }}
          />
        </div>
        <h2 className="text-3xl font-bold text-gray-800">{critter.name}</h2>
      </div>
      <div className="my-6">
        <div className="flex justify-between items-center text-base text-gray-600 mb-2">
          <span className="flex items-center gap-2 font-medium">
            <span className="text-red-500">♡</span> HP
          </span>
          <span>{critter.stats.currentHp}/{critter.stats.maxHp}</span>
        </div>
        <div className="w-full bg-gray-200 rounded-full h-3">
          <div className="bg-green-600 h-3 rounded-full" style={{ width: `${healthPercentage}%` }}></div>
        </div>
      </div>
      <div className="grid grid-cols-2 gap-4 text-center">
        <div className="bg-blue-50/60 p-4 rounded-lg">
          <p className="font-bold text-2xl text-gray-800">{critter.stats.currentAtk}</p>
          <p className="text-base text-gray-500">ATK</p>
        </div>
        <div className="bg-blue-50/60 p-4 rounded-lg">
          <p className="font-bold text-2xl text-gray-800">{critter.stats.currentDef}</p>
          <p className="text-base text-gray-500">DEF</p>
        </div>
      </div>
    </div>
  );
}