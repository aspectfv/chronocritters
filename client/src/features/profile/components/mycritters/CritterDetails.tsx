import type { CritterData } from '@features/profile/types';
import { typeIcons } from '@utils/typeIcons';

const abilityTypeColors: Record<string, string> = {
  ATTACK: 'bg-red-100 text-red-800',
  DEFENSE: 'bg-blue-100 text-blue-800',
  SUPPORT: 'bg-green-100 text-green-800',
  UNKNOWN: 'bg-gray-100 text-gray-800',
};

export const CritterDetails = ({ critter }: { critter: CritterData | null }) => {
  if (!critter) {
    return (
      <div className="flex flex-col h-full items-center justify-center text-center">
        <div className="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-12 w-12 text-gray-400" viewBox="0 0 20 20" fill="currentColor"><path fillRule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clipRule="evenodd" /></svg>
        </div>
        <h3 className="font-semibold text-gray-800 text-xl">Select a Critter</h3>
        <p className="text-gray-500">Select a critter from the list to view its details.</p>
      </div>
    );
  }

  return (
    <div className="text-left">
      <div className="text-center mb-6">
        <div className="w-24 h-24 bg-green-100 rounded-full mx-auto flex items-center justify-center mb-2">
          <span className="text-4xl">
            {typeIcons[critter.type]}
          </span>
        </div>
        <h3 className="font-bold text-2xl text-gray-800">{critter.name}</h3>
        <p className="text-sm text-gray-500">{critter.type}</p>
      </div>

      <h4 className="font-semibold text-green-800 mb-2">Base Stats</h4>
      <div className="grid grid-cols-3 gap-4 text-center bg-gray-50 p-4 rounded-lg mb-6">
        <div>
          <p className="font-bold text-lg">{critter.baseStats.health}</p>
          <p className="text-xs text-gray-500">Health</p>
        </div>
        <div>
          <p className="font-bold text-lg">{critter.baseStats.attack}</p>
          <p className="text-xs text-gray-500">Attack</p>
        </div>
        <div>
          <p className="font-bold text-lg">{critter.baseStats.defense}</p>
          <p className="text-xs text-gray-500">Defense</p>
        </div>
      </div>

      <h4 className="font-semibold text-green-800 mb-2">Abilities</h4>
      <div className="space-y-2">
        {critter.abilities.map(ability => (
          <div key={ability.id} className={`p-3 rounded-lg border ${abilityTypeColors[ability.type]}`}>
            <p className="font-semibold text-sm">{ability.name}</p>
            <p className="text-xs">Power: {ability.power}</p>
            <span
              className={`float-right text-xs px-2 py-0.5 rounded-full ${
                abilityTypeColors[ability.type] || abilityTypeColors.UNKNOWN
              }`}
            >
              {ability.type}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
};