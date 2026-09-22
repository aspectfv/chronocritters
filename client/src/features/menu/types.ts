export enum MatchMakingStatus {
  IDLE = 'idle',
  SEARCHING = 'searching',
  FOUND = 'found',
}

export interface MenuHeaderProps {
  username?: string;
}

export interface MatchResponse {
  playerOneId: string;
  playerTwoId: string;
  battleId: string;
}

export interface TrainerProfileProps {
  wins: number;
  losses: number;
}