import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import { lazy, Suspense } from 'react'

const Home = lazy(() => import('../pages/Home'))

const router = createBrowserRouter([
  { path: '/', element: <Home /> },
])

export const AppRouter = () => (
  <Suspense fallback={<div>Loading...</div>}>
    <RouterProvider router={router} />
  </Suspense>
)
