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
      <h1 className="text-4xl font-bold text-green-600 mb-4">ChronoCritters</h1>
      <div className="flex items-center justify-center gap-2 text-gray-700">
        <div className="flex items-center gap-2">
          <span className="w-2 h-2 bg-green-500 rounded-full"></span>
          <span className="text-sm">{displayName}</span>
        </div>
        <div className="flex items-center gap-2">
          <span className={`w-2 h-2 rounded-full ${getConnectionStatusStyle(connectionStatus).color}`}></span>
          <span className="text-sm">{getConnectionStatusStyle(connectionStatus).text}</span>
        </div>
      </div>
    </div>
  );
}
