import { redirect } from 'react-router-dom';
import { useAuthStore } from '@store/auth/useAuthStore';
import { getPlayerOverview, getMyCritters, getBattleHistory, getBattleHistoryEntry } from '@api/user';

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

export async function battleHistoryLoader() {
  const { user } = useAuthStore.getState();

  if (!user?.id) {
    return redirect('/auth/login');
  }

  try {
    const history = await getBattleHistory(user.id);
    if (!history) {
        throw new Error("Player battle history not found.");
    }
    return history;
  } catch (error) {
    console.error("Failed to load battle history:", error);
    return [];
  }
}

export async function battleHistoryEntryLoader({ params }: { params: { battleId?: string } }) {
  const { user } = useAuthStore.getState();

  if (!user?.id) {
    return redirect('/auth/login');
  }

  const { battleId } = params;
  if (!battleId) {
    throw new Error("Battle ID is required");
  }

  try {
    const history = await getBattleHistoryEntry(user.id, battleId);
    if (!history) {
        throw new Error("Battle history entry not found.");
    }
    return history;
  } catch (error) {
    console.error("Failed to load battle history entry:", error);
    return null;
  }
}