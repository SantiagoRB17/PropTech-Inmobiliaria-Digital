import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom'
import AppShell from './components/layout/AppShell'
import Dashboard from './pages/Dashboard'
import Inmuebles from './pages/Inmuebles'
import InmuebleDetalle from './pages/InmuebleDetalle'
import Clientes from './pages/Clientes'
import Visitas from './pages/Visitas'
import Alertas from './pages/Alertas'
import Asesores from './pages/Asesores'
import Operaciones from './pages/Operaciones'
import Recomendaciones from './pages/Recomendaciones'
import Reportes from './pages/Reportes'
import Anomalias from './pages/Anomalias'

const router = createBrowserRouter([
  {
    path: '/',
    element: <AppShell />,
    children: [
      { index: true, element: <Navigate to="/dashboard" replace /> },
      { path: 'dashboard',       element: <Dashboard /> },
      { path: 'inmuebles',       element: <Inmuebles /> },
      { path: 'inmuebles/:id',   element: <InmuebleDetalle /> },
      { path: 'clientes',        element: <Clientes /> },
      { path: 'visitas',         element: <Visitas /> },
      { path: 'alertas',         element: <Alertas /> },
      { path: 'asesores',        element: <Asesores /> },
      { path: 'operaciones',     element: <Operaciones /> },
      { path: 'recomendaciones', element: <Recomendaciones /> },
      { path: 'reportes',        element: <Reportes /> },
      { path: 'anomalias',       element: <Anomalias /> },
    ],
  },
])

export default function App() {
  return <RouterProvider router={router} />
}
