import { useEffect, useState } from 'react';
import type { CritterCellProps } from '@features/battle/types';
import { StatusEffectChips } from '@features/battle/components/StatusEffectChips';
import {
  getCritterImageUrl,
  getCritterTypeFill,
  getCritterTypeIcon,
  getEffectivenessLabel,
  getHealthTone,
} from '@utils/utils';

const HIT_ANIMATION_MS = 1100;

/**
 * A critter suspended in a brass-rimmed glass cell.
 *
 * The art is 1024x1024 with a saturated gradient baked in and no alpha, so it
 * cannot be dropped onto a battlefield. Framing it turns that gradient into the
 * glow inside the cell rather than a background that has to be hidden.
 */
export function CritterCell({
  playerName,
  critter,
  side,
  showNumericHp = false,
  hitTurn,
  hitDamage = 0,
  hitEffectiveness = 1,
}: CritterCellProps) {
  const [isHit, setIsHit] = useState(false);

  useEffect(() => {
    if (hitTurn === undefined) return;
    setIsHit(true);
    const settle = setTimeout(() => setIsHit(false), HIT_ANIMATION_MS);
    return () => clearTimeout(settle);
  }, [hitTurn]);

  const health = Math.max(0, (critter.stats.currentHp / critter.stats.maxHp) * 100);
  const effectivenessLabel = getEffectivenessLabel(hitEffectiveness);
  const isPlayer = side === 'player';

  return (
    <div className={`flex flex-col gap-3 ${isPlayer ? 'items-start' : 'items-end'}`}>
      {/* Plaque. Only your own cell shows the numeric HP, which is the genre
          convention and the better information design besides. */}
      <div className={`w-full max-w-xs rounded-lg border border-brass/40 bg-arena-deep/80 px-3 py-2 backdrop-blur-sm ${isPlayer ? 'order-2' : 'order-1'}`}>
        <div className="flex items-baseline justify-between gap-2">
          <span className="truncate font-bold text-arena-ink">{critter.name}</span>
          <span className={`shrink-0 rounded-full px-2 py-0.5 text-[10px] font-bold uppercase tracking-wide text-white ${getCritterTypeFill(critter.type)}`}>
            <span aria-hidden="true">{getCritterTypeIcon(critter.type)}</span> {critter.type}
          </span>
        </div>

        <div className="mt-1.5 flex items-center gap-2">
          <span className="text-[10px] font-bold uppercase tracking-widest text-brass-dim">HP</span>
          <div className="h-2 flex-1 overflow-hidden rounded-full bg-arena-glass">
            <div
              className={`h-full rounded-full transition-[width,background-color] duration-500 ease-out ${getHealthTone(health)}`}
              style={{ width: `${health}%` }}
            />
          </div>
          {showNumericHp && (
            <span className="shrink-0 text-xs font-semibold tabular-nums text-arena-ink-muted">
              {critter.stats.currentHp}/{critter.stats.maxHp}
            </span>
          )}
        </div>

        <div className="mt-1.5 flex items-center justify-between gap-2">
          <span className="truncate text-[11px] text-arena-ink-muted">{playerName}</span>
          <StatusEffectChips effects={critter.activeStatusEffects} compact />
        </div>
      </div>

      {/* The cell itself. */}
      <div
        className={`relative order-1 shrink-0 rounded-full p-1 ${isPlayer ? 'order-1' : 'order-2'} ${isHit ? 'animate-critter-hit' : ''}`}
      >
        <div className="rounded-full bg-gradient-to-b from-brass via-brass-dim to-brass/60 p-[3px] shadow-raised">
          <div className="relative overflow-hidden rounded-full bg-arena-deep">
            <img
              src={getCritterImageUrl(critter.name)}
              alt={critter.name}
              className={`block object-cover transition-all duration-500 ${isPlayer ? 'h-40 w-40 sm:h-52 sm:w-52' : 'h-32 w-32 sm:h-40 sm:w-40'} ${critter.fainted ? 'grayscale opacity-40' : ''}`}
              onError={(e) => {
                (e.target as HTMLImageElement).src = getCritterImageUrl('Unknown');
              }}
            />
            {/* Glass: a highlight across the top and a vignette at the base. */}
            <div className="pointer-events-none absolute inset-0 rounded-full bg-gradient-to-b from-white/25 via-transparent to-black/40" aria-hidden="true" />
            <div className="pointer-events-none absolute inset-0 rounded-full ring-1 ring-inset ring-white/20" aria-hidden="true" />
          </div>
        </div>

        {isHit && hitDamage > 0 && (
          <div className="pointer-events-none absolute left-1/2 top-4 z-10 animate-damage-float text-center">
            <span className={`block font-extrabold drop-shadow-lg ${hitEffectiveness > 1 ? 'text-5xl text-danger' : 'text-4xl text-white'}`}>
              -{hitDamage}
            </span>
            {effectivenessLabel && (
              <span className="mt-1 block rounded-full bg-brass px-2 py-0.5 text-[10px] font-black uppercase tracking-wider text-arena-deep">
                {effectivenessLabel}
              </span>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
