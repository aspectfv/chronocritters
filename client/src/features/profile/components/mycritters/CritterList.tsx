import type { CritterListProps } from '@features/profile/types';
import { getCritterTypeIcon } from '@utils/utils';

export const CritterList = ({ roster, selectedCritter, onCritterSelect }: CritterListProps) => {
  
  // Mock data for presentation
  const mockCritterLevels: { [key: string]: string } = {
    Aqualing: "3",
    Cogling: "2",
    "Sylvan Sentinel": "4",
    Strikon: "1",
  };

  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 h-full">
      <h3 className="font-semibold text-lg text-gray-800 mb-4">My Critters ({roster.length})</h3>
      <div className="space-y-3">
        {roster.map((critter) => {
          return (
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
              <div className="flex items-center gap-1 text-yellow-500 font-semibold">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                  <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                </svg>
                <span>{mockCritterLevels[critter.name]}</span>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};