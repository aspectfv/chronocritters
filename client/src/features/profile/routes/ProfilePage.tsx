import { useState } from 'react';
import { ProfileHeader } from '../components/ProfileHeader';
import { ProfileTabs } from '../components/ProfileTabs';
import { OverviewTab } from '../components/OverviewTab';
import { MyCrittersTab } from '../components/MyCrittersTab';
import { BattleHistoryTab } from '../components/BattleHistoryTab';

export type ProfileTab = 'Overview' | 'My Critters' | 'Battle History';

function ProfilePage() {
  const [activeTab, setActiveTab] = useState<ProfileTab>('Overview');

  return (
    <div className="min-h-screen bg-[#F8FFF8] p-4 sm:p-6 md:p-8">
      <div className="max-w-7xl mx-auto">
        <ProfileHeader />
        <ProfileTabs activeTab={activeTab} setActiveTab={setActiveTab} />

        <div className="mt-6">
          {activeTab === 'Overview' && <OverviewTab />}
          {activeTab === 'My Critters' && <MyCrittersTab />}
          {activeTab === 'Battle History' && <BattleHistoryTab />}
        </div>
      </div>
    </div>
  );
}

export default ProfilePage;