import type { AbilitySelectorProps } from '@features/battle/types';
import { type Ability, CritterType } from '@store/battle/types';
import { getCritterTypeStyle, getEffectDescription } from '@utils/utils';


const AbilityCard: React.FC<{ ability: Ability; onClick: () => void; disabled: boolean; critterType: CritterType }> = 
  ({ ability, onClick, disabled, critterType }) => (
  <button
    onClick={onClick}
    disabled={disabled}
    className="bg-white border border-gray-200 rounded-lg p-4 text-left hover:border-green-400 hover:shadow-sm focus:outline-none focus:ring-2 focus:ring-green-400 transition-all w-full disabled:opacity-50 disabled:cursor-not-allowed"
  >
    <div className="flex justify-between items-start">
      <div>
        <h4 className="font-bold text-green-900">{ability.name}</h4>
        <p className="text-xs text-gray-500 mt-1">{getEffectDescription(ability.effects[0])}</p>
      </div>
      <span
        className={`text-xs font-semibold px-2.5 py-0.5 rounded-full whitespace-nowrap 
          ${getCritterTypeStyle(critterType)}`}
      >
        PWR: {ability.effects[0].damage ?? 0}
      </span>
    </div>
  </button>
);

export function AbilitySelector({ abilities, onAbilityClick, isPlayerTurn, critterType }: AbilitySelectorProps) {
  return (
    <div className="my-6">
      <h3 className="text-center font-bold text-green-800 mb-4">Choose Your Ability</h3>
      <div className="bg-green-50 border border-green-200 rounded-lg p-4">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          {abilities.map(ability => (
            <AbilityCard 
              key={ability.id}
              ability={ability} 
              onClick={() => onAbilityClick(ability.id)}
              disabled={!isPlayerTurn}
              critterType={critterType}
            />
          ))}
        </div>
      </div>
    </div>
  );
}