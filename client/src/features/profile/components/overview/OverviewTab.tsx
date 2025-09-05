import { TrainerInfo } from './TrainerInfo';
import { BattleStatistics } from './BattleStatistics';
import { CritterTeamOverview } from './CritterTeamOverview';

export function OverviewTab() {
  return (
    <div className="space-y-8">
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-1">
          <TrainerInfo />
        </div>
        <div className="lg:col-span-2">
          <BattleStatistics />
        </div>
      </div>
      <CritterTeamOverview />
    </div>
  );
}