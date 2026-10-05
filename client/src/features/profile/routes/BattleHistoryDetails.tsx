import type { GetBattleHistoryEntryQuery } from '@/gql/graphql';
import { ArrowLeft } from 'lucide-react';
import { useAuthStore } from '@store/auth/useAuthStore';
import { formatDuration, formatTimestamp } from '@utils/utils';
import { Link, useLoaderData } from 'react-router-dom';

const BackToHistory = () => (
  <Link
    to="/profile/history"
    className="key inline-flex items-center gap-1.5 rounded-lg bg-arena-deep px-3 py-2 text-sm font-black text-arena-ink focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-brass/60"
  >
    <ArrowLeft className="h-4 w-4" aria-hidden="true" />
    All battles
  </Link>
);

const Figure = ({ value, label, tone }: { value: string | number; label: string; tone: string }) => (
  <div className="plaque px-3 py-2.5 text-center">
    <p className={`numeral text-2xl ${tone}`}>{value}</p>
    <p className="text-xs font-bold text-arena-ink-muted">{label}</p>
  </div>
);

export function BattleHistoryDetails() {
  const user = useAuthStore((store) => store.user);
  const loaderData = useLoaderData() as GetBattleHistoryEntryQuery;
  const battle = loaderData?.getMatchHistoryEntry;

  if (!battle) {
    return (
      <div className="panel rounded-lg bg-arena-deep p-6 text-center">
        <p className="display text-lg text-arena-ink">That battle is not on record</p>
        <p className="mt-1 text-sm text-arena-ink-muted">The link may be out of date.</p>
        <div className="mt-4 flex justify-center">
          <BackToHistory />
        </div>
      </div>
    );
  }

  const isVictory = battle.winnerId === user?.id;
  const actorName = (playerId: string | null | undefined) =>
    playerId === user?.id ? (user?.username ?? 'You') : (battle.opponentUsername ?? 'Opponent');

  return (
    <div className="flex flex-col gap-3">
      <div className="panel rounded-lg bg-arena-deep p-4">
        <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
          <div className="flex items-center gap-2.5">
            <span className={`rounded-full border-2 border-outline px-3 py-1 text-xs font-black text-white ${isVictory ? 'bg-vital' : 'bg-ruby'}`}>
              {isVictory ? 'Won' : 'Lost'}
            </span>
            <h2 className="display text-xl text-arena-ink">vs {battle.opponentUsername}</h2>
          </div>
          <BackToHistory />
        </div>

        <dl className="grid grid-cols-2 gap-2.5 sm:grid-cols-4">
          <Figure value={formatDuration(battle.duration ?? 0)} label="Duration" tone="text-arena-ink" />
          <Figure value={battle.turnCount ?? 0} label="Turns" tone="text-arena-ink" />
          <Figure value={battle.damageDealt ?? 0} label="Damage dealt" tone="text-vital" />
          <Figure value={battle.damageReceived ?? 0} label="Damage taken" tone="text-ruby" />
        </dl>

        <p className="mt-3 text-xs text-arena-ink-muted">{formatTimestamp(battle.timestamp)}</p>

        <div className="mt-4 grid grid-cols-1 gap-2.5 sm:grid-cols-2">
          <div className="well rounded-sm px-3 py-2.5">
            <p className="text-xs font-bold text-arena-ink">You sent out</p>
            <p className="mt-0.5 text-sm text-arena-ink-muted">
              {battle.usedCrittersNames?.length ? battle.usedCrittersNames.join(', ') : 'No critters recorded'}
            </p>
          </div>
          <div className="well rounded-sm px-3 py-2.5">
            <p className="text-xs font-bold text-arena-ink">They sent out</p>
            <p className="mt-0.5 text-sm text-arena-ink-muted">
              {battle.opponentCrittersNames?.length ? battle.opponentCrittersNames.join(', ') : 'No critters recorded'}
            </p>
          </div>
        </div>
      </div>

      <div className="panel rounded-lg bg-arena-deep p-4">
        <h3 className="mb-3 text-sm font-bold text-arena-ink">How the battle ran</h3>

        {battle.turnActionHistory?.length ? (
          <ol className="flex flex-col gap-2">
            {battle.turnActionHistory.map((item, index) => (
              <li key={index} className="flex items-stretch gap-2.5">
                <span className="plaque flex w-14 shrink-0 items-center justify-center px-2">
                  <span className="numeral text-sm text-brass-ink">{item?.turn ?? 0}</span>
                </span>
                <span className="well flex-1 rounded-sm px-3 py-2">
                  <span className="block text-sm font-bold text-arena-ink">{actorName(item?.playerId)}</span>
                  <span className="block text-sm text-arena-ink-muted">{item?.turnActionLog ?? ''}</span>
                </span>
              </li>
            ))}
          </ol>
        ) : (
          <p className="well rounded-sm px-3 py-2 text-sm text-arena-ink-muted">
            No turns were recorded for this battle.
          </p>
        )}
      </div>
    </div>
  );
}
