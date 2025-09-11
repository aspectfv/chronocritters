import { useAuthStore } from '@store/auth/useAuthStore';
import { useLobbyStore } from '@store/lobby/useLobbyStore';
import type { MenuHeaderProps } from '@features/menu/types';
import { getConnectionStatusStyle } from '@utils/utils';

export function MenuHeader({ username }: MenuHeaderProps) {
  const user = useAuthStore((state) => state.user);
  const displayName = username || user?.username || 'Trainer';
  const connectionStatus = useLobbyStore((state) => state.connectionStatus);

  return (
    <div className="text-center mb-8">
      <h1 className="text-4xl font-bold text-green-800 mb-4">Chrono Critters</h1>
      <div className="flex items-center justify-center gap-4 text-gray-700">
        <div className="flex items-center gap-2">
          <svg className="h-5 w-5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
          </svg>
          <span className="text-sm font-medium">{displayName}</span>
        </div>
        <div className="flex items-center gap-2">
          <span className={`w-2.5 h-2.5 rounded-full ${getConnectionStatusStyle(connectionStatus).color}`}></span>
          <span className="text-sm font-medium">{getConnectionStatusStyle(connectionStatus).text}</span>
        </div>
      </div>
    </div>
  );
}