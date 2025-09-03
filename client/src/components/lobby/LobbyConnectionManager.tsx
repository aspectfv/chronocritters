import { useEffect } from 'react';
import { useAuthStore } from '@store/auth/useAuthStore';
import { useLobbyStore } from '@store/lobby/useLobbyStore';

export function LobbyConnectionManager() {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  const { connect, disconnect } = useLobbyStore();

  useEffect(() => {
    if (isAuthenticated) {
      connect();
    }

    // This cleanup function will be called when the component unmounts
    // or when `isAuthenticated` changes from true to false (logout).
    return () => {
      disconnect();
    };
  }, [isAuthenticated, connect, disconnect]);

  // This component renders nothing.
  return null;
}