import type { GetBattleHistoryQuery } from '@/gql/graphql';
import { useAuthStore } from '@store/auth/useAuthStore';
import { formatTimestamp } from '@utils/utils';
import { Link, useLoaderData } from 'react-router-dom';

export function BattleHistoryTab() {
  const user = useAuthStore((store) => store.user);
  const loaderData = useLoaderData() as GetBattleHistoryQuery;
  const battleHistory = loaderData?.getPlayer?.matchHistory || [];
  
  return (
    <div className="bg-surface rounded-lg shadow-sm border border-line p-6">
      <h3 className="font-semibold text-accent-ink mb-4">Recent Battle History</h3>
      <div className="space-y-3">
        {battleHistory.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-12 text-ink-faint">
            <svg className="w-14 h-14 mb-4" viewBox="0 0 64 64" fill="none" aria-hidden="true">
              <circle cx="32" cy="32" r="30" stroke="currentColor" strokeWidth="4" fill="none" />
              <path d="M20 44c0-6 8-10 12-10s12 4 12 10" stroke="currentColor" strokeWidth="3" strokeLinecap="round" />
              <circle cx="24" cy="28" r="3" fill="currentColor" />
              <circle cx="40" cy="28" r="3" fill="currentColor" />
              <path d="M28 36c1.5 2 6.5 2 8 0" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
            </svg>
            <span className="text-lg font-semibold">No battles found</span>
            <span className="text-sm mt-2">Start a match to see your battle history here!</span>
          </div>
        ) : (
          battleHistory.map((item, index) => {
            const result = (item?.winnerId ?? '') === (user?.id ?? '') ? 'Victory' : 'Defeat';
            return item ? (
              <Link to={`/profile/history/${item.battleId}`} key={index} className="block p-4 rounded-lg border border-line bg-surface-sunk hover:bg-surface-sunk transition-colors">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-4">
                    <span className={`px-3 py-1 text-xs font-bold rounded-full ${result === 'Victory' ? 'bg-accent-soft text-accent-ink' : 'bg-danger-soft text-danger-ink'}`}>
                      {result}
                    </span>
                    <div>
                      <p className="font-bold text-ink">vs {item.opponentUsername}</p>
                      <p className="text-sm text-ink-muted">Used: {item.usedCrittersNames?.join(', ') ?? 'None'}</p>
                    </div>
                  </div>
                  <p className="text-sm text-ink-muted">{formatTimestamp(item.timestamp)}</p>
                </div>
              </Link>
            ) : null
          })
        )}
      </div>
    </div>
  );
}