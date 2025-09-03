import type { NotificationsProps } from '@features/menu/types';
import { defaultNotifications } from '@features/menu/types';

export function Notifications({ notifications = defaultNotifications }: NotificationsProps) {
  return (
    <div className="bg-green-50 border border-green-200 rounded-lg p-4 mb-6">
      <div className="flex items-center gap-2 text-green-700 mb-3">
        <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
          <path d="M10 2a6 6 0 00-6 6v3.586l-.707.707A1 1 0 004 14h12a1 1 0 00.707-1.707L16 11.586V8a6 6 0 00-6-6zM10 18a3 3 0 01-3-3h6a3 3 0 01-3 3z" />
        </svg>
        <span className="font-medium">Notifications</span>
      </div>
      <div className="space-y-2">
        {notifications.map((notification) => (
          <div key={notification.id} className="text-green-700 text-sm">
            {notification.message}
          </div>
        ))}
      </div>
    </div>
  );
}
