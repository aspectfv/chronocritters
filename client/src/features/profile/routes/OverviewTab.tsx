import { TrainerInfo } from '@features/profile/components/overview/TrainerInfo';
import { BattleStatistics } from '@features/profile/components/overview/BattleStatistics';
import { CritterTeamOverview } from '@features/profile/components/overview/CritterTeamOverview';
import { useLoaderData } from 'react-router-dom';
import type { GetPlayerOverviewQuery } from 'src/gql/graphql';
export function OverviewTab() {
  const loaderData = useLoaderData() as GetPlayerOverviewQuery;
  const username = loaderData?.getPlayer?.username || 'Unknown Trainer';
  const roster = loaderData?.getPlayer?.roster || [];
  const wins = loaderData?.getPlayer?.stats?.wins ?? 0;
  const losses = loaderData?.getPlayer?.stats?.losses ?? 0;

  return (
    <div className="space-y-8">
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-1">
          <TrainerInfo username={username} />
        </div>
        <div className="lg:col-span-2">
          <BattleStatistics wins={wins} losses={losses} />
        </div>
      </div>
      <CritterTeamOverview roster={roster} />
    </div>
  );
}