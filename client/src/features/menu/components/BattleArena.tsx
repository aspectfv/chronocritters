import { useAuthStore } from '@store/auth/useAuthStore';
import { useLobbyStore } from '@store/lobby/useLobbyStore';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import type { MatchResponse } from '@features/menu/types';

export function BattleArena() {
  const [matchmakingStatus, setMatchmakingStatus] = useState<'idle' | 'searching' | 'found'>('idle');
  const navigate = useNavigate();
  const { isConnected, publish, subscribe } = useLobbyStore();
  const user = useAuthStore((state) => state.user);

  useEffect(() => {
    if (!isConnected || !user || !subscribe) return;

    const matchStatusSubscription = subscribe(`/user/${user.id}/matchmaking/status`, (match: MatchResponse) => {
      setMatchmakingStatus('found');
      
      navigate(`/battle/${match.battleId}`);
    });

    const matchErrorSubscription = subscribe(`/user/${user.id}/error`, (error: { message: string }) => {
      console.error('Matchmaking error:', error.message);
      setMatchmakingStatus('idle');
    });

    return () => {
      matchStatusSubscription?.unsubscribe();
      matchErrorSubscription?.unsubscribe();
    };
  }, [isConnected, user, subscribe, navigate]);

  const handleFindMatch = () => {
    if (isConnected) {
      setMatchmakingStatus('searching');
      publish('/app/matchmaking/join', {});
    }
  };

  const getButtonText = () => {
    if (!isConnected) return 'Connecting...';
    if (matchmakingStatus === 'searching') return 'Searching for Opponent...';
    return 'Find Match';
  };

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
        disabled={!isConnected || matchmakingStatus === 'searching'}
        className="w-full bg-green-600 hover:bg-green-700 text-white font-semibold py-3 px-4 rounded-lg transition-colors flex items-center justify-center gap-2 disabled:bg-gray-400 disabled:cursor-not-allowed"
      >
        <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
          <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM9.555 7.168A1 1 0 008 8v4a1 1 0 001.555.832l3-2a1 1 0 000-1.664l-3-2z" clipRule="evenodd" />
        </svg>
        {getButtonText()}
      </button>
    </div>
  );
}
