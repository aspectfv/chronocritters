import type { CritterType } from '@store/battle/types';

export interface CritterCardProps {
  name: string;
  level: number;
  type: CritterType;
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

export interface GetPlayerOverviewData {
  getPlayer: {
    __typename?: 'Player';
    id: string;
    username: string;
    stats: {
      __typename?: 'PlayerStats';
      wins: number;
      losses: number;
    };
    roster: {
      __typename?: 'Critter';
      name: string;
      type: CritterType;
    }[];
  };
}

export interface GetPlayerOverviewVars {
  id: string;
}

export interface TrainerInfoProps {
  username: string;
}

export interface BattleStatisticsProps {
  wins: number;
  losses: number;
}

export interface CritterTeamOverviewProps {
  roster: {
    name: string;
    type: CritterType;
  }[];
}