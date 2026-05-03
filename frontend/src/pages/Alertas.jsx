import { useEffect, useState } from 'react'
import { Bell, CheckCircle } from 'lucide-react'
import DSBadge from '../components/ui/DSBadge'
import StatusBadge from '../components/ui/StatusBadge'
import { getAlertas, getAlertasHistorial, resolverAlerta } from '../api'

const NIVELES = ['CRITICA', 'ALTA', 'MEDIA', 'BAJA']
const NIVEL_COLOR = {
  CRITICA: 'var(--color-danger)',
  ALTA: '#f97316',
  MEDIA: 'var(--color-warning)',
  BAJA: 'var(--color-text-muted)',
}

export default function Alertas() {
  const [alertas, setAlertas] = useState([])
  const [loading, setLoading] = useState(true)
  const [filtroNivel, setFiltroNivel] = useState('')
  const [vista, setVista] = useState('activas')

  const cargar = () => {
    setLoading(true)
    const fn = vista === 'historial' ? getAlertasHistorial : getAlertas
    fn()
      .then((r) => setAlertas(r.data))
      .finally(() => setLoading(false))
  }

  useEffect(() => { cargar() }, [vista])

  const handleResolver = async (idAlerta) => {
    try {
      await resolverAlerta(idAlerta)
      cargar()
    } catch {
      alert('Error al resolver la alerta')
    }
  }

  const alertasFiltradas = filtroNivel
    ? alertas.filter((a) => a.nivel === filtroNivel)
    : alertas

  const conteo = (nivel) => alertas.filter((a) => a.nivel === nivel).length

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>

      {/* Tabs activas / historial */}
      <div style={{ display: 'flex', gap: 8 }}>
        <button className={`pill ${vista === 'activas' ? 'active' : ''}`} onClick={() => setVista('activas')}>
          Activas
        </button>
        <button className={`pill ${vista === 'historial' ? 'active' : ''}`} onClick={() => setVista('historial')}>
          Historial completo
        </button>
      </div>

      {/* KPI cards por nivel */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 16 }}>
        {NIVELES.map((nivel) => {
          const color = NIVEL_COLOR[nivel]
          return (
            <div key={nivel} className="card"
              style={{ borderLeft: `3px solid ${color}`, cursor: 'pointer', transition: 'opacity 0.15s', opacity: filtroNivel && filtroNivel !== nivel ? 0.5 : 1 }}
              onClick={() => setFiltroNivel(filtroNivel === nivel ? '' : nivel)}
            >
              <div style={{ fontSize: 11, fontWeight: 600, color, textTransform: 'uppercase', letterSpacing: 0.5, marginBottom: 6 }}>
                {nivel}
              </div>
              <div style={{ fontSize: 28, fontWeight: 800, color: 'var(--color-text)' }}>
                {conteo(nivel)}
              </div>
              <div style={{ fontSize: 12, color: 'var(--color-text-muted)', marginTop: 2 }}>alertas</div>
            </div>
          )
        })}
      </div>

      {/* Tabla de alertas */}
      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '16px 20px', borderBottom: '1px solid var(--color-sand)' }}>
          <div style={{ display: 'flex', gap: 10, alignItems: 'center' }}>
            <div className="section-title" style={{ marginBottom: 0 }}>
              {filtroNivel ? `Alertas ${filtroNivel}` : 'Todas las alertas'} ({alertasFiltradas.length})
            </div>
            {filtroNivel && (
              <button className="btn-ghost" style={{ fontSize: 11 }} onClick={() => setFiltroNivel('')}>✕ Limpiar</button>
            )}
          </div>
          <DSBadge icon="⚡" label="PriorityQueue O(log n)" color="var(--color-danger)" />
        </div>

        {loading ? (
          <div className="loading-center"><div className="spinner" /> Cargando alertas…</div>
        ) : alertasFiltradas.length === 0 ? (
          <div className="empty-state"><Bell size={40} /><div>No hay alertas{filtroNivel ? ` de nivel ${filtroNivel}` : ' activas'}.</div></div>
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th style={{ width: 4 }}></th>
                <th>Nivel</th>
                <th>Tipo</th>
                <th>Descripción</th>
                <th>Entidad</th>
                <th>Fecha</th>
                <th>Acción</th>
              </tr>
            </thead>
            <tbody>
              {alertasFiltradas.map((a) => {
                const color = NIVEL_COLOR[a.nivel] || 'var(--color-text-muted)'
                return (
                  <tr key={a.idAlerta}>
                    <td style={{ padding: 0 }}>
                      <div style={{ width: 4, height: 48, background: color }} />
                    </td>
                    <td><StatusBadge value={a.nivel} /></td>
                    <td>
                      <DSBadge icon="⚡" label={a.tipo?.replace(/_/g, ' ')} color={color} />
                    </td>
                    <td style={{ fontSize: 13, maxWidth: 300 }}>{a.descripcion}</td>
                    <td>
                      <span className="mono" style={{ fontSize: 11, color: 'var(--color-accent)' }}>
                        {a.entidadRelacionada}
                      </span>
                    </td>
                    <td>
                      <span className="mono" style={{ fontSize: 10, color: 'var(--color-text-muted)' }}>
                        {a.fecha ? new Date(a.fecha).toLocaleString('es-CO', { dateStyle: 'short', timeStyle: 'short' }) : '—'}
                      </span>
                    </td>
                    <td>
                      <button className="btn-ghost" style={{ fontSize: 11, gap: 4 }} onClick={() => handleResolver(a.idAlerta)}>
                        <CheckCircle size={12} /> Resolver
                      </button>
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        )}
      </div>
    </div>
  )
}
