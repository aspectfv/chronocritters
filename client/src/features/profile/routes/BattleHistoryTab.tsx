import type { GetBattleHistoryQuery } from '@/gql/graphql';
import { useAuthStore } from '@store/auth/useAuthStore';
import { formatTimestamp } from '@utils/utils';
import { Link, useLoaderData } from 'react-router-dom';

export function BattleHistoryTab() {
  const user = useAuthStore((store) => store.user);
  const loaderData = useLoaderData() as GetBattleHistoryQuery;
  const battleHistory = loaderData?.getPlayer?.matchHistory || [];

  return (
    <div className="panel rounded-lg bg-arena-deep p-4">
      <h2 className="mb-3 text-sm font-bold text-arena-ink">Your battles</h2>

      {battleHistory.length === 0 ? (
        <div className="well rounded-sm px-4 py-10 text-center">
          <p className="display text-lg text-arena-ink">No battles yet</p>
          <p className="mt-1 text-sm text-arena-ink-muted">Win or lose, every match you play is recorded here.</p>
          <Link
            to="/menu?queue=1"
            className="key mt-4 inline-flex rounded-lg bg-brass px-5 py-2.5 text-sm font-black text-white focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-brass/60"
          >
            Find a match
          </Link>
        </div>
      ) : (
        <div className="flex flex-col gap-2.5">
          {battleHistory.map((item, index) => {
            if (!item) return null;
            const isVictory = (item.winnerId ?? '') === (user?.id ?? '');

            return (
              <Link
                key={index}
                to={`/profile/history/${item.battleId}`}
                className="key flex flex-wrap items-center gap-3 rounded-lg bg-arena-deep p-3 focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-brass/60"
              >
                <span className={`shrink-0 rounded-full border-2 border-outline px-2.5 py-0.5 text-[11px] font-black text-white ${isVictory ? 'bg-vital' : 'bg-ruby'}`}>
                  {isVictory ? 'Won' : 'Lost'}
                </span>

                <span className="min-w-0 flex-1">
                  <span className="block truncate font-bold text-arena-ink">vs {item.opponentUsername}</span>
                  <span className="block truncate text-xs text-arena-ink-muted">
                    {item.usedCrittersNames?.length ? item.usedCrittersNames.join(', ') : 'No critters recorded'}
                  </span>
                </span>

                <span className="shrink-0 text-xs text-arena-ink-muted">{formatTimestamp(item.timestamp)}</span>
              </Link>
            );
          })}
        </div>
      )}
    </div>
  );
}
