import { useState, useEffect } from 'react';
import { gql } from '@apollo/client';
import { useQuery } from '@apollo/client/react';
import { useAuthStore } from '@store/auth/useAuthStore';
import type { CritterType } from '@store/battle/types';
import type { GetMyCrittersData, GetMyCrittersVars, CritterData } from '@features/profile/types';

// Define the GraphQL query to fetch the player's full roster details
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

const typeIcons: Record<CritterType, string> = {
  FIRE: '🔥',
  WATER: '💧',
  ELECTRIC: '⚡',
  GRASS: '🌍',
  UNKNOWN: '❓',
};

// Sub-component for displaying details of a selected critter
const CritterDetails = ({ critter }: { critter: CritterData | null }) => {
  // Gracefully handle the case where no critter is selected
  if (!critter) {
    return (
      <div className="flex flex-col h-full items-center justify-center text-center">
        <div className="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-12 w-12 text-gray-400" viewBox="0 0 20 20" fill="currentColor"><path fillRule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clipRule="evenodd" /></svg>
        </div>
        <h3 className="font-semibold text-gray-800 text-xl">Select a Critter</h3>
        <p className="text-gray-500">Select a critter from the list to view its details.</p>
      </div>
    );
  }

  return (
    <div className="text-left">
      <div className="text-center mb-6">
        <div className="w-24 h-24 bg-green-100 rounded-full mx-auto flex items-center justify-center mb-2">
          <span className="text-4xl">
            {critter.type === 'FIRE' ? '🔥' : critter.type === 'WATER' ? '💧' : critter.type === 'ELECTRIC' ? '⚡' : '🌍'}
          </span>
        </div>
        <h3 className="font-bold text-2xl text-gray-800">{critter.name}</h3>
        <p className="text-sm text-gray-500">{critter.type}</p>
      </div>

      <h4 className="font-semibold text-green-800 mb-2">Base Stats</h4>
      <div className="grid grid-cols-3 gap-4 text-center bg-gray-50 p-4 rounded-lg mb-6">
        <div>
          <p className="font-bold text-lg">{critter.baseStats.health}</p>
          <p className="text-xs text-gray-500">Health</p>
        </div>
        <div>
          <p className="font-bold text-lg">{critter.baseStats.attack}</p>
          <p className="text-xs text-gray-500">Attack</p>
        </div>
        <div>
          <p className="font-bold text-lg">{critter.baseStats.defense}</p>
          <p className="text-xs text-gray-500">Defense</p>
        </div>
      </div>

      <h4 className="font-semibold text-green-800 mb-2">Abilities</h4>
      <div className="space-y-2">
        {critter.abilities.map(ability => (
          <div key={ability.id} className="p-3 bg-gray-50 rounded-lg border border-gray-200">
            <p className="font-semibold text-sm text-gray-700">{ability.name} <span className="float-right text-xs bg-green-100 text-green-800 px-2 py-0.5 rounded-full">{ability.type}</span></p>
            <p className="text-xs text-gray-500">Power: {ability.power}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

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