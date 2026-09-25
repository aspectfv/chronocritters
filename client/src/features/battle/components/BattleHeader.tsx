import { Link } from 'react-router-dom';
import { ArrowLeft, ChevronsRight, Flag } from 'lucide-react';
import { useLobbyStore } from '@store/lobby/useLobbyStore';
import { useBattleStore } from '@store/battle/useBattleStore';
import type { BattleHeaderProps } from '@features/battle/types';
import { getConnectionStatusStyle } from '@utils/utils';
import { BattleMusicControl } from '@features/battle/components/BattleMusicControl';

/** A slim brass rail. The old centred 36px title ate a third of a phone screen. */
export function BattleHeader({ isPlayerTurn, onForfeit }: BattleHeaderProps) {
  const connectionStatus = useLobbyStore((state) => state.connectionStatus);
  const connection = getConnectionStatusStyle(connectionStatus);
  const opponentName = useBattleStore((state) => state.opponent.username);

  return (
    <div className="panel flex items-center gap-2 rounded-lg bg-arena-deep px-2 py-1.5">
      <Link
        to="/menu"
        className="flex items-center gap-1.5 rounded-control px-2 py-1.5 text-sm font-semibold text-arena-ink-muted transition-colors hover:bg-arena-glass hover:text-arena-ink focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brass"
      >
        <ArrowLeft className="h-4 w-4" aria-hidden="true" />
        <span className="hidden sm:inline">Menu</span>
      </Link>

      <span className="flex items-center gap-1.5 text-xs text-arena-ink-muted" title={connection.text}>
        <span className={`h-2 w-2 rounded-full transition-colors ${connection.color}`} aria-hidden="true" />
        <span className="hidden md:inline">{connection.text}</span>
      </span>

      {/* Turn state is the one thing that must be unmissable, so it owns the
          centre. Your turn is a lit plate you are meant to act on; theirs is a
          readout that says who is being waited for, with the dots carrying the
          waiting rather than a second pill that looks like a dead button. */}
      <div className="flex flex-1 justify-center">
        {isPlayerTurn ? (
          <span
            key="yours"
            className="animate-turn-claim flex items-center gap-1.5 rounded-full border-2 border-outline bg-brass px-4 py-1 text-sm font-black text-white shadow-[0_3px_0_0_var(--color-outline)]"
          >
            <ChevronsRight className="h-4 w-4" aria-hidden="true" />
            Your move
          </span>
        ) : (
          <span key="theirs" className="flex items-center gap-2 px-2 py-1 text-sm font-bold text-arena-ink-muted">
            <span className="truncate">{opponentName || 'Your opponent'} is choosing</span>
            <span className="flex items-end gap-0.5" aria-hidden="true">
              {[0, 1, 2].map((index) => (
                <span
                  key={index}
                  className="animate-waiting-dot h-1.5 w-1.5 rounded-full bg-brass"
                  style={{ animationDelay: `${index * 160}ms` }}
                />
              ))}
            </span>
          </span>
        )}
      </div>

      <BattleMusicControl />

      <button
        type="button"
        onClick={onForfeit}
        className="flex items-center gap-1.5 rounded-control px-2 py-1.5 text-sm font-semibold text-danger transition-colors hover:bg-danger/15 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-danger"
      >
        <Flag className="h-4 w-4" aria-hidden="true" />
        <span className="hidden sm:inline">Forfeit</span>
      </button>
    </div>
  );
}
