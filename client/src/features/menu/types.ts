export enum MatchMakingStatus {
  IDLE = 'idle',
  SEARCHING = 'searching',
  FOUND = 'found',
}

export interface MenuHeaderProps {
  username?: string;
}

export interface NotificationItem {
  id: string;
  message: string;
  type: 'welcome' | 'shop' | 'battle' | 'general';
}

export interface NotificationsProps {
  notifications?: NotificationItem[];
}

export interface MatchResponse {
  playerOneId: string;
  playerTwoId: string;
  battleId: string;
}

export interface GetPlayerStatsVars {
  id: string;
}

export interface GetPlayerStatsData {
  getPlayer: {
    __typename?: 'Player';
    stats: {
      __typename?: 'PlayerStats';
      wins: number;
      losses: number;
    };
  };
}

export interface TrainerProfileProps {
  wins: number;
  losses: number;
}