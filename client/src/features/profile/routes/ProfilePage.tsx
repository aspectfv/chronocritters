import { Outlet } from 'react-router-dom';
import { ProfileHeader } from '../components/ProfileHeader';
import { ProfileTabs } from '../components/ProfileTabs';

function ProfilePage() {
  return (
    <main className="min-h-screen bg-canvas p-4 sm:p-6 md:p-8">
      <div className="max-w-7xl mx-auto">
        <ProfileHeader />
        <ProfileTabs />
        <div className="mt-6">
          <Outlet />
        </div>
      </div>
    </main>
  );
}

export default ProfilePage;