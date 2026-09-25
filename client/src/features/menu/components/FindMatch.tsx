import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { Swords } from 'lucide-react';
import { useAuthStore } from '@store/auth/useAuthStore';
import { useLobbyStore } from '@store/lobby/useLobbyStore';
import { ConnectionStatus } from '@store/lobby/types';
import { MatchMakingStatus, type MatchResponse } from '@features/menu/types';

/**
 * The one thing the menu exists for, sized accordingly.
 *
 * It was a button inside a card the same size as the two cards beside it, so
 * nothing on the page said what to do first.
 */
export function FindMatch() {
  const [matchmakingStatus, setMatchmakingStatus] = useState<MatchMakingStatus>(MatchMakingStatus.IDLE);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const { connectionStatus, publish, subscribe } = useLobbyStore();
  const user = useAuthStore((state) => state.user);

  const isConnected = connectionStatus === ConnectionStatus.CONNECTED;
  const isSearching = matchmakingStatus === MatchMakingStatus.SEARCHING;

  useEffect(() => {
    if (!isConnected || !user) return;

    const matchStatusSubscription = subscribe(`/user/${user.id}/matchmaking/status`, (match: MatchResponse) => {
      setMatchmakingStatus(MatchMakingStatus.FOUND);
      navigate(`/battle/${match.battleId}`);
    });

    const matchErrorSubscription = subscribe(`/user/${user.id}/error`, (payload: { message: string }) => {
      setError(payload.message ?? 'Matchmaking failed. Please try again.');
      setMatchmakingStatus(MatchMakingStatus.IDLE);
    });

    return () => {
      matchStatusSubscription?.unsubscribe();
      matchErrorSubscription?.unsubscribe();
    };
  }, [isConnected, user, subscribe, navigate]);

  // Losing the connection while queued means the server has already dropped us.
  useEffect(() => {
    if (!isConnected) {
      setMatchmakingStatus(MatchMakingStatus.IDLE);
    }
  }, [isConnected]);

  // Arriving from the results screen's "Battle again" queues straight away.
  useEffect(() => {
    if (searchParams.get('queue') !== '1' || !isConnected || isSearching) return;
    setSearchParams({}, { replace: true });
    setError(null);
    setMatchmakingStatus(MatchMakingStatus.SEARCHING);
    publish('/app/matchmaking/join', {});
  }, [searchParams, isConnected, isSearching, setSearchParams, publish]);

  const handleFindMatch = () => {
    if (!isConnected) return;
    setError(null);
    setMatchmakingStatus(MatchMakingStatus.SEARCHING);
    publish('/app/matchmaking/join', {});
  };

  const handleCancelSearch = () => {
    if (!isConnected) return;
    publish('/app/matchmaking/leave', {});
    setMatchmakingStatus(MatchMakingStatus.IDLE);
  };

  return (
    <div className="flex flex-col gap-2">
      {isSearching ? (
        <>
          <div
            className="panel flex items-center justify-center gap-3 rounded-lg bg-arena-deep px-4 py-4 text-lg font-black text-arena-ink"
            role="status"
            aria-live="polite"
          >
            <span className="h-5 w-5 animate-spin rounded-full border-[3px] border-outline border-t-transparent" aria-hidden="true" />
            Looking for an opponent
          </div>
          <button
            type="button"
            onClick={handleCancelSearch}
            className="key rounded-lg bg-arena-deep px-4 py-2.5 text-sm font-black text-arena-ink focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-brass/60"
          >
            Stop looking
          </button>
        </>
      ) : (
        <button
          type="button"
          onClick={handleFindMatch}
          disabled={!isConnected}
          className="key flex items-center justify-center gap-3 rounded-lg bg-brass px-4 py-5 text-xl font-black tracking-tight text-white focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-brass/60"
        >
          <Swords className="h-6 w-6" aria-hidden="true" />
          {isConnected ? 'Find a match' : 'Connecting to the lobby'}
        </button>
      )}

      {error && (
        <p className="rounded-sm border-2 border-outline bg-ruby px-3 py-2 text-center text-sm font-bold text-white" role="alert">
          {error}
        </p>
      )}

      {connectionStatus === ConnectionStatus.ERROR && (
        <p className="rounded-sm border-2 border-outline bg-ruby px-3 py-2 text-center text-sm font-bold text-white" role="alert">
          Matchmaking is unreachable. Check your connection and try again.
        </p>
      )}
    </div>
  );
}
