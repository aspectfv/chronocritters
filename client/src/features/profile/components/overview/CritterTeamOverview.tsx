import { gql } from '@apollo/client';
import { useQuery } from '@apollo/client/react';
import { useAuthStore } from '@store/auth/useAuthStore';
import { CritterCard } from './CritterCard';
import type { GetCritterTeamData, GetCritterTeamVars } from '@features/profile/types';

const GET_CRITTER_TEAM_OVERVIEW = gql`
  query GetCritterTeamOverview($id: ID!) {
    getPlayer(id: $id) {
      id
      roster {
        name
        type
      }
    }
  }
`;

export function CritterTeamOverview() {
  const user = useAuthStore((state) => state.user);
  const { data, loading, error } = useQuery<GetCritterTeamData, GetCritterTeamVars>(GET_CRITTER_TEAM_OVERVIEW, {
    variables: { id: user!.id },
    skip: !user,
  });

  if (loading) return <p>Loading team...</p>;
  if (error) return <p>Error loading team.</p>;

  const roster = data?.getPlayer?.roster || [];

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <h3 className="font-semibold text-green-800 mb-4">Critter Team Overview</h3>
      <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
        {roster.map((critter) => (
          // Level is mocked as it's not in the GraphQL schema yet
          <CritterCard key={critter.name} name={critter.name} type={critter.type} level={1} />
        ))}
      </div>
    </div>
  );
}