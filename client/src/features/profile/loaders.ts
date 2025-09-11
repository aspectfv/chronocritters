import { redirect } from 'react-router-dom';
import { useAuthStore } from '@store/auth/useAuthStore';
import { getPlayerOverview, getMyCritters } from '@api/user';

export async function overviewLoader() {
  const { user } = useAuthStore.getState();

  if (!user?.id) {
    return redirect('/auth/login');
  }

  try {
    const overviewData = await getPlayerOverview(user.id);
    return overviewData;
  } catch (error) {
    console.error("Failed to load player overview:", error);
    return {
        id: user.id,
        username: user.username,
        stats: { wins: 0, losses: 0 },
        roster: []
    };
  }
}

export async function myCrittersLoader() {
  const { user } = useAuthStore.getState();

  if (!user?.id) {
    return redirect('/auth/login');
  }

  try {
    const roster = await getMyCritters(user.id);
    if (!roster) {
        throw new Error("Player roster not found.");
    }
    return roster;
  } catch (error) {
    console.error("Failed to load player critters:", error);
    return [];
  }
}