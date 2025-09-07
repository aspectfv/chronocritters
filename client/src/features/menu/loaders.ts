import { redirect } from 'react-router-dom';
import { useAuthStore } from '@store/auth/useAuthStore';
import { getPlayerStats } from '@api/user';

export async function menuLoader() {
  const { user } = useAuthStore.getState();

  if (!user?.id) {
    return redirect('/auth/login');
  }

  try {
    const stats = await getPlayerStats(user.id);
    return stats;
  } catch (error) {
    console.error("Failed to load trainer profile stats:", error);
    return { wins: 0, losses: 0 };
  }
}