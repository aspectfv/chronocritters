export interface BattleStatisticsProps {
  wins: number;
  losses: number;
  totalBattles: number;
  winRate: number;
}

export type CritterType = 'Fire' | 'Water' | 'Electric' | 'Ground';

export interface CritterCardProps {
  name: string;
  level: number;
  type: CritterType;
}

export interface TrainerInfoProps {
  name: string;
  title: string;
  level: number;
  experience: number;
}