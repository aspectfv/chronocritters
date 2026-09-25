import { redirect } from 'react-router-dom';
import { useAuthStore } from '@store/auth/useAuthStore';
import { getPlayerOverview } from '@api/user';

export async function menuLoader() {
  const { user } = useAuthStore.getState();

  if (!user?.id) {
    return redirect('/auth/login');
  }

  return getPlayerOverview(user.id);
}
