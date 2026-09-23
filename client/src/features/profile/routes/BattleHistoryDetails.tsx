import type { GetBattleHistoryEntryQuery } from '@/gql/graphql';
import { useAuthStore } from '@store/auth/useAuthStore';
import { formatDuration, formatTimestamp } from '@utils/utils';
import { Link, useLoaderData } from 'react-router-dom';

const ActionItem = ({ turn, actor, description }: { turn: number, actor: string, description: string }) => (
    <div className="flex items-stretch gap-4">
        <div className="w-16 flex items-center justify-center">
            <span className="text-sm font-medium text-ink-muted">Turn {turn}</span>
        </div>
        <div className="flex-1 bg-surface p-3 rounded-lg border border-line">
            <p className="font-bold text-ink mb-1">{actor}</p>
            <p className="text-sm text-ink-muted">{description}</p>
        </div>
    </div>
);

export function BattleHistoryDetails() {
    const user = useAuthStore((store) => store.user);
    const loaderData = useLoaderData() as GetBattleHistoryEntryQuery;
    const battle = loaderData?.getMatchHistoryEntry;

    if (!battle) {
        return (
            <div className="bg-surface p-6 rounded-lg shadow-sm border border-line text-center space-y-4">
                <p className="text-ink-muted">This battle could not be found.</p>
                <Link to="/profile/history" className="inline-block text-sm bg-surface-sunk px-3 py-2 rounded-lg hover:bg-line font-semibold text-ink">
                    &larr; Back to History
                </Link>
            </div>
        );
    }

    const isVictory = battle.winnerId === user?.id;
    const actorName = (playerId: string | null | undefined) =>
        playerId === user?.id ? (user?.username ?? 'You') : (battle.opponentUsername ?? 'Opponent');

    return (
        <div className="space-y-6">
            <div className="bg-surface p-4 sm:p-6 rounded-lg shadow-sm border border-line">
                <div className="mb-6 flex flex-wrap items-center justify-between gap-3">
                    <h2 className="text-xl font-bold text-accent-ink">
                        Battle Details: vs {battle.opponentUsername}
                    </h2>
                    <Link to="/profile/history" className="text-sm bg-surface-sunk px-3 py-2 rounded-lg hover:bg-line font-semibold text-ink flex items-center gap-1">
                        &larr; Back to History
                    </Link>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    <div className="space-y-4">
                        <div className={`text-lg font-bold text-center py-2 px-4 rounded-lg ${isVictory ? 'bg-accent-soft text-accent-ink' : 'bg-danger-soft text-danger-ink'}`}>
                            {isVictory ? 'Victory' : 'Defeat'}
                        </div>
                        <div>
                            <p className="text-sm font-semibold text-ink-muted">Date</p>
                            <p className="font-medium text-ink">{formatTimestamp(battle.timestamp)}</p>
                        </div>
                        <div>
                            <p className="text-sm font-semibold text-ink-muted">Duration</p>
                            <p className="font-medium text-ink flex items-center gap-1">
                                <svg className="h-4 w-4 text-ink-faint" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                                {formatDuration(battle.duration ?? 0)}
                            </p>
                        </div>
                        <div>
                            <p className="text-sm font-semibold text-ink-muted">Total Turns</p>
                            <p className="font-medium text-ink">{battle.turnCount}</p>
                        </div>
                    </div>
                    <div className="space-y-3">
                         <h3 className="text-base font-bold text-accent-ink">Critters</h3>
                         <div className="bg-accent-soft p-3 rounded-lg border border-accent/25">
                             <p className="text-sm font-semibold">Your Critters</p>
                             <p>{battle.usedCrittersNames?.join(', ') ?? 'None'}</p>
                         </div>
                         <div className="bg-surface-sunk p-3 rounded-lg border border-line">
                             <p className="text-sm font-semibold">Opponent's Critters</p>
                             <p>{battle.opponentCrittersNames?.join(', ') ?? 'None'}</p>
                         </div>
                    </div>
                    <div className="space-y-3">
                        <h3 className="text-base font-bold text-accent-ink">Damage Summary</h3>
                        <div className="bg-accent-soft p-4 rounded-lg border border-accent/25">
                            <p className="text-sm font-semibold text-accent-ink">Damage Dealt</p>
                            <p className="text-3xl font-bold text-accent-ink">{battle.damageDealt}</p>
                        </div>
                         <div className="bg-danger-soft p-4 rounded-lg border border-danger/25">
                            <p className="text-sm font-semibold text-danger-ink">Damage Received</p>
                            <p className="text-3xl font-bold text-danger-ink">{battle.damageReceived}</p>
                        </div>
                    </div>
                </div>
            </div>

            <div className="bg-surface-sunk p-4 sm:p-6 rounded-lg shadow-sm border border-line">
                <h3 className="text-xl font-bold text-accent-ink mb-4">Turn-by-Turn Actions</h3>
                <div className="space-y-3">
                    {battle.turnActionHistory?.map((item, index) => (
                        <ActionItem
                            key={index}
                            turn={item?.turn ?? 0}
                            actor={actorName(item?.playerId)}
                            description={item?.turnActionLog ?? ''}
                        />
                    ))}
                </div>
            </div>
        </div>
    );
}
