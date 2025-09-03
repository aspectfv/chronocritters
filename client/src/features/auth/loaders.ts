import { redirect } from 'react-router-dom';
import { useAuthStore } from '@features/auth/store/useAuthStore';

export function loginLoader() {
  const { isAuthenticated } = useAuthStore.getState();
  
  if (isAuthenticated) {
    return redirect('/menu');
  }

  return null;
}