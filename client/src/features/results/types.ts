import type { BaseStats, Critter, Player, PlayerStats } from "@/gql/graphql";

export type Result = 'victory' | 'defeat' | null;

export interface ResultsHeaderProps {
  result: Result;
  opponentName: string;
}

export interface ProgressSummaryProps {
  player: Player | null | undefined;
  critters: Critter[] | null | undefined;
  expGained: number;
}

export interface ProgressBarProps {
  name: string;
  finalStats: PlayerStats | BaseStats | null | undefined;
  expGained: number;
}

export interface LocationState {
  result: Result;
  battleRewards?: {
    playersExpGained: {
      [playerId: string]: number
    }
    crittersExpGained: {
      [critterId: string]: number
    }
  };
}