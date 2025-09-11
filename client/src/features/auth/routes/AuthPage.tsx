import { Outlet } from 'react-router-dom';

function AuthPage() {
  return (
    <div className="min-h-screen bg-[#f0f7f3] flex items-center justify-center p-4">
      <div className="bg-white rounded-xl p-8 w-full max-w-md shadow-lg">
        <Outlet />
      </div>
    </div>
  );
}

export default AuthPage;