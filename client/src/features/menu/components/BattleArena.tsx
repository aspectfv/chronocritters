import { useAuthStore } from '@store/auth/useAuthStore';
import { useLobbyStore } from '@store/lobby/useLobbyStore';
import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { MatchMakingStatus, type MatchResponse } from '@features/menu/types';
import { Swords } from 'lucide-react';
import { Surface } from '@components/ui/Surface';
import { Button } from '@components/ui/Button';
import { ConnectionStatus } from '@store/lobby/types';

export function BattleArena() {
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
    <Surface className="flex h-full flex-col">
      <div className="mb-4 flex items-center gap-2 text-ink">
        <Swords className="h-5 w-5" aria-hidden="true" />
        <span className="font-semibold">Battle Arena</span>
      </div>

      <p className="mb-6 text-ink-muted">
        Challenge other trainers in epic critter battles!
      </p>

      <div className="mt-auto">
      {isSearching ? (
        <div className="space-y-3">
          <div
            className="flex w-full items-center justify-center gap-2 rounded-control bg-surface-sunk px-4 py-3 font-semibold text-ink-muted"
            role="status"
            aria-live="polite"
          >
            <span className="h-4 w-4 animate-spin rounded-full border-2 border-accent border-t-transparent" aria-hidden="true"></span>
            Searching for an opponent...
          </div>
          <Button variant="secondary" block onClick={handleCancelSearch}>
            Cancel Search
          </Button>
        </div>
      ) : (
        <Button block onClick={handleFindMatch} disabled={!isConnected}>
          {isConnected ? 'Find Match' : 'Connecting to Lobby...'}
        </Button>
      )}
      </div>

      {error && (
        <p className="mt-2 text-center text-sm text-danger-ink" role="alert">{error}</p>
      )}

      {connectionStatus === ConnectionStatus.ERROR && (
        <p className="mt-2 text-center text-sm text-danger-ink" role="alert">
          Could not connect to the matchmaking service. Please try again later.
        </p>
      )}
    </Surface>
  );
}
