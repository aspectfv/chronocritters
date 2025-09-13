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
import { BattleHistoryTab } from '@features/profile/routes/BattleHistoryTab';
import { MyCrittersTab } from '@features/profile/routes/MyCrittersTab';
import { OverviewTab } from '@features/profile/routes/OverviewTab';
import { menuLoader } from '@features/menu/loaders';
import { myCrittersLoader, overviewLoader } from '@features/profile/loaders';
import { BattleHistoryDetails } from '@features/profile/routes/BattleHistoryDetails';
import { resultsLoader } from '@features/results/loaders';
import { battleLoader } from '@features/battle/loaders';

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
        loader: menuLoader,
      },
      {
        path: 'profile',
        element: <ProtectedRoute><ProfilePage /></ProtectedRoute>,
        children: [
          {
            index: true,
            element: <OverviewTab />,
            loader: overviewLoader
          },
          {
            path: 'critters',
            element: <MyCrittersTab />,
            loader: myCrittersLoader
          },
          {
            path: 'history',
            element: <BattleHistoryTab />,
          },
          {
            path: 'history/:battleId',
            element: <BattleHistoryDetails />,
          }
        ],
      },
      {
        path: 'battle/:battleId',
        element: <ProtectedRoute><BattlePage /></ProtectedRoute>,
        loader: battleLoader
      },
      {
        path: 'results/:battleId',
        element: <ProtectedRoute><ResultsPage /></ProtectedRoute>,
        loader: resultsLoader
      },
    ],
  },
]);

export const AppRouter = () => (
  <Suspense fallback={<div>Loading...</div>}>
    <RouterProvider router={router} />
  </Suspense>
);
