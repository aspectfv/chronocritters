import { MenuHeader } from '@features/menu/components/MenuHeader';
import { Notifications } from '@features/menu/components/Notifications';
import { BattleArena } from '@features/menu/components/BattleArena';
import { TrainerProfile } from '@features/menu/components/TrainerProfile';
import { LogoutButton } from '@features/menu/components/LogoutButton';
import { useLoaderData } from 'react-router-dom';
import type { GetPlayerStatsQuery } from 'src/gql/graphql';

function MenuPage() {
  const loaderData = useLoaderData() as GetPlayerStatsQuery;
  const wins = loaderData?.getPlayer?.stats?.wins ?? 0;
  const losses = loaderData?.getPlayer?.stats?.losses ?? 0;

  return (
    <div className="min-h-screen bg-[#f0f7f3]">
      <div className="max-w-4xl mx-auto px-4 py-8">
        <MenuHeader />
        <Notifications />
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          <BattleArena />
          <TrainerProfile wins={wins} losses={losses} />
        </div>
        <LogoutButton />
      </div>
    </div>
  );
}

export default MenuPage;