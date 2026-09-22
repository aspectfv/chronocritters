import { useAuthStore } from '@store/auth/useAuthStore';
import { useLobbyStore } from '@store/lobby/useLobbyStore';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { MatchMakingStatus, type MatchResponse } from '@features/menu/types';
import { ConnectionStatus } from '@store/lobby/types';

export function BattleArena() {
  const [matchmakingStatus, setMatchmakingStatus] = useState<MatchMakingStatus>(MatchMakingStatus.IDLE);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
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
    <div className="bg-white border border-gray-200 rounded-xl p-6 shadow-sm">
      <div className="flex items-center gap-2 text-gray-700 mb-4">
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6.253v11.494m-5.747-5.747h11.494" />
        </svg>
        <span className="font-semibold">Battle Arena</span>
      </div>

      <p className="text-gray-600 mb-6">
        Challenge other trainers in epic critter battles!
      </p>

      {isSearching ? (
        <div className="space-y-3">
          <div className="w-full bg-gray-100 text-gray-700 font-semibold py-3 px-4 rounded-lg flex items-center justify-center gap-2">
            <span className="w-4 h-4 border-2 border-green-600 border-t-transparent rounded-full animate-spin"></span>
            Searching for an opponent...
          </div>
          <button
            onClick={handleCancelSearch}
            className="w-full bg-white hover:bg-gray-50 text-gray-700 font-semibold py-3 px-4 rounded-lg border border-gray-300 transition-colors"
          >
            Cancel Search
          </button>
        </div>
      ) : (
        <button
          onClick={handleFindMatch}
          disabled={!isConnected}
          className="w-full bg-green-700 hover:bg-green-800 text-white font-semibold py-3 px-4 rounded-lg transition-colors flex items-center justify-center gap-2 disabled:bg-gray-400 disabled:cursor-not-allowed"
        >
          {isConnected ? 'Find Match' : 'Connecting to Lobby...'}
        </button>
      )}

      {error && (
        <p className="text-center text-red-600 text-xs mt-2">{error}</p>
      )}

      {connectionStatus === ConnectionStatus.ERROR && (
        <p className="text-center text-red-600 text-xs mt-2">
          Could not connect to the matchmaking service. Please try again later.
        </p>
      )}
    </div>
  );
}
