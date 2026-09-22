import type { TeamDisplayProps } from '@features/battle/types';
import { StatusEffectChips } from '@features/battle/components/StatusEffectChips';
import { getCritterImageUrl, getHealthTone } from '@utils/utils';

export function TeamDisplay({ title, team, activeCritterId, isPlayerTurn, onCritterClick }: TeamDisplayProps) {
  const getCritterIndex = (critterId: string) => { return team.findIndex(c => c.id === critterId); };
  const benched = team.filter(critter => critter.id !== activeCritterId);

  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
      <div className="flex items-baseline justify-between mb-3">
        <h3 className="font-semibold text-lg text-gray-800">{title}</h3>
        {isPlayerTurn && <span className="text-xs text-gray-500">Switching costs your turn</span>}
      </div>
      <div className="grid grid-cols-3 gap-3">
        {benched.map(critter => {
          const healthPercentage = Math.max(0, (critter.stats.currentHp / critter.stats.maxHp) * 100);
          const isFainted = critter.stats.currentHp <= 0;
          const canSwitchTo = isPlayerTurn && !isFainted;

          return (
            <button
              key={critter.id}
              type="button"
              disabled={!canSwitchTo}
              onClick={() => onCritterClick(getCritterIndex(critter.id))}
              className={`text-center p-2 rounded-lg border transition-all bg-gray-50 ${
                canSwitchTo
                  ? 'cursor-pointer border-gray-200 hover:border-green-400 focus:outline-none focus:ring-2 focus:ring-green-400'
                  : 'border-gray-200 cursor-default'
              } ${isFainted ? 'opacity-40' : ''}`}
            >
              <div className="mx-auto flex items-center justify-center">
                <img
                  src={getCritterImageUrl(critter.name)}
                  alt={critter.name}
                  className={`w-14 h-14 object-cover rounded-md ${isFainted ? 'grayscale' : ''}`}
                  onError={e => {
                    const target = e.target as HTMLImageElement;
                    target.src = getCritterImageUrl('Unknown');
                  }}
                />
              </div>
              <p className="text-sm font-semibold mt-1 text-gray-700 truncate">{critter.name}</p>
              <p className="text-xs text-gray-500 capitalize">{critter.type.toLowerCase()}</p>
              <div className="mt-1 min-h-3">
                <StatusEffectChips effects={critter.activeStatusEffects} compact />
              </div>
              <div className="w-full bg-gray-200 rounded-full h-1.5 mt-1.5">
                <div
                  className={`h-1.5 rounded-full transition-[width,background-color] duration-500 ease-out ${getHealthTone(healthPercentage)}`}
                  style={{ width: `${healthPercentage}%` }}
                ></div>
              </div>
            </button>
          );
        })}
      </div>
    </div>
  );
}
