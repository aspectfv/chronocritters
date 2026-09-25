import type { BattleStatisticsProps } from '@features/profile/types';

export function BattleStatistics({ wins, losses }: BattleStatisticsProps) {
  const totalBattles = wins + losses;
  const winRate = totalBattles > 0 ? Math.round((wins / totalBattles) * 100) : 0;

  return (
    <div className="panel flex h-full flex-col rounded-lg bg-arena-deep p-4">
      <h2 className="mb-3 text-sm font-bold text-arena-ink">Record</h2>

      <dl className="grid grid-cols-3 gap-2.5">
        <div className="plaque px-3 py-3 text-center">
          <dd className="numeral text-3xl text-vital">{wins}</dd>
          <dt className="text-xs font-bold text-arena-ink-muted">Won</dt>
        </div>
        <div className="plaque px-3 py-3 text-center">
          <dd className="numeral text-3xl text-ruby">{losses}</dd>
          <dt className="text-xs font-bold text-arena-ink-muted">Lost</dt>
        </div>
        <div className="plaque px-3 py-3 text-center">
          <dd className="numeral text-3xl text-arena-ink">{totalBattles}</dd>
          <dt className="text-xs font-bold text-arena-ink-muted">Fought</dt>
        </div>
      </dl>

      <div className="mt-auto pt-4">
        <div className="mb-1.5 flex items-baseline justify-between">
          <span className="text-xs font-bold text-arena-ink">How close to even</span>
          <span className="numeral text-sm text-arena-ink">{winRate}%</span>
        </div>
        <div
          className="well h-4 w-full overflow-hidden rounded-sm"
          role="progressbar"
          aria-label="Win rate"
          aria-valuenow={winRate}
          aria-valuemin={0}
          aria-valuemax={100}
        >
          <div className="hp-fill h-full bg-vital" style={{ width: `${winRate}%` }} />
        </div>
      </div>
    </div>
  );
}
