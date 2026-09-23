import { Outlet } from 'react-router-dom';

function AuthPage() {
  return (
    <main className="min-h-screen bg-canvas flex items-center justify-center p-4">
      <div className="bg-surface rounded-xl p-8 w-full max-w-md shadow-lg">
        <Outlet />
      </div>
    </main>
  );
}

export default AuthPage;