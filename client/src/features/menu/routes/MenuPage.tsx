import { MenuHeader } from '@features/menu/components/MenuHeader';
import { Notifications } from '@features/menu/components/Notifications';
import { BattleArena } from '@features/menu/components/BattleArena';
import { TrainerProfile } from '@features/menu/components/TrainerProfile';
import { LogoutButton } from '@features/menu/components/LogoutButton';
import { useLoaderData } from 'react-router-dom';
import type { GetPlayerStatsData } from '../types';

function MenuPage() {
  const { wins, losses } = useLoaderData() as GetPlayerStatsData['getPlayer']['stats'];

  return (
    <div className="min-h-screen bg-white">
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
