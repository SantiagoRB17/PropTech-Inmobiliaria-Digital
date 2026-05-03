import { NavLink } from 'react-router-dom'
import {
  LayoutDashboard, Building2, Users, Calendar,
  Bell, UserCheck, FileText, Network, BarChart3, ShieldAlert
} from 'lucide-react'

const NAV_ITEMS = [
  { to: '/dashboard',       icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/inmuebles',       icon: Building2,       label: 'Inmuebles' },
  { to: '/clientes',        icon: Users,           label: 'Clientes' },
  { to: '/visitas',         icon: Calendar,        label: 'Visitas' },
  { to: '/alertas',         icon: Bell,            label: 'Alertas' },
  { to: '/asesores',        icon: UserCheck,       label: 'Asesores' },
  { to: '/operaciones',     icon: FileText,        label: 'Operaciones' },
  { to: '/recomendaciones', icon: Network,         label: 'Recomendaciones' },
  { to: '/reportes',        icon: BarChart3,       label: 'Reportes' },
  { to: '/anomalias',       icon: ShieldAlert,     label: 'Anomalías' },
]

export default function Sidebar() {
  return (
    <aside
      style={{
        width: 'var(--sidebar-width)',
        minWidth: 'var(--sidebar-width)',
        height: '100vh',
        background: 'var(--color-bg-dark)',
        borderRight: '1px solid var(--color-panel)',
        display: 'flex',
        flexDirection: 'column',
        overflow: 'hidden',
      }}
    >
      {/* Logo */}
      <div style={{ padding: '18px 16px 14px', borderBottom: '1px solid var(--color-panel)' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <div
            style={{
              width: 36, height: 36,
              background: 'var(--color-accent)',
              borderRadius: 9,
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              flexShrink: 0,
            }}
          >
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z" />
              <polyline points="9 22 9 12 15 12 15 22" />
            </svg>
          </div>
          <div>
            <div style={{ fontSize: 15, fontWeight: 800, color: 'var(--color-sand)', lineHeight: 1.2 }}>
              PropTech
            </div>
            <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 9, color: 'var(--color-text-muted)' }}>
              Gestión Inmobiliaria
            </div>
          </div>
        </div>
      </div>

      {/* Navegación */}
      <nav style={{ flex: 1, overflowY: 'auto', padding: '10px 8px' }}>
        {NAV_ITEMS.map(({ to, icon: Icon, label }) => (
          <NavLink
            key={to}
            to={to}
            style={({ isActive }) => ({
              display: 'flex',
              alignItems: 'center',
              gap: 10,
              padding: '9px 12px',
              borderRadius: 7,
              marginBottom: 2,
              textDecoration: 'none',
              background: isActive ? 'var(--color-panel)' : 'transparent',
              color: isActive ? 'var(--color-sand)' : 'var(--color-text-muted)',
              fontWeight: isActive ? 600 : 400,
              fontSize: 13,
              transition: 'background 0.15s, color 0.15s',
            })}
          >
            {({ isActive }) => (
              <>
                <Icon size={16} color={isActive ? 'var(--color-accent)' : 'var(--color-text-muted)'} />
                <span style={{ flex: 1 }}>{label}</span>
              </>
            )}
          </NavLink>
        ))}
      </nav>

      {/* Avatar usuario */}
      <div style={{ padding: '14px 16px', borderTop: '1px solid var(--color-panel)', display: 'flex', alignItems: 'center', gap: 10 }}>
        <div
          style={{
            width: 32, height: 32, borderRadius: '50%',
            background: 'var(--color-accent)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            color: '#fff', fontWeight: 700, fontSize: 12, flexShrink: 0,
          }}
        >
          SB
        </div>
        <div>
          <div style={{ fontSize: 12, fontWeight: 600, color: 'var(--color-sand)' }}>Santiago B.</div>
          <div style={{ fontSize: 10, color: 'var(--color-text-muted)' }}>Admin</div>
        </div>
      </div>
    </aside>
  )
}
