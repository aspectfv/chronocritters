import { TrainerInfo } from './TrainerInfo';
import { BattleStatistics } from './BattleStatistics';
import { CritterTeamOverview } from './CritterTeamOverview';

export function OverviewTab() {
  return (
    <div className="space-y-8">
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-1">
          <TrainerInfo 
            name="a"
            title="Gold Trainer"
            level={15}
            experience={2450}
          />
        </div>
        <div className="lg:col-span-2">
          <BattleStatistics
            wins={24}
            losses={8}
            totalBattles={32}
            winRate={75}
          />
        </div>
      </div>
      <CritterTeamOverview />
    </div>
  );
}