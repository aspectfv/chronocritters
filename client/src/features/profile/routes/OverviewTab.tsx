import { TrainerInfo } from '@features/profile/components/overview/TrainerInfo';
import { BattleStatistics } from '@features/profile/components/overview/BattleStatistics';
import { CritterTeamOverview } from '@features/profile/components/overview/CritterTeamOverview';
import { useLoaderData } from 'react-router-dom';
import type { GetPlayerOverviewData } from '../types';

export function OverviewTab() {
  const { username, stats, roster } = useLoaderData() as GetPlayerOverviewData['getPlayer'];

  return (
    <div className="space-y-8">
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-1">
          <TrainerInfo username={username} />
        </div>
        <div className="lg:col-span-2">
          <BattleStatistics wins={stats.wins} losses={stats.losses} />
        </div>
      </div>
      <CritterTeamOverview roster={roster} />
    </div>
  );
}