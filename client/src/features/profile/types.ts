import type { CritterType } from '@store/battle/types';

export interface BattleStatisticsProps {
  wins: number;
  losses: number;
  totalBattles: number;
  winRate: number;
}

export interface CritterCardProps {
  name: string;
  level: number;
  type: CritterType;
}

export interface GetTrainerInfoData {
  getPlayer: {
    __typename: 'Player';
    id: string;
    username: string;
  };
}

export interface GetTrainerInfoVars {
  id: string;
}

export interface GetBattleStatsData {
  getPlayer: {
    __typename: 'Player';
    id: string;
    stats: {
      __typename: 'PlayerStats';
      wins: number;
      losses: number;
    };
  };
}

export interface GetBattleStatsVars {
  id: string;
}

export interface RosterCritter {
  name: string;
  type: CritterType;
}

export interface GetCritterTeamData {
  getPlayer: {
    id: string;
    roster: RosterCritter[];
  };
}

export interface GetCritterTeamVars {
  id: string;
}