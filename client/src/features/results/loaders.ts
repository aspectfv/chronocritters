import { redirect } from 'react-router-dom';
import { useAuthStore } from '@store/auth/useAuthStore';
import { getPlayerResults } from '@api/user';

export async function resultsLoader() {
  const { user } = useAuthStore.getState();

  if (!user?.id) {
    return redirect('/auth/login');
  }

  try {
    const playerData = await getPlayerResults(user.id);
    return playerData;
  } catch (error) {
    console.error("Failed to load player data for results:", error);
    return {
      getPlayer: {
        stats: { level: 1, experience: 0, experienceToNextLevel: 500 },
        roster: [],
      },
    };
  }
}