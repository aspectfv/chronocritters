import type { TeamRailProps } from '@features/battle/types';
import { StatusEffectChips } from '@features/battle/components/StatusEffectChips';
import { getCritterImageUrl, getCritterTypeFill, getCritterTypeIcon, getHealthTone } from '@utils/utils';

/**
 * The bench, with what is actually on it: name, type and remaining health for
 * every critter. Party dots looked tidier and told you nothing, which is the
 * wrong trade when the whole decision is who to send out next.
 */
export function TeamRail({ title, team, activeCritterId, align, canSwitch = false, onCritterClick }: TeamRailProps) {
  const bench = team
    .map((critter, index) => ({ critter, index }))
    .filter(({ critter }) => critter.id !== activeCritterId);

  if (bench.length === 0) return null;

  return (
    <div className={`w-full ${align === 'right' ? 'text-right' : 'text-left'}`}>
      <p className="mb-1 text-xs text-arena-ink-muted">{title}</p>
      <div className={`flex flex-wrap gap-1.5 ${align === 'right' ? 'justify-end' : 'justify-start'}`}>
        {bench.map(({ critter, index }) => {
          const health = Math.max(0, (critter.stats.currentHp / critter.stats.maxHp) * 100);
          const isDown = critter.stats.currentHp <= 0;
          const selectable = canSwitch && !isDown;

          const inner = (
            <>
              <span className="relative shrink-0">
                <img
                  src={getCritterImageUrl(critter.name)}
                  alt=""
                  aria-hidden="true"
                  className={`h-8 w-8 rounded-full object-cover ring-1 ring-black/10 ${isDown ? 'grayscale opacity-40' : ''}`}
                  onError={(e) => { (e.target as HTMLImageElement).src = getCritterImageUrl('Unknown'); }}
                />
                <span className={`absolute -bottom-0.5 -right-0.5 h-2.5 w-2.5 rounded-full ring-2 ring-arena-deep ${isDown ? 'bg-arena-glass' : getCritterTypeFill(critter.type)}`} aria-hidden="true" />
              </span>

              <span className="min-w-0 flex-1 text-left">
                <span className="flex items-center gap-1">
                  <span className={`truncate text-xs font-bold ${isDown ? 'text-arena-ink-muted line-through' : 'text-arena-ink'}`}>
                    {critter.name}
                  </span>
                  <span className="shrink-0 text-[10px]" aria-hidden="true">{getCritterTypeIcon(critter.type)}</span>
                </span>
                <span className="mt-0.5 flex items-center gap-1">
                  <span className="h-1 w-12 overflow-hidden rounded-full bg-arena-glass">
                    <span className={`block h-full rounded-full ${getHealthTone(health)}`} style={{ width: `${health}%` }} />
                  </span>
                  <span className="text-[10px] tabular-nums text-arena-ink-muted">
                    {critter.stats.currentHp}/{critter.stats.maxHp}
                  </span>
                </span>
                <StatusEffectChips effects={critter.activeStatusEffects} compact />
              </span>
            </>
          );

          const shell = 'flex items-center gap-2 rounded-lg border px-2 py-1.5 w-[9.5rem] transition-all';

          return selectable ? (
            <button
              key={critter.id}
              type="button"
              onClick={() => onCritterClick?.(index)}
              aria-label={`Send out ${critter.name}, ${critter.stats.currentHp} of ${critter.stats.maxHp} health`}
              className={`${shell} border-brass/30 bg-arena-deep hover:border-brass hover:bg-arena-glass/40 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brass`}
            >
              {inner}
            </button>
          ) : (
            <span
              key={critter.id}
              className={`${shell} border-brass/15 bg-arena-deep/70 ${isDown ? 'opacity-60' : ''}`}
            >
              {inner}
            </span>
          );
        })}
      </div>
    </div>
  );
}
