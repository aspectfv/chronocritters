import { Outlet } from 'react-router-dom';
import { ProfileHeader } from '../components/ProfileHeader';
import { ProfileTabs } from '../components/ProfileTabs';

function ProfilePage() {
  return (
    <main className="min-h-screen bg-arena text-arena-ink">
      <div className="mx-auto flex min-h-screen max-w-5xl flex-col gap-3 p-3 sm:p-5">
        <ProfileHeader />
        <ProfileTabs />
        <Outlet />
      </div>
    </main>
  );
}

export default ProfilePage;
