import { lazy, Suspense } from 'react';
import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom';
import { loginAction, registerAction } from '@features/auth/actions';
import { loginLoader } from '@features/auth/loaders';
import { ProtectedRoute } from '@components/auth/ProtectedRoute';
import { RouteError } from '@components/RouteError';
import { RouteFallback } from '@components/RouteFallback';

const AuthPage = lazy(() => import('@features/auth/routes/AuthPage'));
const LoginForm = lazy(() => import('@features/auth/components/LoginForm'));
const RegisterForm = lazy(() => import('@features/auth/components/RegisterForm'));
const MenuPage = lazy(() => import('@features/menu/routes/MenuPage'));
const ProfilePage = lazy(() => import('@features/profile/routes/ProfilePage'));
const BattlePage = lazy(() => import('@features/battle/routes/BattlePage'));
const ResultsPage = lazy(() => import('@features/results/routes/ResultsPage'));
const CatalogPage = lazy(() => import('@features/catalog/routes/CatalogPage'));
const BattleHistoryTab = lazy(() => import('@features/profile/routes/BattleHistoryTab').then((m) => ({ default: m.BattleHistoryTab })));
const MyCrittersTab = lazy(() => import('@features/profile/routes/MyCrittersTab').then((m) => ({ default: m.MyCrittersTab })));
const OverviewTab = lazy(() => import('@features/profile/routes/OverviewTab').then((m) => ({ default: m.OverviewTab })));
const BattleHistoryDetails = lazy(() => import('@features/profile/routes/BattleHistoryDetails').then((m) => ({ default: m.BattleHistoryDetails })));
import { menuLoader } from '@features/menu/loaders';
import { battleHistoryEntryLoader, battleHistoryLoader, myCrittersLoader, overviewLoader } from '@features/profile/loaders';
import { resultsLoader } from '@features/results/loaders';
import { battleLoader } from '@features/battle/loaders';
import { catalogLoader } from '@features/catalog/loaders';

const router = createBrowserRouter([
  {
    path: '/',
    errorElement: <RouteError />,
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
            loader: battleHistoryLoader
          },
          {
            path: 'history/:battleId',
            element: <BattleHistoryDetails />,
            loader: battleHistoryEntryLoader
          }
        ],
      },
      {
        path: 'catalog',
        element: <ProtectedRoute><CatalogPage /></ProtectedRoute>,
        loader: catalogLoader
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
  <Suspense fallback={<RouteFallback />}>
    <RouterProvider router={router} />
  </Suspense>
);
