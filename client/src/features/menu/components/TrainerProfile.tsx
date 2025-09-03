import { Link } from 'react-router-dom';
import { gql } from '@apollo/client';
import { useQuery } from '@apollo/client/react';
import { useAuthStore } from '@store/auth/useAuthStore';
import type { GetPlayerStatsData, GetPlayerStatsVars } from "@features/menu/types";

const GET_PLAYER_STATS = gql`
  query GetPlayerStats($id: ID!) {
    getPlayer(id: $id) {
      stats {
        wins
        losses
      }
    }
  }
`;

export function TrainerProfile() {
  const user = useAuthStore((state) => state.user);

  const { data, loading, error } = useQuery<GetPlayerStatsData, GetPlayerStatsVars>(GET_PLAYER_STATS, {
    variables: {
      id: user!.id
    },
    // do not run the query until the user is authenticated and available
    skip: !user,
  });

  const isLoading = loading || !data;
  const hasError = !!error;
  const stats = data?.getPlayer?.stats;

  return (
    <div className="bg-green-50 border border-green-200 rounded-lg p-6">
      <div className="flex items-center gap-2 text-green-700 mb-4">
        <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
          <path fillRule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clipRule="evenodd" />
        </svg>
        <span className="font-semibold">Trainer Profile</span>
      </div>
      
      <p className="text-gray-700 mb-4">
        Manage your critter team and view your battle history.
      </p>
      
      <div className="flex gap-4 mb-6">
        <div className="bg-orange-100 text-orange-800 px-3 py-1 rounded-full text-sm font-medium">
          Wins: {isLoading ? '...' : hasError ? '!' : stats?.wins ?? 0}
        </div>
        <div className="text-gray-600 text-sm font-medium">
          Losses: {isLoading ? '...' : hasError ? '!' : stats?.losses ?? 0}
        </div>
      </div>
      
      <Link 
        to="/profile"
        className="w-full bg-white hover:bg-gray-50 text-green-700 font-semibold py-3 px-4 rounded-lg border border-green-200 transition-colors flex items-center justify-center gap-2"
      >
        <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
          <path fillRule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clipRule="evenodd" />
        </svg>
        View Profile
      </Link>
    </div>
  );
}