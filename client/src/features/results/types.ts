import type { BaseStats, Critter, Player, PlayerStats } from "@/gql/graphql";
import type { BattleState } from "@store/battle/types";

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
  battleState: BattleState;
}