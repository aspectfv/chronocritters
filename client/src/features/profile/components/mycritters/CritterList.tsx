import type { CritterListProps } from '@features/profile/types';
import { typeIcons } from '@utils/typeIcons';

export const CritterList = ({ roster, selectedCritter, onCritterSelect }: CritterListProps) => {
  return (
    <div className="md:col-span-1 bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <h3 className="font-semibold text-green-800 mb-4">My Critters ({roster.length})</h3>
      <div className="space-y-3">
        {roster.map((critter) => (
          <div 
            key={critter.id} 
            onClick={() => onCritterSelect(critter)}
            className={`p-4 rounded-lg border cursor-pointer transition-colors ${
              selectedCritter?.id === critter.id 
                ? 'bg-green-50 border-green-400' 
                : 'bg-gray-50 border-gray-200 hover:border-green-400'
            }`}
          >
            <p className="font-bold text-gray-800">
              {typeIcons[critter.type]} {critter.name}
            </p>
            <p className="text-sm text-gray-500">{critter.type}</p>
          </div>
        ))}
      </div>
    </div>
  );
};