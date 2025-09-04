import { Outlet } from 'react-router-dom'; // Import Outlet
import { ProfileHeader } from '../components/ProfileHeader';
import { ProfileTabs } from '../components/ProfileTabs';

function ProfilePage() {
  return (
    <div className="min-h-screen bg-[#F8FFF8] p-4 sm:p-6 md:p-8">
      <div className="max-w-7xl mx-auto">
        <ProfileHeader />
        <ProfileTabs />
        <div className="mt-6">
          <Outlet />
        </div>
      </div>
    </div>
  );
}

export default ProfilePage;