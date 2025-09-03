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

export interface TrainerProfileProps {
  wins?: number;
  losses?: number;
}

// Default data
export const defaultNotifications: NotificationItem[] = [
  {
    id: '1',
    message: 'Welcome back, trainer!',
    type: 'welcome'
  },
  {
    id: '2', 
    message: 'New critters available in the shop!',
    type: 'shop'
  }
];
