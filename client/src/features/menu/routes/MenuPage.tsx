import { MenuHeader } from '@features/menu/components/MenuHeader';
import { BattleArena } from '@features/menu/components/BattleArena';
import { TrainerProfile } from '@features/menu/components/TrainerProfile';
import { LogoutButton } from '@features/menu/components/LogoutButton';
import { useLoaderData, useSearchParams } from 'react-router-dom';
import type { GetPlayerStatsQuery } from 'src/gql/graphql';

const notices: Record<string, string> = {
  'battle-ended': 'That battle is no longer running, so you have been returned to the menu.',
};

function MenuPage() {
  const loaderData = useLoaderData() as GetPlayerStatsQuery;
  const [searchParams] = useSearchParams();
  const wins = loaderData?.getPlayer?.stats?.wins ?? 0;
  const losses = loaderData?.getPlayer?.stats?.losses ?? 0;
  const notice = notices[searchParams.get('notice') ?? ''];

  return (
    <main className="min-h-screen bg-canvas">
      <div className="max-w-4xl mx-auto px-4 py-8">
        <MenuHeader />
        {notice && (
          <div role="status" className="mb-6 rounded-lg border border-warn/35 bg-warn-soft px-4 py-3 text-sm text-warn-ink">
            {notice}
          </div>
        )}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          <BattleArena />
          <TrainerProfile wins={wins} losses={losses} />
        </div>
        <LogoutButton />
      </div>
    </main>
  );
}

export default MenuPage;