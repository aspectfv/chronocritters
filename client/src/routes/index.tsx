import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { lazy, Suspense } from 'react';

const AuthPage = lazy(() => import('../features/auth/routes/AuthPage'));
const MenuPage = lazy(() => import('../features/menu/routes/MenuPage'));
const ProfilePage = lazy(() => import('../features/profile/routes/ProfilePage'));
const BattlePage = lazy(() => import('../features/battle/routes/BattlePage'));
const ResultsPage = lazy(() => import('../features/results/routes/ResultsPage'));

const router = createBrowserRouter([
  { path: '/', element: <AuthPage /> },
  { path: '/menu', element: <MenuPage /> },
  { path: '/profile', element: <ProfilePage /> },
  { path: '/battle', element: <BattlePage /> },
  { path: '/results', element: <ResultsPage /> },
]);

export const AppRouter = () => (
  <Suspense fallback={<div>Loading...</div>}>
    <RouterProvider router={router} />
  </Suspense>
);
