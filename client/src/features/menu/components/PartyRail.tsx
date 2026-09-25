import { Link } from 'react-router-dom';
import type { PartyRailProps } from '@features/menu/types';
import { getCritterImageUrl, getCritterTypeFill, getCritterTypeIcon } from '@utils/utils';

/**
 * The team, on the menu rather than two screens away.
 *
 * A trainer's party is the thing they recognise their own save by, so it is the
 * one piece of content the menu leads with. It uses the arena's framing, so the
 * critters look the same here as they do on the field.
 */
export function PartyRail({ roster }: PartyRailProps) {
  const party = roster?.filter((critter) => critter !== null) ?? [];

  if (party.length === 0) {
    return (
      <p className="well rounded-sm px-3 py-2 text-sm text-arena-ink-muted">
        No critters yet. Your team is handed out when you register.
      </p>
    );
  }

  return (
    <Link
      to="/profile/critters"
      className="flex flex-wrap gap-3 rounded-md focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-brass/60"
      aria-label="Your party. Opens your critters."
    >
      {party.map((critter) => (
        <span key={critter.id} className="flex w-16 flex-col items-center gap-1">
          <span className="relative rounded-full border-2 border-outline bg-gradient-to-b from-brass via-brass-dim to-brass-ink p-[3px] shadow-[0_3px_0_0_var(--color-outline)] transition-transform hover:-translate-y-0.5">
            <span className="block overflow-hidden rounded-full border-2 border-outline">
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
          </span>
          <span className="w-full truncate text-center text-[11px] font-bold text-arena-ink">
            {critter.name}
          </span>
        </span>
      ))}
    </Link>
  );
}
