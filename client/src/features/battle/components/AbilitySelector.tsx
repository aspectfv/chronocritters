import type { Ability } from '@store/battle/types';

interface AbilitySelectorProps {
  abilities: Ability[];
  onAbilityClick: (abilityId: string) => void; // Changed prop to expect an ID
  isPlayerTurn: boolean;
}

// Sub-component for a single ability button
const AbilityCard: React.FC<{ ability: Ability; onClick: () => void; disabled: boolean }> = ({ ability, onClick, disabled }) => (
  <button
    onClick={onClick}
    disabled={disabled}
    className="bg-white border border-gray-200 rounded-lg p-4 text-left hover:border-green-400 hover:shadow-sm focus:outline-none focus:ring-2 focus:ring-green-400 transition-all w-full disabled:opacity-50 disabled:cursor-not-allowed"
  >
    <div className="flex justify-between items-start">
      <div>
        <h4 className="font-bold text-green-900">{ability.name}</h4>
        <p className="text-xs text-gray-500 mt-1">{ability.description}</p>
      </div>
      <span className={`text-xs font-semibold px-2.5 py-0.5 rounded-full whitespace-nowrap ${
        ability.type === 'FIRE' ? 'bg-red-100 text-red-800' : 
        ability.type === 'WATER' ? 'bg-blue-100 text-blue-800' :
        ability.type === 'GRASS' ? 'bg-green-100 text-green-800' :
        ability.type === 'ELECTRIC' ? 'bg-yellow-100 text-yellow-800' :
        'bg-gray-100 text-gray-800'
      }`}>
        PWR: {ability.power}
      </span>
    </div>
  </button>
);

export function AbilitySelector({ abilities, onAbilityClick, isPlayerTurn }: AbilitySelectorProps) {
  return (
    <div className="my-6">
      <h3 className="text-center font-bold text-green-800 mb-4">Choose Your Ability</h3>
      <div className="bg-green-50 border border-green-200 rounded-lg p-4">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          {abilities.map(ability => (
            <AbilityCard 
              key={ability.id} // Use the unique ID for the key
              ability={ability} 
              onClick={() => onAbilityClick(ability.id)} // Pass the ability ID on click
              disabled={!isPlayerTurn}
            />
          ))}
        </div>
      </div>
    </div>
  );
}