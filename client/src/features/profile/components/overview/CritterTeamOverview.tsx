import type { CritterCardProps, CritterTeamOverviewProps } from '@features/profile/types';
import { CritterPortrait } from '@components/ui/CritterPortrait';

// sub component
export function CritterCard({ name, level }: CritterCardProps) {
  return (
    <div className="text-center p-4 bg-accent-soft rounded-lg border border-accent/20">
      <CritterPortrait name={name} size="md" className="mb-2" />
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