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
      <p className="mb-1.5 text-xs font-bold text-arena-ink">{title}</p>
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
                  className={`h-8 w-8 rounded-full border-2 border-outline object-cover ${isDown ? 'grayscale opacity-40' : ''}`}
                  onError={(e) => { (e.target as HTMLImageElement).src = getCritterImageUrl('Unknown'); }}
                />
                <span className={`absolute -bottom-0.5 -right-0.5 h-3 w-3 rounded-full border-2 border-outline ${isDown ? 'bg-arena-glass' : getCritterTypeFill(critter.type)}`} aria-hidden="true" />
              </span>

              <span className="min-w-0 flex-1 text-left">
                <span className="flex items-center gap-1">
                  <span className={`truncate text-xs font-bold ${isDown ? 'text-arena-ink-muted line-through' : 'text-arena-ink'}`}>
                    {critter.name}
                  </span>
                  <span className="shrink-0 text-[10px]" aria-hidden="true">{getCritterTypeIcon(critter.type)}</span>
                </span>
                <span className="mt-0.5 flex items-center gap-1">
                  <span className="well block h-2.5 w-12 overflow-hidden rounded-sm">
                    <span className={`hp-fill block h-full ${getHealthTone(health)}`} style={{ width: `${health}%` }} />
                  </span>
                  <span className="numeral text-[10px] text-arena-ink">
                    {critter.stats.currentHp}/{critter.stats.maxHp}
                  </span>
                </span>
                <StatusEffectChips effects={critter.activeStatusEffects} compact />
              </span>
            </>
          );

          const shell = 'flex items-center gap-2 rounded-md px-2 py-1.5 w-[9.5rem] text-left';

          return selectable ? (
            <button
              key={critter.id}
              type="button"
              onClick={() => onCritterClick?.(index)}
              aria-label={`Send out ${critter.name}, ${critter.stats.currentHp} of ${critter.stats.maxHp} health`}
              className={`key ${shell} bg-arena-deep focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-brass/60`}
            >
              {inner}
            </button>
          ) : (
            <span
              key={critter.id}
              className={`panel ${shell} bg-arena-deep ${isDown ? 'opacity-55' : ''}`}
            >
              {inner}
            </span>
          );
        })}
      </div>
    </div>
  );
}
