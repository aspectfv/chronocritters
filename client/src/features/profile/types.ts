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

export interface CritterData {
  __typename?: 'Critter';
  id: string;
  name: string;
  type: CritterType;
  baseStats: {
    __typename?: 'BaseStats';
    health: number;
    attack: number;
    defense: number;
  };
  abilities: {
      __typename?: 'Ability';
      id: string;
      name: string;
      power: number;
      type: string;
  }[];
}

export interface GetMyCrittersData {
  getPlayer: {
    __typename?: 'Player';
    id: string;
    roster: CritterData[];
  };
}

export interface GetMyCrittersVars {
  id: string;
}

export interface CritterListProps {
  roster: CritterData[];
  selectedCritter: CritterData | null;
  onCritterSelect: (critter: CritterData) => void;
}