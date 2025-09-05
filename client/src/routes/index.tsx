import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom';
import { Suspense } from 'react';
import { loginAction, registerAction } from '@features/auth/actions';
import { loginLoader } from '@features/auth/loaders';
import AuthPage from '@features/auth/routes/AuthPage';
import LoginForm from '@features/auth/components/LoginForm';
import RegisterForm from '@features/auth/components/RegisterForm';
import MenuPage from '@features/menu/routes/MenuPage';
import ProfilePage from '@features/profile/routes/ProfilePage';
import BattlePage from '@features/battle/routes/BattlePage';
import ResultsPage from '@features/results/routes/ResultsPage';
import { ProtectedRoute } from '@components/auth/ProtectedRoute';
import { BattleHistoryTab } from '@features/profile/components/battlehistory/BattleHistoryTab';
import { MyCrittersTab } from '@features/profile/components/mycritters/MyCrittersTab';
import { OverviewTab } from '@features/profile/components/overview/OverviewTab';

const router = createBrowserRouter([
  {
    path: '/',
    children: [
      {
        index: true,
        element: <Navigate to="/auth/login" replace />,
      },
      {
        path: 'auth',
        element: <AuthPage />,
        children: [
          {
            path: 'login',
            element: <LoginForm />,
            loader: loginLoader,
            action: loginAction,
          },
          {
            path: 'register',
            element: <RegisterForm />,
            action: registerAction,
          },
        ],
      },
      {
        path: 'menu',
        element: <ProtectedRoute><MenuPage /></ProtectedRoute>,
      },
      {
        path: 'profile',
        element: <ProtectedRoute><ProfilePage /></ProtectedRoute>,
        children: [
          {
            index: true,
            element: <OverviewTab />,
          },
          {
            path: 'critters',
            element: <MyCrittersTab />,
          },
          {
            path: 'history',
            element: <BattleHistoryTab />,
          },
        ],
      },
      {
        path: 'battle/:battleId',
        element: <ProtectedRoute><BattlePage /></ProtectedRoute>,
      },
      {
        path: 'results/:battleId',
        element: <ProtectedRoute><ResultsPage /></ProtectedRoute>,
      },
    ],
  },
]);

export const AppRouter = () => (
  <Suspense fallback={<div>Loading...</div>}>
    <RouterProvider router={router} />
  </Suspense>
);
