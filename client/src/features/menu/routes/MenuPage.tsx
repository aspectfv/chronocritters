import { Header } from '@features/menu/components/Header';
import { Notifications } from '@features/menu/components/Notifications';
import { BattleArena } from '@features/menu/components/BattleArena';
import { TrainerProfile } from '@features/menu/components/TrainerProfile';
import { LogoutButton } from '@features/menu/components/LogoutButton';

function MenuPage() {
  return (
    <div className="min-h-screen bg-white">
      <div className="max-w-4xl mx-auto px-4 py-8">
        <Header />
        <Notifications />
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          <BattleArena />
          <TrainerProfile />
        </div>
        <LogoutButton />
      </div>
    </div>
  );
}

export default MenuPage;
