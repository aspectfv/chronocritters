import type { BaseStats, Critter, PlayerStats } from "@/gql/graphql";

export type Result = 'victory' | 'defeat' | null;

export interface ResultsHeaderProps {
  result: Result;
  opponentName: string;
}

export interface ProgressSummaryProps {
  playerStats: PlayerStats;
  critters: Critter[] | null | undefined;
  xpGained: number;
}

export interface ProgressBarProps {
  name: string;
  finalStats: PlayerStats | BaseStats | null | undefined;
  xpGained: number;
}