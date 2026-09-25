import { Link } from 'react-router-dom';
import { ArrowLeft, Flag } from 'lucide-react';
import { useLobbyStore } from '@store/lobby/useLobbyStore';
import type { BattleHeaderProps } from '@features/battle/types';
import { getConnectionStatusStyle } from '@utils/utils';
import { BattleMusicControl } from '@features/battle/components/BattleMusicControl';

/** A slim brass rail. The old centred 36px title ate a third of a phone screen. */
export function BattleHeader({ isPlayerTurn, onForfeit }: BattleHeaderProps) {
  const connectionStatus = useLobbyStore((state) => state.connectionStatus);
  const connection = getConnectionStatusStyle(connectionStatus);

  return (
    <div className="flex items-center gap-2 rounded-lg border border-brass/25 bg-arena-deep px-2 py-1.5 shadow-card">
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

      {/* Turn state is the one thing that must be unmissable, so it owns the centre. */}
      <div className="flex flex-1 justify-center">
        {isPlayerTurn ? (
          <span key="yours" className="animate-turn-claim rounded-full bg-brass px-4 py-1 text-sm font-bold text-white shadow-raised">
            Your Turn
          </span>
        ) : (
          <span key="theirs" className="animate-turn-claim rounded-full border border-brass/25 px-4 py-1 text-sm font-semibold text-arena-ink-muted">
            Opponent&rsquo;s turn
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
