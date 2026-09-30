import type { GetPlayerOverviewQuery } from '@/gql/graphql';

export enum MatchMakingStatus {
  IDLE = 'idle',
  SEARCHING = 'searching',
  FOUND = 'found',
}

export interface MatchResponse {
  playerOneId: string;
  playerTwoId: string;
  battleId: string;
}

export type MenuPlayer = NonNullable<GetPlayerOverviewQuery['getPlayer']>;

/** How many critters a player takes into a battle. Gamelogic enforces the same number. */
export const TEAM_SIZE = 3;

export interface TeamSelection {
  team: string[];
  teamSize: number;
  onToggle: (critterId: string) => void;
}

export interface TrainerCardProps extends TeamSelection {
  player: MenuPlayer;
}

export interface TeamPickerProps extends TeamSelection {
  roster: MenuPlayer['roster'];
}

export interface FindMatchProps {
  team: string[];
  isTeamReady: boolean;
}
