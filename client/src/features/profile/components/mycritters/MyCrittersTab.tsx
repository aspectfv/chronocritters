import { useState, useEffect } from 'react';
import { gql } from '@apollo/client';
import { useQuery } from '@apollo/client/react';
import { useAuthStore } from '@store/auth/useAuthStore';
import type { GetMyCrittersData, GetMyCrittersVars, CritterData } from '@features/profile/types';
import { CritterDetails, typeIcons } from './CritterDetails';

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
      <div className="md:col-span-1 bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h3 className="font-semibold text-green-800 mb-4">My Critters ({roster.length})</h3>
        <div className="space-y-3">
          {roster.map((critter) => (
            <div 
              key={critter.id} 
              onClick={() => setSelectedCritter(critter)}
              className={`p-4 rounded-lg border cursor-pointer transition-colors ${
                selectedCritter?.id === critter.id 
                  ? 'bg-green-50 border-green-400' 
                  : 'bg-gray-50 border-gray-200 hover:border-green-400'
              }`}
            >
              <p className="font-bold text-gray-800">
                {typeIcons[critter.type]} {critter.name}
              </p>
              <p className="text-sm text-gray-500">{critter.type}</p>
            </div>
          ))}
        </div>
      </div>

      <div className="md:col-span-2 bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <CritterDetails critter={selectedCritter} />
      </div>
    </div>
  );
}