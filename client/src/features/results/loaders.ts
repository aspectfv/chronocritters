import { redirect } from 'react-router-dom';
import { useAuthStore } from '@store/auth/useAuthStore';
import { getBattleHistoryEntry, getPlayerResults } from '@api/user';
import type { GetPlayerResultsQuery } from '@/gql/graphql';
import type { ResultsLoaderData, ResultsLoaderParams } from '@features/results/types';

const EMPTY_PLAYER: GetPlayerResultsQuery = {
  getPlayer: {
    stats: { level: 1, experience: 0, experienceToNextLevel: 500 },
    roster: [],
  },
} as GetPlayerResultsQuery;

export async function resultsLoader({ params }: ResultsLoaderParams) {
  const { user } = useAuthStore.getState();

  if (!user?.id) {
    return redirect('/auth/login');
  }

  const playerResults = await getPlayerResults(user.id)
    .then((data) => data ?? EMPTY_PLAYER)
    .catch((error) => {
      console.error('Failed to load player data for results:', error);
      return EMPTY_PLAYER;
    });

  // Reloading the results page loses the router state the battle handed over,
  // so the recorded match is fetched as the fallback source of the outcome.
  const matchHistoryEntry = params.battleId
    ? await getBattleHistoryEntry(user.id, params.battleId)
        .then((data) => data?.getMatchHistoryEntry ?? null)
        .catch(() => null)
    : null;

  return { playerResults, matchHistoryEntry } satisfies ResultsLoaderData;
}
