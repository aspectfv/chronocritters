import { Navigate } from 'react-router-dom';
import { useAuthStore } from '@store/auth/useAuthStore';
import type { ProtectedRouteProps } from '@components/auth/types';

export function ProtectedRoute({ children }: ProtectedRouteProps) {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);

  if (!isAuthenticated) {
    return <Navigate to={'/auth/login'} replace />;
  }

  return <>{children}</>;
}
