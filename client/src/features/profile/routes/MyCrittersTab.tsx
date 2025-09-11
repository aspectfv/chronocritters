import { useState, useEffect } from 'react';
import { useLoaderData } from 'react-router-dom';
import type { CritterData } from '@features/profile/types';
import { CritterDetails } from '@features/profile/components/mycritters/CritterDetails';
import { CritterList } from '@features/profile/components/mycritters/CritterList';
import type { GetPlayerOverviewQuery } from '@/gql/graphql';

export function MyCrittersTab() {
  const loaderData = useLoaderData() as GetPlayerOverviewQuery;
  const [selectedCritter, setSelectedCritter] = useState<CritterData | null>(null);

  const roster = (loaderData?.getPlayer?.roster?.filter((c): c is CritterData => c !== null) || []) as CritterData[];

  useEffect(() => {
    if (!selectedCritter && roster && roster.length > 0) {
      setSelectedCritter(roster[0]);
    }
  }, [roster, selectedCritter]);

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