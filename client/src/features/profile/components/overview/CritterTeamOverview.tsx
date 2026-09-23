import type { CritterCardProps, CritterTeamOverviewProps } from '@features/profile/types';
import { getCritterImageUrl } from '@utils/utils';

// sub component
export function CritterCard({ name, level }: CritterCardProps) {
  return (
    <div className="text-center p-4 bg-accent-soft rounded-lg border border-accent/20">
      <div className="flex items-center justify-center mb-2">
        <img 
        src={getCritterImageUrl(name)} 
        alt={name} 
        className="max-h-24 max-w-full object-cover rounded-full" 
        onError={e => {
          const target = e.target as HTMLImageElement;
          target.src = getCritterImageUrl('Unknown');
        }}
        />
      </div>
      <p className="font-bold text-ink">{name}</p>
      <p className="text-sm text-ink-muted">Lv. {level}</p>
    </div>
  );
}

export function CritterTeamOverview({ roster }: CritterTeamOverviewProps) {
  return (
    <div className="bg-surface rounded-lg shadow-sm border border-line p-6">
      <h3 className="font-semibold text-accent-ink mb-4">Critter Team Overview</h3>
      <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
        {roster?.map((critter) => (
          <CritterCard key={critter?.id ?? critter?.name} name={critter?.name ?? 'Unknown'} level={critter?.baseStats?.level ?? 1} />
        ))}
      </div>
    </div>
  );
}