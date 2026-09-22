import type { BaseStats, Critter, GetBattleHistoryEntryQuery, GetPlayerResultsQuery, Player, PlayerStats } from "@/gql/graphql";
import type { BattleRewards, BattleStats, PlayerState } from "@store/battle/types";

export type Result = 'victory' | 'defeat' | null;

export interface ResultsHeaderProps {
  result: Result;
  opponentName: string;
}

export interface ProgressSummaryProps {
  player: Player | null | undefined;
  critters: Critter[] | null | undefined;
  expGained: number;
  critterExpGained: Record<string, number>;
}

export interface ProgressBarProps {
  name: string;
  finalStats: PlayerStats | BaseStats | null | undefined;
  expGained: number;
}

/** The slice of the finished battle the results screen renders. */
export interface BattleOutcomeSummary {
  battleStats: BattleStats;
  battleRewards?: BattleRewards;
  opponent: PlayerState;
}

export interface LocationState {
  result: Result;
  battleState: BattleOutcomeSummary;
}

export type MatchHistoryOutcome = NonNullable<GetBattleHistoryEntryQuery['getMatchHistoryEntry']>;

export interface ResultsLoaderData {
  playerResults: GetPlayerResultsQuery;
  matchHistoryEntry: MatchHistoryOutcome | null;
}

export interface ResultsLoaderParams {
  params: {
    battleId?: string;
  };
}
