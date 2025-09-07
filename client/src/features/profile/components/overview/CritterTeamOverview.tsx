import type { CritterCardProps, CritterTeamOverviewProps } from '@features/profile/types';
import { getCritterImageUrl } from '@utils/utils';

// sub component
export function CritterCard({ name, level }: CritterCardProps) {
  return (
    <div className="text-center p-4 bg-green-50/50 rounded-lg border border-green-100">
      <div className="h-16 flex items-center justify-center mb-2">
        <img src={getCritterImageUrl(name)} alt={name} className="max-h-16 max-w-full object-contain" />
      </div>
      <p className="font-bold text-gray-800">{name}</p>
      <p className="text-sm text-gray-500">Lv. {level}</p>
    </div>
  );
}

export function CritterTeamOverview({ roster }: CritterTeamOverviewProps) {
  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <h3 className="font-semibold text-green-800 mb-4">Critter Team Overview</h3>
      <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
        {roster.map((critter) => (
          <CritterCard key={critter.name} name={critter.name} type={critter.type} level={1} />
        ))}
      </div>
    </div>
  );
}