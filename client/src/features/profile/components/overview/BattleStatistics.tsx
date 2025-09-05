import { gql } from '@apollo/client';
import { useQuery } from '@apollo/client/react';
import { useAuthStore } from '@store/auth/useAuthStore';
import type { GetBattleStatsData, GetBattleStatsVars } from '@features/profile/types';

const GET_BATTLE_STATS = gql`
  query GetBattleStats($id: ID!) {
    getPlayer(id: $id) {
      id
      stats {
        wins
        losses
      }
    }
  }
`;

export function BattleStatistics() {
  const user = useAuthStore((state) => state.user);
  const { data, loading, error } = useQuery<GetBattleStatsData, GetBattleStatsVars>(GET_BATTLE_STATS, {
    variables: { id: user!.id },
    skip: !user,
  });

  const stats = data?.getPlayer?.stats;
  const wins = loading || error || !stats ? 0 : stats.wins;
  const losses = loading || error || !stats ? 0 : stats.losses;
  const totalBattles = wins + losses;
  const winRate = totalBattles > 0 ? Math.round((wins / totalBattles) * 100) : 0;

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 h-full">
      <h3 className="font-semibold text-green-800 mb-6 flex items-center gap-2">
        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor"><path d="M2 10a8 8 0 018-8v8h8a8 8 0 11-16 0z" /><path d="M12 2.252A8.014 8.014 0 0117.748 8H12V2.252z" /></svg>
        Battle Statistics
      </h3>
      <div className="grid grid-cols-2 gap-8 mb-6">
        <div className="text-center">
          <p className="text-4xl font-bold text-green-600">{loading ? '...' : wins}</p>
          <p className="text-sm text-gray-500">Wins</p>
        </div>
        <div className="text-center">
          <p className="text-4xl font-bold text-red-500">{loading ? '...' : losses}</p>
          <p className="text-sm text-gray-500">Losses</p>
        </div>
      </div>
      <div className="space-y-4 text-sm">
        <div className="flex justify-between"><span className="text-gray-500">Total Battles</span><span className="font-bold">{loading ? '...' : totalBattles}</span></div>
        <div className="flex justify-between"><span className="text-gray-500">Win Rate</span><span className="font-bold">{loading ? '...' : `${winRate}%`}</span></div>
        <div>
          <p className="text-gray-500 mb-1">Win Rate Progress</p>
          <div className="w-full bg-green-100 rounded-full h-2.5">
            <div className="bg-gradient-to-r from-green-500 to-yellow-400 h-2.5 rounded-full" style={{ width: `${winRate}%` }}></div>
          </div>
        </div>
      </div>
    </div>
  );
}