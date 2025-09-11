import type { CritterData } from '@features/profile/types';
import { getCritterImageUrl, getEffectDescription } from '@utils/utils';

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
        <div className="mx-auto flex items-center justify-center mb-2">
          <img
            src={getCritterImageUrl(critter.name ?? 'Unknown')}
            alt={critter.name ?? 'Unknown Critter'}
            className="w-26 h-26 object-cover rounded-full"
            onError={e => {
              const target = e.target as HTMLImageElement;
              target.src = getCritterImageUrl('Unknown');
            }}
          />
        </div>
        <h3 className="font-bold text-2xl text-gray-800">{critter.name ?? 'Unknown Critter'}</h3>
        <p className="text-sm text-gray-500">{critter.type ?? 'Unknown Type'}</p>
      </div>

      <h4 className="font-semibold text-green-800 mb-2">Base Stats</h4>
      <div className="grid grid-cols-3 gap-4 text-center bg-gray-50 p-4 rounded-lg mb-6">
        <div>
          <p className="font-bold text-lg">{critter.baseStats?.health ?? 0}</p>
          <p className="text-xs text-gray-500">Health</p>
        </div>
        <div>
          <p className="font-bold text-lg">{critter.baseStats?.attack ?? 0}</p>
          <p className="text-xs text-gray-500">Attack</p>
        </div>
        <div>
          <p className="font-bold text-lg">{critter.baseStats?.defense ?? 0}</p>
          <p className="text-xs text-gray-500">Defense</p>
        </div>
      </div>

      <h4 className="font-semibold text-green-800 mb-2">Abilities</h4>
      <div className="space-y-3">
        {critter.abilities?.map(ability => (
          <div key={ability?.id} className="p-4 rounded-lg border bg-white border-gray-200">
            <p className="font-semibold text-gray-900">{ability?.name}</p>
            <div className="mt-2 pl-2 border-l-2 border-gray-200 space-y-1">
              {ability?.effects?.map(ef => ef ? getEffectDescription(ef) : '')}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};