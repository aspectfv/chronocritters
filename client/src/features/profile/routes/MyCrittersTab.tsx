import { useState, useEffect } from 'react';
import { gql } from '@apollo/client';
import { useQuery } from '@apollo/client/react';
import { useAuthStore } from '@store/auth/useAuthStore';
import type { GetMyCrittersData, GetMyCrittersVars, CritterData } from '@features/profile/types';
import { CritterDetails } from '@features/profile/components/mycritters/CritterDetails';
import { CritterList } from '@features/profile/components/mycritters/CritterList';

const GET_MY_CRITTERS = gql`
  query GetMyCritters($id: ID!) {
    getPlayer(id: $id) {
      id
      roster {
        id
        name
        type
        baseStats {
          health
          attack
          defense
        }
        abilities {
          id
          name
          power
          type
        }
      }
    }
  }
`;

export function MyCrittersTab() {
  const user = useAuthStore((state) => state.user);
  const [selectedCritter, setSelectedCritter] = useState<CritterData | null>(null);

  const { data, loading, error } = useQuery<GetMyCrittersData, GetMyCrittersVars>(GET_MY_CRITTERS, {
    variables: { 
      id: user!.id 
    },
    skip: !user,
  });

  useEffect(() => {
    const roster = data?.getPlayer?.roster;
    if (!selectedCritter && roster && roster.length > 0) {
      setSelectedCritter(roster[0]);
    }
  }, [data, selectedCritter]);

  const roster = data?.getPlayer?.roster || [];

  if (loading) return <p>Loading your critters...</p>;
  if (error) return <p>Error: Could not load your critters.</p>;

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
      <CritterList 
        roster={roster}
        selectedCritter={selectedCritter}
        onCritterSelect={setSelectedCritter}
      />

      <div className="md:col-span-2 bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <CritterDetails critter={selectedCritter} />
      </div>
    </div>
  );
}