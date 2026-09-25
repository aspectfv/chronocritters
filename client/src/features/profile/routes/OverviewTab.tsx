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
  const level = loaderData?.getPlayer?.stats?.level ?? 0;
  const experience = loaderData?.getPlayer?.stats?.experience ?? 0;

  return (
    <div className="flex flex-col gap-3">
      <div className="grid grid-cols-1 gap-3 lg:grid-cols-[2fr_3fr]">
        <TrainerInfo username={username} level={level} experience={experience} />
        <BattleStatistics wins={wins} losses={losses} />
      </div>
      <CritterTeamOverview roster={roster} />
    </div>
  );
}