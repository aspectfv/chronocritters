import { Outlet } from 'react-router-dom';

function AuthPage() {
  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div className="bg-green-50 border border-green-100 rounded-lg p-8 w-full max-w-md shadow-sm">
        <Outlet />
      </div>
    </div>
  );
}

export default AuthPage;
