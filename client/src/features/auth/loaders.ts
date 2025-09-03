import { redirect } from 'react-router-dom';
import { useAuthStore } from '@store/auth/useAuthStore';

export function loginLoader() {
  const { isAuthenticated } = useAuthStore.getState();
  
  if (isAuthenticated) {
    return redirect('/menu');
  }

  return null;
}