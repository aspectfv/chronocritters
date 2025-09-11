import type { TeamDisplayProps } from '@features/battle/types';
import { getCritterImageUrl } from '@utils/utils';

export function TeamDisplay({ title, team, activeCritterId, isPlayerTurn, onCritterClick }: TeamDisplayProps) {
  const getCritterIndex = (critterId: string) => { return team.findIndex(c => c.id === critterId); };
  
  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
      <h3 className="font-semibold text-lg text-gray-800 mb-4">{title}</h3>
      <div className="grid grid-cols-3 gap-3">
        {team
          .filter(critter => critter.id !== activeCritterId)
          .map(critter => {
            const healthPercentage = (critter.stats.currentHp / critter.stats.maxHp) * 100;
            const isFainted = critter.stats.currentHp <= 0;
            const canClick = isPlayerTurn && onCritterClick;

            return (
              <div 
                key={critter.id} 
                className={`text-center p-3 rounded-lg transition-all bg-gray-50/60 border ${
                  canClick ? 'cursor-pointer hover:border-green-400' : ''
                } ${isFainted ? 'opacity-40' : ''}`}
                onClick={() => canClick && !isFainted && onCritterClick(getCritterIndex(critter.id))}
              >
                <div className="mx-auto flex items-center justify-center mb-2">
                  <img 
                    src={getCritterImageUrl(critter.name)} 
                    alt={critter.name} 
                    className="w-16 h-16 object-cover rounded-md"
                    onError={e => {
                      const target = e.target as HTMLImageElement;
                      target.src = getCritterImageUrl('Unknown');
                    }}
                  />
                </div>
                <p className="text-sm font-bold mt-1 text-gray-700">{critter.name}</p>
                <p className="text-xs text-gray-500">{critter.type}</p>
                <div className="w-full bg-gray-200 rounded-full h-1.5 mt-2">
                  <div
                    className="bg-green-600 h-1.5 rounded-full"
                    style={{ width: `${healthPercentage}%` }}
                  ></div>
                </div>
              </div>
            );
          })}
      </div>
    </div>
  );
}