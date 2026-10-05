import type { CatalogEntryProps } from '@features/catalog/types';
import {
  describeBattleEffect,
  getBattleEffectMeta,
  getCritterImageUrl,
  getCritterTypeFill,
  getCritterTypeIcon,
} from '@utils/utils';

function StatMeter({ label, value, ceiling, fill }: {
  label: string;
  value: number;
  ceiling: number;
  fill: string;
}) {
  return (
    <div className="flex items-center gap-2">
      <span className="numeral w-14 shrink-0 text-[11px] text-brass-ink">{label}</span>
      <span className="well block h-3.5 flex-1 overflow-hidden rounded-sm">
        <span
          className={`hp-fill block h-full ${fill}`}
          style={{ width: `${ceiling > 0 ? (value / ceiling) * 100 : 0}%` }}
        />
      </span>
      <span className="numeral w-7 shrink-0 text-right text-sm text-arena-ink">{value}</span>
    </div>
  );
}

/**
 * One critter, read the way it is played: what it is made of, then what it can
 * do with it. The ability effects are spelled out rather than summarised,
 * because a move's second effect is usually the reason to bring the critter.
 */
export function CatalogEntry({ critter, ceilings }: CatalogEntryProps) {
  const stats = critter.baseStats;
  const abilities = critter.abilities?.filter((ability) => ability !== null) ?? [];

  return (
    <article className="panel rounded-lg bg-arena-deep p-4">
      <div className="flex items-start gap-4">
        <span className="relative shrink-0 rounded-full border-[3px] border-outline bg-gradient-to-b from-brass via-brass-dim to-brass-ink p-[4px] shadow-[0_5px_0_0_var(--color-outline)]">
          <span className="relative block overflow-hidden rounded-full border-2 border-outline">
            <img
              src={getCritterImageUrl(critter.name)}
              alt=""
              aria-hidden="true"
              className="block h-24 w-24 object-cover sm:h-28 sm:w-28"
              onError={(e) => {
                (e.target as HTMLImageElement).src = getCritterImageUrl('Unknown');
              }}
            />
            <span className="pointer-events-none absolute inset-0 rounded-full bg-gradient-to-b from-white/20 via-transparent to-black/15" aria-hidden="true" />
          </span>
        </span>

        <div className="min-w-0 flex-1">
          <div className="flex flex-wrap items-center gap-2">
            <h2 className="display truncate text-xl text-arena-ink">{critter.name}</h2>
            <span className={`shrink-0 rounded-full border-2 border-outline px-2 py-0.5 text-[11px] font-black text-white ${getCritterTypeFill(critter.type)}`}>
              <span aria-hidden="true">{getCritterTypeIcon(critter.type)}</span> {critter.type}
            </span>
          </div>

          <p className="mt-1.5 text-sm leading-snug text-arena-ink-muted">{critter.description}</p>

          {stats && (
            <div className="mt-3 space-y-1.5">
              <StatMeter label="HP" value={stats.health ?? 0} ceiling={ceilings.health} fill="bg-vital" />
              <StatMeter label="Attack" value={stats.attack ?? 0} ceiling={ceilings.attack} fill="bg-ruby" />
              <StatMeter label="Defence" value={stats.defense ?? 0} ceiling={ceilings.defense} fill="bg-blued" />
            </div>
          )}
        </div>
      </div>

      <div className="mt-4 grid grid-cols-1 gap-2.5 sm:grid-cols-2">
        {abilities.map((ability) => (
          <div
            key={ability.id}
            className={`rounded-md border-2 border-outline p-3 text-white shadow-[0_3px_0_0_var(--color-outline)] ${getCritterTypeFill(critter.type)}`}
          >
            <p className="text-[15px] font-black tracking-tight drop-shadow-[0_1px_0_rgba(0,0,0,0.35)]">
              {ability.name}
            </p>
            <p className="mt-0.5 text-xs font-medium text-white/85">{ability.description}</p>

            <div className="mt-2 flex flex-wrap items-center gap-1.5">
              {(ability.effects ?? [])
                .filter((effect): effect is NonNullable<typeof effect> => effect !== null)
                .map((effect) => (
                  <span
                    key={effect.id}
                    className="numeral inline-flex items-center gap-1 rounded border-2 border-outline bg-outline/80 px-1.5 py-0.5 text-[11px] leading-tight text-white"
                  >
                    <span aria-hidden="true">{getBattleEffectMeta(effect).icon}</span>
                    {describeBattleEffect(effect)}
                  </span>
                ))}
            </div>
          </div>
        ))}
      </div>
    </article>
  );
}
