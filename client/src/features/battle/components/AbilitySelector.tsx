import type { Ability, EffectUnion } from '@/gql/graphql';
import type { AbilitySelectorProps } from '@features/battle/types';
import { getEffectDescription } from '@utils/utils';

const getEffectType = (effect: EffectUnion | null | undefined): 'DAMAGE' | 'BUFF' | 'DEBUFF' | 'UNKNOWN' => {
  if (!effect) return 'UNKNOWN';
  if (effect.type.includes('DAMAGE')) return 'DAMAGE';
  if (effect.type === 'BUFF') return 'BUFF';
  if (effect.type === 'DEBUFF') return 'DEBUFF';
  return 'UNKNOWN';
};

const effectTypeStyles = {
  DAMAGE: 'bg-red-100 text-red-800 border-red-300',
  BUFF: 'bg-green-100 text-green-800 border-green-300',
  DEBUFF: 'bg-yellow-100 text-yellow-800 border-yellow-300',
  UNKNOWN: 'bg-gray-100 text-gray-800 border-gray-300',
};

const AbilityCard: React.FC<{ ability: Ability; onClick: () => void; disabled: boolean }> = ({ ability, onClick, disabled }) => {
  const effect = ability.effects?.[0];
  const effectType = getEffectType(effect);
  const typeStyle = effectTypeStyles[effectType];

  return (
    <button
      onClick={onClick}
      disabled={disabled}
      className="bg-white border-2 border-gray-200 rounded-xl p-4 text-left hover:border-green-400 focus:outline-none focus:ring-2 focus:ring-green-400 transition-all w-full disabled:opacity-50 disabled:cursor-not-allowed"
    >
      <div className="flex justify-between items-center">
        <div>
          <h4 className="font-bold text-lg text-gray-800">{ability.name}</h4>
          <p className="text-sm text-gray-500 mt-1">{effect ? getEffectDescription(effect) : 'No effect description.'}</p>
        </div>
        <span className={`text-xs font-semibold px-3 py-1 rounded-full border-2 ${typeStyle}`}>
          {effectType}
        </span>
      </div>
    </button>
  );
};

export function AbilitySelector({ abilities, onAbilityClick, isPlayerTurn }: AbilitySelectorProps) {
  return (
    <div className="my-2">
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
        <h3 className="text-center font-bold text-lg text-gray-800 mb-4">Choose Your Ability</h3>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          {abilities.map(ability => (
            <AbilityCard 
              key={ability.id}
              ability={ability} 
              onClick={() => onAbilityClick(ability.id ?? '')}
              disabled={!isPlayerTurn}
            />
          ))}
        </div>
      </div>
    </div>
  );
}