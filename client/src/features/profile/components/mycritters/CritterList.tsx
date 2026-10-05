import type { CritterListProps } from '@features/profile/types';
import { getCritterImageUrl, getCritterTypeFill, getCritterTypeIcon } from '@utils/utils';

export const CritterList = ({ roster, selectedCritter, onCritterSelect }: CritterListProps) => {
  return (
    <div className="panel h-full rounded-lg bg-arena-deep p-4">
      <h2 className="mb-3 text-sm font-bold text-arena-ink">Your critters</h2>

      {roster.length === 0 ? (
        <p className="well rounded-sm px-3 py-2 text-sm text-arena-ink-muted">
          No critters yet. Your team is handed out when you register.
        </p>
      ) : (
        <div className="flex flex-col gap-2.5">
          {roster.map((critter) => {
            const isSelected = selectedCritter?.id === critter.id;

            return (
              <button
                key={critter.id}
                type="button"
                aria-pressed={isSelected}
                onClick={() => onCritterSelect(critter)}
                className={`key flex items-center gap-3 rounded-lg p-3 text-left focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-brass/60 ${
                  isSelected ? 'bg-brass text-white' : 'bg-arena-deep text-arena-ink'
                }`}
              >
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
                </span>

                <span className="min-w-0 flex-1">
                  <span className="block truncate font-black">{critter.name}</span>
                  <span className={`flex items-center gap-2 text-xs ${isSelected ? 'text-white/85' : 'text-arena-ink-muted'}`}>
                    <span className={`rounded-full border-2 border-outline px-1.5 py-0.5 text-[10px] font-black text-white ${getCritterTypeFill(critter.type)}`}>
                      <span aria-hidden="true">{getCritterTypeIcon(critter.type)}</span> {critter.type}
                    </span>
                    <span className="numeral">Lv {critter.baseStats?.level ?? 1}</span>
                  </span>
                </span>
              </button>
            );
          })}
        </div>
      )}
    </div>
  );
};
