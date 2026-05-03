import { useLocation } from 'react-router-dom'

const TITULOS = {
  '/dashboard':        'Dashboard',
  '/inmuebles':        'Inmuebles',
  '/clientes':         'Clientes',
  '/visitas':          'Visitas',
  '/alertas':          'Alertas',
  '/asesores':         'Asesores',
  '/operaciones':      'Operaciones',
  '/recomendaciones':  'Recomendaciones',
  '/reportes':         'Reportes',
  '/anomalias':        'Anomalías',
}

export default function TopBar() {
  const { pathname } = useLocation()
  const base = '/' + pathname.split('/')[1]
  const titulo = TITULOS[base] || 'PropTech'

  return (
    <header
      style={{
        height: 'var(--topbar-height)',
        background: 'var(--color-surface)',
        borderBottom: '1px solid var(--color-sand)',
        display: 'flex',
        alignItems: 'center',
        padding: '0 28px',
        gap: 16,
        flexShrink: 0,
      }}
    >
      <h1 style={{ fontSize: 17, fontWeight: 700, color: 'var(--color-text)', flex: 1 }}>
        {titulo}
      </h1>

      {/* Avatar */}
      <div
        style={{
          width: 34, height: 34, borderRadius: '50%',
          background: 'linear-gradient(135deg, var(--color-accent), var(--color-panel))',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          color: '#fff', fontWeight: 700, fontSize: 12, cursor: 'pointer',
        }}
      >
        SB
      </div>
    </header>
  )
}
