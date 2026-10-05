import type { CritterTeamOverviewProps } from '@features/profile/types';
import { getCritterImageUrl, getCritterTypeFill, getCritterTypeIcon } from '@utils/utils';

export function CritterTeamOverview({ roster }: CritterTeamOverviewProps) {
  const team = roster?.filter((critter) => critter !== null) ?? [];

  return (
    <div className="panel rounded-lg bg-arena-deep p-4">
      <h2 className="mb-3 text-sm font-bold text-arena-ink">Your team</h2>

      {team.length === 0 ? (
        <p className="well rounded-sm px-3 py-2 text-sm text-arena-ink-muted">
          No critters yet. Your team is handed out when you register.
        </p>
      ) : (
        <div className="grid grid-cols-2 gap-2.5 sm:grid-cols-3 md:grid-cols-4">
          {team.map((critter) => (
            <div key={critter.id ?? critter.name} className="plaque flex items-center gap-3 px-3 py-2.5">
              <span className="relative shrink-0 rounded-full border-2 border-outline bg-gradient-to-b from-brass via-brass-dim to-brass-ink p-[3px]">
                <span className="block overflow-hidden rounded-full border-2 border-outline">
                  <img
                    src={getCritterImageUrl(critter.name)}
                    alt=""
                    aria-hidden="true"
                    className="block h-11 w-11 object-cover"
                    onError={(e) => {
                      (e.target as HTMLImageElement).src = getCritterImageUrl('Unknown');
                    }}
                  />
                </span>
                <span
                  className={`absolute -bottom-1 -right-1 flex h-4.5 w-4.5 items-center justify-center rounded-full border-2 border-outline p-0.5 text-[9px] ${getCritterTypeFill(critter.type)}`}
                  aria-hidden="true"
                >
                  {getCritterTypeIcon(critter.type)}
                </span>
              </span>

              <span className="min-w-0">
                <span className="block truncate text-sm font-bold text-arena-ink">{critter.name}</span>
                <span className="numeral block text-xs text-brass-ink">Lv {critter.baseStats?.level ?? 1}</span>
              </span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
