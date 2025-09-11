import { useAuthStore } from '@store/auth/useAuthStore';
import { useLobbyStore } from '@store/lobby/useLobbyStore';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { MatchMakingStatus, type MatchResponse } from '@features/menu/types';
import { ConnectionStatus } from '@store/lobby/types';
import { getButtonState } from '@utils/utils';

export function BattleArena() {
  const [matchmakingStatus, setMatchmakingStatus] = useState<MatchMakingStatus>(MatchMakingStatus.IDLE);
  const navigate = useNavigate();
  const { connectionStatus, publish, subscribe } = useLobbyStore();
  const user = useAuthStore((state) => state.user);

  useEffect(() => {
    if (connectionStatus !== ConnectionStatus.CONNECTED || !user || !subscribe) return;

    const matchStatusSubscription = subscribe(`/user/${user.id}/matchmaking/status`, (match: MatchResponse) => {
      setMatchmakingStatus(MatchMakingStatus.FOUND);
      navigate(`/battle/${match.battleId}`);
    });

    const matchErrorSubscription = subscribe(`/user/${user.id}/error`, (error: { message: string }) => {
      console.error('Matchmaking error:', error.message);
      setMatchmakingStatus(MatchMakingStatus.IDLE);
    });

    return () => {
      matchStatusSubscription?.unsubscribe();
      matchErrorSubscription?.unsubscribe();
    };
  }, [connectionStatus, user, subscribe, navigate]);

  const handleFindMatch = () => {
    if (connectionStatus) {
      setMatchmakingStatus(MatchMakingStatus.SEARCHING);
      publish('/app/matchmaking/join', {});
    }
  };

  const { text: buttonText, disabled: isButtonDisabled } = getButtonState(connectionStatus, matchmakingStatus);

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
      
      <button 
        onClick={handleFindMatch}
        disabled={isButtonDisabled}
        className="w-full bg-green-700 hover:bg-green-800 text-white font-semibold py-3 px-4 rounded-lg transition-colors flex items-center justify-center gap-2 disabled:bg-gray-400 disabled:cursor-not-allowed"
      >
        <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
          <path d="M8 7a2 2 0 100-4 2 2 0 000 4zM8 14a2 2 0 100-4 2 2 0 000 4zm4-7a2 2 0 100-4 2 2 0 000 4zm4 7a2 2 0 100-4 2 2 0 000 4z" />
        </svg>
        {buttonText}
      </button>

      {connectionStatus === ConnectionStatus.ERROR && (
        <p className="text-center text-red-600 text-xs mt-2">
          Could not connect to the matchmaking service. Please try again later.
        </p>
      )}
    </div>
  );
}