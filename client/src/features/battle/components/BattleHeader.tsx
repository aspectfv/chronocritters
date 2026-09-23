import { Link } from 'react-router-dom';
import { useLobbyStore } from '@store/lobby/useLobbyStore';
import type { BattleHeaderProps } from '@features/battle/types';
import { getConnectionStatusStyle } from '@utils/utils';
import { BattleMusicControl } from '@features/battle/components/BattleMusicControl';

export function BattleHeader({ isPlayerTurn, onForfeit }: BattleHeaderProps) {
  const connectionStatus = useLobbyStore((state) => state.connectionStatus);

  return (
    <div className="relative text-center mb-6">
      <div className="absolute left-0 top-1/2 -translate-y-1/2">
        <Link to="/menu" className="bg-white text-gray-700 font-semibold py-2 px-4 rounded-lg shadow-sm border border-gray-200 hover:bg-gray-50 transition-colors flex items-center gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
          </svg>
          Back to Menu
        </Link>
      </div>

      <div className="absolute right-0 top-1/2 -translate-y-1/2 flex items-center gap-3">
        <BattleMusicControl />
        <button
          onClick={onForfeit}
          className="bg-white text-red-600 font-semibold py-2 px-4 rounded-lg shadow-sm border border-red-200 hover:bg-red-50 transition-colors"
        >
          Forfeit
        </button>
      </div>

      <div>
        <div className="flex items-center justify-center gap-2 text-sm text-green-700 mb-2">
          <span className={`w-2.5 h-2.5 rounded-full ${getConnectionStatusStyle(connectionStatus).color}`}></span>
          {getConnectionStatusStyle(connectionStatus).text}
        </div>
        <h1 className="text-4xl font-bold text-green-800">Battle Arena</h1>
        <div className="h-10 mt-4 flex items-center justify-center">
          {/* Keyed so React remounts the pill and the claim animation replays
              every time control changes hands. */}
          {isPlayerTurn ? (
            <div key="yours" className="animate-turn-claim inline-block bg-green-700 text-white text-sm font-bold px-4 py-2 rounded-full shadow-md">
              Your Turn
            </div>
          ) : (
            <div key="theirs" className="animate-turn-claim inline-block bg-gray-200 text-gray-600 text-sm font-semibold px-4 py-2 rounded-full">
              Waiting for opponent...
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
