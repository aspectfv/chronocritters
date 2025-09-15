import type { GetBattleHistoryQuery } from '@/gql/graphql';
import { useAuthStore } from '@store/auth/useAuthStore';
import { formatTimestamp } from '@utils/utils';
import { Link, useLoaderData } from 'react-router-dom';

export function BattleHistoryTab() {
  const user = useAuthStore((store) => store.user);
  const loaderData = useLoaderData() as GetBattleHistoryQuery;
  const battleHistory = loaderData?.getPlayer?.matchHistory || [];
  
  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <h3 className="font-semibold text-green-800 mb-4">Recent Battle History</h3>
      <div className="space-y-3">
        {battleHistory.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-12 text-gray-400">
            <svg className="w-14 h-14 mb-4" viewBox="0 0 64 64" fill="none">
              <circle cx="32" cy="32" r="30" stroke="#A3A3A3" strokeWidth="4" fill="#F3F4F6" />
              <path d="M20 44c0-6 8-10 12-10s12 4 12 10" stroke="#A3A3A3" strokeWidth="3" strokeLinecap="round" />
              <circle cx="24" cy="28" r="3" fill="#A3A3A3" />
              <circle cx="40" cy="28" r="3" fill="#A3A3A3" />
              <path d="M28 36c1.5 2 6.5 2 8 0" stroke="#A3A3A3" strokeWidth="2" strokeLinecap="round" />
            </svg>
            <span className="text-lg font-semibold">No battles found</span>
            <span className="text-sm mt-2">Start a match to see your battle history here!</span>
          </div>
        ) : (
          battleHistory.map((item, index) => {
            const result = (item?.winnerId ?? '') === (user?.id ?? '') ? 'Victory' : 'Defeat';
            return item ? (
              <Link to={`/profile/history/${item.battleId}`} key={index} className="block p-4 rounded-lg border border-gray-200 bg-gray-50 hover:bg-gray-100 transition-colors">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-4">
                    <span className={`px-3 py-1 text-xs font-bold rounded-full ${result === 'Victory' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                      {result}
                    </span>
                    <div>
                      <p className="font-bold text-gray-800">vs {item.opponentUsername}</p>
                      <p className="text-sm text-gray-500">Used: {item.crittersUsed?.join(', ') ?? 'None'}</p>
                    </div>
                  </div>
                  <p className="text-sm text-gray-500">{formatTimestamp(item.timestamp)}</p>
                </div>
              </Link>
            ) : null
          })
        )}
      </div>
    </div>
  );
}