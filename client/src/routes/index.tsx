import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom';
import { Suspense } from 'react';
import { loginAction, registerAction } from '@features/auth/actions';
import AuthPage from '@features/auth/routes/AuthPage';
import LoginForm from '@features/auth/components/LoginForm';
import RegisterForm from '@features/auth/components/RegisterForm';
import MenuPage from '@features/menu/routes/MenuPage';
import ProfilePage from '@features/profile/routes/ProfilePage';
import BattlePage from '@features/battle/routes/BattlePage';
import ResultsPage from '@features/results/routes/ResultsPage';
import { ProtectedRoute } from '../components/ProtectedRoute';

const router = createBrowserRouter([
  {
    path: '/',
    children: [
      {
        index: true,
        element: <Navigate to="/auth" replace />,
      },
      {
        path: 'auth',
        element: <AuthPage />,
        children: [
          {
            index: true,
            element: <LoginForm />,
            action: loginAction,
          },
          {
            path: 'login',
            element: <LoginForm />,
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
      },
      {
        path: 'battle',
        element: <ProtectedRoute><BattlePage /></ProtectedRoute>,
      },
      {
        path: 'results',
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
