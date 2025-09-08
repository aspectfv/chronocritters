import type { TeamDisplayProps } from '@features/battle/types';
import { getCritterImageUrl } from '@utils/utils';

export function TeamDisplay({ title, team, activeCritterId }: TeamDisplayProps) {
  return (
    <div>
      <h3 className="font-semibold text-green-800 mb-2">{title}</h3>
      <div className="bg-green-50 border border-green-200 rounded-lg p-4 flex justify-center gap-4 h-full">
        {team
          .filter(critter => critter.id !== activeCritterId)
          .map(critter => {
            const healthPercentage = (critter.stats.currentHp / critter.stats.maxHp) * 100;
            return (
              <div key={critter.id} className="text-center w-24">
                <div className="bg-green-100 rounded-full mx-auto flex items-center justify-center">
                  <img 
                    src={getCritterImageUrl(critter.name)} 
                    alt={critter.name} 
                    className="w-12 h-12 object-cover rounded-full"
                    onError={e => {
                      const target = e.target as HTMLImageElement;
                      target.src = getCritterImageUrl('Unknown');
                    }}
                  />
                </div>
                <p className="text-xs mt-1 text-gray-700">{critter.name}</p>
                <div className="my-1">
                  <div className="flex justify-between items-center text-xs text-gray-600 mb-0.5">
                    <span>HP</span>
                    <span>{critter.stats.currentHp}/{critter.stats.maxHp}</span>
                  </div>
                  <div className="w-full bg-green-200 rounded-full h-1.5">
                    <div
                      className="bg-gradient-to-r from-green-600 to-yellow-400 h-1.5 rounded-full"
                      style={{ width: `${healthPercentage}%` }}
                    ></div>
                  </div>
                </div>
              </div>
            );
          })}
      </div>
    </div>
  );
}