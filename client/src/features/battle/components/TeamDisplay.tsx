import type { TeamDisplayProps } from '@features/battle/types';
import { getCritterImageUrl } from '@utils/utils';

export function TeamDisplay({ title, team, activeCritterId }: TeamDisplayProps) {
  return (
    <div>
      <h3 className="font-semibold text-green-800 mb-2">{title}</h3>
      <div className="bg-green-50 border border-green-200 rounded-lg p-4 flex justify-center gap-4 h-full">
        {team
          .filter(critter => critter.id !== activeCritterId)
          .map(critter => (
            <div key={critter.id} className="text-center">
              <div className="w-12 h-12 bg-green-100 rounded-full mx-auto flex items-center justify-center">
                <img src={getCritterImageUrl(critter.name)} alt={critter.name} className="w-10 h-10 object-contain" />
              </div>
              <p className="text-xs mt-1 text-gray-700">{critter.name}</p>
              <p className="text-xs text-gray-500">{critter.stats.currentHp}/{critter.stats.maxHp}</p>
            </div>
        ))}
      </div>
    </div>
  );
}