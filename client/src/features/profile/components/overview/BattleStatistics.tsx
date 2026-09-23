import type { BattleStatisticsProps } from "@features/profile/types";

export function BattleStatistics({ wins, losses }: BattleStatisticsProps) {
  const totalBattles = wins + losses;
  const winRate = totalBattles > 0 ? Math.round((wins / totalBattles) * 100) : 0;

  return (
    <div className="bg-surface rounded-xl shadow-sm border border-line p-6 h-full">
      <div className="flex items-center gap-3 mb-6">
        <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-accent-ink" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
        </svg>
        <h3 className="font-semibold text-lg text-ink">Battle Statistics</h3>
      </div>
      <div className="grid grid-cols-2 gap-4 mb-6">
        <div className="bg-accent-soft text-center p-4 rounded-lg">
          <p className="text-3xl font-bold text-accent-ink">{wins}</p>
          <p className="text-sm text-accent">Wins</p>
        </div>
        <div className="bg-danger-soft text-center p-4 rounded-lg">
          <p className="text-3xl font-bold text-danger">{losses}</p>
          <p className="text-sm text-danger">Losses</p>
        </div>
      </div>
      <div className="space-y-4 text-base">
        <div className="flex justify-between"><span className="text-ink-muted">Total Battles</span><span className="font-bold text-ink">{totalBattles}</span></div>
        <div className="flex justify-between"><span className="text-ink-muted">Win Rate</span><span className="font-bold text-ink">{`${winRate}%`}</span></div>
        <div>
          <div className="flex justify-between mb-1">
            <span className="text-ink-muted">Win Rate Progress</span>
            <span className="font-bold text-ink">{`${winRate}%`}</span>
          </div>
          <div className="w-full bg-line rounded-full h-2.5">
            <div className="bg-accent h-2.5 rounded-full" style={{ width: `${winRate}%` }}></div>
          </div>
        </div>
      </div>
    </div>
  );
}