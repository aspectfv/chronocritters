import type { BattleAbility } from '@store/battle/types';
import type { AbilitySelectorProps } from '@features/battle/types';
import { getAbilityPower, getBattleEffectMeta } from '@utils/utils';

const AbilityCard: React.FC<{ ability: BattleAbility; onClick: () => void; disabled: boolean }> = ({ ability, onClick, disabled }) => {
  const effect = ability.effects[0];
  const power = getAbilityPower(effect);

  return (
    <button
      onClick={onClick}
      disabled={disabled}
      className="bg-white border-2 border-gray-200 rounded-xl p-4 text-left hover:border-green-400 hover:-translate-y-0.5 focus:outline-none focus:ring-2 focus:ring-green-400 transition-all w-full disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:translate-y-0"
    >
      <div className="flex justify-between items-start gap-2">
        <div className="min-w-0">
          <h4 className="font-bold text-lg text-gray-800">{ability.name}</h4>
          <p className="text-sm text-gray-500 mt-1">{effect?.description ?? 'No effect description.'}</p>
        </div>
        {effect && (
          <span
            className={`shrink-0 rounded-full border px-2 py-0.5 text-xs font-semibold ${getBattleEffectMeta(effect).style}`}
          >
            {getBattleEffectMeta(effect).icon} {power !== null ? power : getBattleEffectMeta(effect).label}
          </span>
        )}
      </div>
    </button>
  );
};

export function AbilitySelector({ abilities, onAbilityClick, isPlayerTurn, isResolving }: AbilitySelectorProps) {
  return (
    <div className="my-2">
      <div className={`bg-white rounded-xl shadow-sm border p-6 transition-colors ${isPlayerTurn ? 'border-green-300' : 'border-gray-200'}`}>
        <h3 className="text-center font-bold text-lg text-gray-800 mb-4">
          {isResolving ? 'Resolving your move...' : isPlayerTurn ? 'Choose Your Ability' : 'Waiting for your opponent'}
        </h3>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          {abilities.map(ability => (
            <AbilityCard
              key={ability.id}
              ability={ability}
              onClick={() => onAbilityClick(ability.id)}
              disabled={!isPlayerTurn}
            />
          ))}
        </div>
      </div>
    </div>
  );
}
