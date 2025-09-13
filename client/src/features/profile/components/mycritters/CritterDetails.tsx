import type { CritterData } from '@features/profile/types';
import { getCritterImageUrl, getCritterTypeStyle } from '@utils/utils';

export const CritterDetails = ({ critter }: { critter: CritterData | null }) => {
  if (!critter) {
    return (
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 h-full flex flex-col items-center justify-center text-center">
        <div className="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-12 w-12 text-gray-400" viewBox="0 0 20 20" fill="currentColor"><path fillRule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clipRule="evenodd" /></svg>
        </div>
        <h3 className="font-semibold text-gray-800 text-xl">Select a Critter</h3>
        <p className="text-gray-500">Select a critter from the list to view its details.</p>
      </div>
    );
  }

  // Mock data for presentation
  const mockDetails = {
    level: 18,
    currentXp: 1250,
    maxXp: 1500,
  };
  const xpPercentage = (mockDetails.currentXp / mockDetails.maxXp) * 100;

  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 h-full">
      <h3 className="font-semibold text-lg text-gray-800 mb-6">{critter.name} Details</h3>
      
      <div className="text-center mb-6">
        <div className={`mx-auto flex items-center justify-center mb-4 w-28 h-28`}>
          <img
            src={getCritterImageUrl(critter.name ?? 'Unknown')}
            alt={critter.name ?? 'Unknown Critter'}
            className="w-28 h-28 object-cover rounded-full"
            onError={e => {
              const target = e.target as HTMLImageElement;
              target.src = getCritterImageUrl('Unknown');
            }}
          />
        </div>
        <h3 className="font-bold text-2xl text-gray-800">{critter.name}</h3>
        <span className={`${getCritterTypeStyle(critter.type)} text-black text-xs font-semibold px-3 py-1 rounded-full`}>{critter.type}</span>
        {critter.description && (
          <p className="mt-2 text-gray-600 text-sm">{critter.description}</p>
        )}
      </div>

      <div className="mb-8">
        <div className="flex justify-between text-sm mb-1">
          <span className="font-semibold text-gray-700">Level {mockDetails.level}</span>
          <span className="text-gray-500">{mockDetails.currentXp} / {mockDetails.maxXp} XP</span>
        </div>
        <div className="w-full bg-gray-200 rounded-full h-2">
          <div className="bg-green-600 h-2 rounded-full" style={{width: `${xpPercentage}%`}}></div>
        </div>
      </div>

      <h4 className="font-semibold text-gray-800 mb-4">Base Stats</h4>
      <div className="grid grid-cols-3 gap-3 mb-8">
        <div className="bg-blue-50/60 p-3 rounded-lg flex items-center gap-2 font-medium text-gray-700">
          <span className="text-red-500">♡</span> HP: {critter.baseStats?.health ?? 0}
        </div>
        <div className="bg-blue-50/60 p-3 rounded-lg flex items-center gap-2 font-medium text-gray-700">
          <span className="text-orange-500">⚔</span> ATK: {critter.baseStats?.attack ?? 0}
        </div>
        <div className="bg-blue-50/60 p-3 rounded-lg flex items-center gap-2 font-medium text-gray-700">
          <span className="text-blue-500">🛡</span> DEF: {critter.baseStats?.defense ?? 0}
        </div>
      </div>

      <h4 className="font-semibold text-gray-800 mb-4">Abilities</h4>
      <div className="space-y-3">
        {critter.abilities?.map(ability => (
          <div key={ability?.id} className="p-3 rounded-lg bg-gray-50 border border-gray-200">
            <p className="font-semibold text-gray-700">{ability?.name}</p>
          </div>
        ))}
      </div>
    </div>
  );
};