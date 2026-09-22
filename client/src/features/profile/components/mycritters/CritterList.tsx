import type { CritterListProps } from '@features/profile/types';
import { getCritterTypeIcon } from '@utils/utils';

export const CritterList = ({ roster, selectedCritter, onCritterSelect }: CritterListProps) => {
  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 h-full">
      <h3 className="font-semibold text-lg text-gray-800 mb-4">My Critters ({roster.length})</h3>
      {roster.length === 0 ? (
        <p className="text-sm text-gray-500">You have no critters yet.</p>
      ) : (
        <div className="space-y-3">
          {roster.map((critter) => (
            <div
              key={critter.id}
              onClick={() => onCritterSelect(critter)}
              className={`p-4 rounded-lg border-2 cursor-pointer transition-colors flex items-center gap-4 ${
                selectedCritter?.id === critter.id
                  ? 'bg-green-50/50 border-green-500'
                  : 'bg-white border-gray-200 hover:border-green-300'
              }`}
            >
              <span className="text-3xl">{getCritterTypeIcon(critter.type)}</span>
              <div className="flex-grow">
                <p className="font-bold text-gray-800">{critter.name}</p>
                <div className="flex items-center gap-2 text-sm text-gray-500">
                  <span>{critter.type}</span>
                  <span className="bg-gray-200 px-2 py-0.5 rounded-full text-xs font-semibold">Level {critter.baseStats?.level ?? 1}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
