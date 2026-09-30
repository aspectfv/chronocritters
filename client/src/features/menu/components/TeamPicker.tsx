import type { TeamPickerProps } from '@features/menu/types';
import { getCritterImageUrl, getCritterTypeFill, getCritterTypeIcon } from '@utils/utils';

/**
 * The three critters this trainer takes into their next battle, picked from
 * everything they own.
 *
 * Picking order is the team order, so the first pick is the one sent out
 * first. Tapping a picked critter drops it and closes the gap behind it.
 */
export function TeamPicker({ roster, team, teamSize, onToggle }: TeamPickerProps) {
  const owned = roster?.filter((critter) => critter !== null) ?? [];

  if (owned.length === 0) {
    return (
      <p className="well rounded-sm px-3 py-2 text-sm text-arena-ink-muted">
        No critters yet. Your critters are handed out when you register.
      </p>
    );
  }

  return (
    <div className="flex flex-col gap-2">
      <p className="text-xs text-arena-ink-muted">
        Pick {teamSize}. The first one you pick leads. {team.length} of {teamSize} picked.
      </p>

      <ul className="flex flex-wrap gap-3">
        {owned.map((critter) => {
          const pick = team.indexOf(critter.id ?? '');
          const isPicked = pick >= 0;
          const isFull = team.length >= teamSize;

          return (
            <li key={critter.id}>
              <button
                type="button"
                onClick={() => critter.id && onToggle(critter.id)}
                disabled={!isPicked && isFull}
                aria-pressed={isPicked}
                aria-label={isPicked ? `${critter.name}, pick ${pick + 1}${pick === 0 ? ', leads' : ''}` : critter.name ?? undefined}
                className="flex w-16 flex-col items-center gap-1 rounded-md focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-brass/60 disabled:opacity-50"
              >
                <span
                  className={`relative rounded-full border-2 border-outline p-[3px] shadow-[0_3px_0_0_var(--color-outline)] transition-transform hover:-translate-y-0.5 ${
                    isPicked ? 'bg-gradient-to-b from-brass via-brass-dim to-brass-ink' : 'bg-arena-deep'
                  }`}
                >
                  <span className={`block overflow-hidden rounded-full border-2 border-outline ${isPicked ? '' : 'grayscale'}`}>
                    <img
                      src={getCritterImageUrl(critter.name)}
                      alt=""
                      aria-hidden="true"
                      className="block h-12 w-12 object-cover"
                      onError={(e) => {
                        (e.target as HTMLImageElement).src = getCritterImageUrl('Unknown');
                      }}
                    />
                  </span>
                  <span
                    className={`absolute -bottom-1 -right-1 flex h-5 w-5 items-center justify-center rounded-full border-2 border-outline text-[10px] ${getCritterTypeFill(critter.type)}`}
                    aria-hidden="true"
                  >
                    {getCritterTypeIcon(critter.type)}
                  </span>
                  {isPicked && (
                    <span
                      className="numeral absolute -left-1 -top-1 flex h-5 w-5 items-center justify-center rounded-full border-2 border-outline bg-brass text-[11px] text-white"
                      aria-hidden="true"
                    >
                      {pick + 1}
                    </span>
                  )}
                </span>
                <span className="w-full truncate text-center text-[11px] font-bold text-arena-ink">
                  {critter.name}
                </span>
                <span className="h-3 text-[10px] font-bold uppercase leading-none text-brass" aria-hidden="true">
                  {pick === 0 ? 'Leads' : ''}
                </span>
              </button>
            </li>
          );
        })}
      </ul>
    </div>
  );
}
