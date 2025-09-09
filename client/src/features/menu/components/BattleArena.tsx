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
  }, [connectionStatus , user, subscribe, navigate]);

  const handleFindMatch = () => {
    if (connectionStatus ) {
      setMatchmakingStatus(MatchMakingStatus.SEARCHING);
      publish('/app/matchmaking/join', {});
    }
  };

  

  const { text: buttonText, disabled: isButtonDisabled } = getButtonState(connectionStatus, matchmakingStatus);

  return (
    <div className="bg-green-50 border border-green-200 rounded-lg p-6">
      <div className="flex items-center gap-2 text-green-700 mb-4">
        <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
          <path fillRule="evenodd" d="M12.316 3.051a1 1 0 01.633 1.265l-4 12a1 1 0 11-1.898-.632l4-12a1 1 0 011.265-.633zM5.707 6.293a1 1 0 010 1.414L3.414 10l2.293 2.293a1 1 0 11-1.414 1.414l-3-3a1 1 0 010-1.414l3-3a1 1 0 011.414 0zm8.586 0a1 1 0 011.414 0l3 3a1 1 0 010 1.414l-3 3a1 1 0 11-1.414-1.414L16.586 10l-2.293-2.293a1 1 0 010-1.414z" clipRule="evenodd" />
        </svg>
        <span className="font-semibold">Battle Arena</span>
      </div>
      
      <p className="text-gray-700 mb-6">
        Challenge other trainers in epic critter battles!
      </p>
      
      <button 
        onClick={handleFindMatch}
        disabled={isButtonDisabled}
        className="w-full bg-green-600 hover:bg-green-700 text-white font-semibold py-3 px-4 rounded-lg transition-colors flex items-center justify-center gap-2 disabled:bg-gray-400 disabled:cursor-not-allowed"
      >
        <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
          <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM9.555 7.168A1 1 0 008 8v4a1 1 0 001.555.832l3-2a1 1 0 000-1.664l-3-2z" clipRule="evenodd" />
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
