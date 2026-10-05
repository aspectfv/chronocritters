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

export interface TrainerCardProps {
  player: MenuPlayer;
}

export interface PartyRailProps {
  roster: MenuPlayer['roster'];
}
