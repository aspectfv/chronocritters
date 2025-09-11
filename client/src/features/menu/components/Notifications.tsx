import type { NotificationItem, NotificationsProps } from '@features/menu/types';

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

export function Notifications({ notifications = defaultNotifications }: NotificationsProps) {
  return (
    <div className="bg-white border border-gray-200 rounded-xl p-6 mb-6 shadow-sm">
      <div className="flex items-center gap-2 text-gray-700 mb-4">
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
        </svg>
        <span className="font-semibold">Notifications</span>
      </div>
      <div className="space-y-3">
        {notifications.map((notification) => (
          <div key={notification.id} className="bg-green-50 text-green-800 text-sm rounded-lg p-3">
            {notification.message}
          </div>
        ))}
      </div>
    </div>
  );
}