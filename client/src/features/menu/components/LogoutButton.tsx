import { LogOut } from 'lucide-react';
import { useAuthStore } from '@store/auth/useAuthStore';

export function LogoutButton() {
  const logout = useAuthStore((state) => state.logout);

  return (
    <button
      type="button"
      onClick={logout}
      className="mx-auto flex items-center gap-2 rounded-control px-3 py-2 text-sm font-semibold text-arena-ink-muted transition-colors hover:bg-arena-glass hover:text-arena-ink focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brass"
    >
      <LogOut className="h-4 w-4" aria-hidden="true" />
      Sign out
    </button>
  );
}
