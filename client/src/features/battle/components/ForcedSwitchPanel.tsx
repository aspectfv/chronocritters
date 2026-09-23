import type { ForcedSwitchPanelProps } from '@features/battle/types';
import { getCritterImageUrl, getCritterTypeStyle, getHealthTone } from '@utils/utils';

/**
 * Stays mounted and hidden rather than unmounting, which is what lets the exit
 * animate in plain CSS. An animation library was tried here and cost 43kB
 * gzipped for this one transition, which it did not earn.
 */
export function ForcedSwitchPanel({ open, team, onCritterClick, disabled }: ForcedSwitchPanelProps) {
  const available = team
    .map((critter, index) => ({ critter, index }))
    .filter(({ critter }) => critter.stats.currentHp > 0);

  return (
    <div
      aria-hidden={!open}
      className={`fixed inset-0 z-30 flex items-end justify-center bg-arena-ink/40 p-4 backdrop-blur-sm transition-opacity duration-200 sm:items-center ${
        open ? 'opacity-100' : 'pointer-events-none opacity-0'
      }`}
    >
      <div
        className={`w-full max-w-lg rounded-xl border-2 border-brass/40 bg-arena-deep p-6 shadow-overlay transition-all duration-300 ${
          open ? 'translate-y-0 scale-100' : 'translate-y-4 scale-95'
        }`}
      >
        <h2 className="text-center text-xl font-bold text-arena-ink">Your critter fainted</h2>
        <p className="mt-1 text-center text-sm text-arena-ink-muted">
          Choose who goes out next. This one is free — you still get your turn.
        </p>

        <div className="mt-5 grid grid-cols-1 gap-3 sm:grid-cols-2">
          {available.map(({ critter, index }) => {
            const healthPercentage = Math.max(0, (critter.stats.currentHp / critter.stats.maxHp) * 100);

            return (
              <button
                key={critter.id}
                type="button"
                disabled={disabled || !open}
                onClick={() => onCritterClick(index)}
                className="flex items-center gap-3 rounded-lg border-2 border-brass/25 bg-arena p-3 text-left transition-all hover:border-brass focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brass disabled:opacity-50"
              >
                <img
                  src={getCritterImageUrl(critter.name)}
                  alt={critter.name}
                  className="h-14 w-14 shrink-0 rounded-md object-cover"
                  onError={e => {
                    const target = e.target as HTMLImageElement;
                    target.src = getCritterImageUrl('Unknown');
                  }}
                />
                <div className="min-w-0 flex-1">
                  <div className="flex items-center justify-between gap-2">
                    <span className="truncate font-bold text-arena-ink">{critter.name}</span>
                    <span className={`shrink-0 rounded-full border px-2 py-0.5 text-xs font-semibold ${getCritterTypeStyle(critter.type)}`}>
                      {critter.type}
                    </span>
                  </div>
                  <p className="mt-1 text-xs text-arena-ink-muted">{critter.stats.currentHp}/{critter.stats.maxHp} HP</p>
                  <div className="mt-1 h-1.5 w-full rounded-full bg-arena-glass">
                    <div className={`h-1.5 rounded-full ${getHealthTone(healthPercentage)}`} style={{ width: `${healthPercentage}%` }}></div>
                  </div>
                </div>
              </button>
            );
          })}
        </div>

        <p className="mt-4 text-center text-xs text-arena-ink-muted/70">
          Run the clock down and the next critter in your roster is sent out for you.
        </p>
      </div>
    </div>
  );
}
