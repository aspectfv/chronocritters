import { Header } from '@features/menu/components/Header';
import { Notifications } from '@features/menu/components/Notifications';
import { BattleArena } from '@features/menu/components/BattleArena';
import { TrainerProfile } from '@features/menu/components/TrainerProfile';
import { LogoutButton } from '@features/menu/components/LogoutButton';

function MenuPage() {
  return (
    <div className="min-h-screen bg-white">
      <div className="max-w-4xl mx-auto px-4 py-8">
        {/* Header with title and user status */}
        <Header />
        
        {/* Notifications Section */}
        <Notifications />
        
        {/* Main Content Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          {/* Battle Arena Card */}
          <BattleArena />
          
          {/* Trainer Profile Card */}
          <TrainerProfile />
        </div>
        
        {/* Logout Button */}
        <LogoutButton />
      </div>
    </div>
  );
}

export default MenuPage;
