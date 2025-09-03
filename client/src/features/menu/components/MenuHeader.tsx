import { useAuthStore } from '@features/auth/store/useAuthStore';
import type { MenuHeaderProps } from '@features/menu/types';

export function MenuHeader({ username }: MenuHeaderProps) {
  const user = useAuthStore((state) => state.user);
  const displayName = username || user?.username || 'Trainer';

  return (
    <div className="text-center mb-8">
      <h1 className="text-4xl font-bold text-green-600 mb-4">Chrono-Critters</h1>
      <div className="flex items-center justify-center gap-2 text-gray-700">
        <div className="flex items-center gap-2">
          <span className="w-2 h-2 bg-green-500 rounded-full"></span>
          <span className="text-sm">{displayName}</span>
        </div>
        <div className="flex items-center gap-2">
          <span className="w-2 h-2 bg-green-500 rounded-full"></span>
          <span className="text-sm">Online</span>
        </div>
      </div>
    </div>
  );
}
