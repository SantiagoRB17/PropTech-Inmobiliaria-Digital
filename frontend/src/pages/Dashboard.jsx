import { useEffect, useState } from 'react'
import { Building2, Users, Calendar, AlertTriangle, RotateCcw } from 'lucide-react'
import KpiCard from '../components/ui/KpiCard'
import DSBadge from '../components/ui/DSBadge'
import StatusBadge from '../components/ui/StatusBadge'
import GrafoPanel from '../components/ui/GrafoPanel'
import {
  getInmuebles, getClientes, getVisitas, getAlertas,
  getReporteZonas, deshacerInmueble
} from '../api'

export default function Dashboard() {
  const [inmuebles, setInmuebles] = useState([])
  const [clientes, setClientes] = useState([])
  const [visitas, setVisitas] = useState([])
  const [alertas, setAlertas] = useState([])
  const [zonas, setZonas] = useState({})
  const [loading, setLoading] = useState(true)
  const [pilaAcciones, setPilaAcciones] = useState([])
  const [msgDeshacer, setMsgDeshacer] = useState('')

  useEffect(() => {
    Promise.all([
      getInmuebles(),
      getClientes(),
      getVisitas(),
      getAlertas(),
      getReporteZonas(),
    ])
      .then(([inm, cli, vis, ale, zon]) => {
        setInmuebles(inm.data)
        setClientes(cli.data)
        setVisitas(vis.data)
        setAlertas(ale.data)
        setZonas(zon.data)
      })
      .finally(() => setLoading(false))
  }, [])

  const totalInmuebles = inmuebles.length
  const clientesActivos = clientes.filter((c) => c.estadoBusqueda === 'ACTIVO').length
  const visitasPendientes = visitas.filter((v) => v.estado === 'PENDIENTE').length
  const alertasCriticas = alertas.filter((a) => a.nivel === 'CRITICA').length

  const visitasPendientesOrdenadas = visitas
    .filter((v) => v.estado === 'PENDIENTE')
    .slice(0, 5)

  const alertasRecientes = alertas.slice(0, 5)

  const zonasOrdenadas = Object.entries(zonas)
    .sort(([, a], [, b]) => b - a)
    .slice(0, 5)
  const maxVisitas = zonasOrdenadas[0]?.[1] || 1

  const handleDeshacer = async () => {
    try {
      const res = await deshacerInmueble()
      const codigo = res.data.codigo
      setPilaAcciones((prev) => [
        { codigo, accion: 'Restaurado', ts: new Date().toLocaleTimeString() },
        ...prev.slice(0, 2),
      ])
      setMsgDeshacer(`✓ Restaurado ${codigo}`)
      setTimeout(() => setMsgDeshacer(''), 3000)
    } catch (e) {
      setMsgDeshacer('Sin cambios que deshacer.')
      setTimeout(() => setMsgDeshacer(''), 3000)
    }
  }

  if (loading) {
    return (
      <div className="loading-center">
        <div className="spinner" />
        Cargando dashboard…
      </div>
    )
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>

      {/* Fila 1 — KPIs */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 16 }}>
        <KpiCard
          icon={<Building2 size={20} />}
          label="Total Inmuebles"
          value={totalInmuebles}
          color="var(--color-accent)"
          dsIcon="🗂️" dsLabel="HashMap O(1)"
          delta="Búsqueda directa por código"
        />
        <KpiCard
          icon={<Users size={20} />}
          label="Clientes Activos"
          value={clientesActivos}
          color="var(--color-panel)"
          dsIcon="📋" dsLabel="ArrayList O(n)"
          delta="Con búsqueda en curso"
        />
        <KpiCard
          icon={<Calendar size={20} />}
          label="Visitas Pendientes"
          value={visitasPendientes}
          color="var(--color-warning)"
          dsIcon="📦" dsLabel="Cola FIFO"
          delta="En cola de atención"
        />
        <KpiCard
          icon={<AlertTriangle size={20} />}
          label="Alertas Críticas"
          value={alertasCriticas}
          color="var(--color-danger)"
          dsIcon="⚡" dsLabel="PriorityQueue"
          delta="Requieren atención inmediata"
        />
      </div>

      {/* Fila 2 — Cola de visitas + Pila + Zonas */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 260px 260px', gap: 16 }}>

        {/* Cola de visitas del día */}
        <div className="card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 14 }}>
            <div>
              <div className="section-title">Cola de Visitas del Día</div>
              <div className="section-subtitle">{visitasPendientesOrdenadas.length} en cola</div>
            </div>
            <DSBadge icon="📦" label="Cola FIFO — O(1)" />
          </div>
          {visitasPendientesOrdenadas.length === 0 ? (
            <div style={{ color: 'var(--color-text-muted)', fontSize: 13, textAlign: 'center', padding: '20px 0' }}>
              No hay visitas pendientes
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
              {visitasPendientesOrdenadas.map((v, i) => (
                <div key={v.idVisita}
                  style={{
                    display: 'flex', alignItems: 'center', gap: 12,
                    padding: '10px 12px',
                    background: i === 0 ? '#00a6c008' : 'var(--color-bg-light)',
                    borderRadius: 8,
                    border: i === 0 ? '1px solid #00a6c030' : '1px solid var(--color-sand)',
                  }}
                >
                  <div style={{
                    width: 24, height: 24, borderRadius: '50%',
                    background: i === 0 ? 'var(--color-accent)' : 'var(--color-sand)',
                    color: i === 0 ? '#fff' : 'var(--color-text-muted)',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    fontSize: 11, fontWeight: 700, flexShrink: 0,
                  }}>
                    {i + 1}
                  </div>
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--color-text)', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                      {v.idCliente}
                    </div>
                    <div style={{ fontSize: 11, color: 'var(--color-text-muted)' }}>
                      {v.codigoInmueble}
                    </div>
                  </div>
                  <div style={{ textAlign: 'right', flexShrink: 0 }}>
                    <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 11, color: 'var(--color-accent)' }}>
                      {v.hora ?? '—'}
                    </div>
                    <StatusBadge value={v.estado} />
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Pila de acciones */}
        <div className="card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 14 }}>
            <div>
              <div className="section-title">Pila de Acciones</div>
              <div className="section-subtitle">Historial LIFO</div>
            </div>
            <DSBadge icon="📚" label="Stack LIFO" />
          </div>

          {pilaAcciones.length === 0 ? (
            <div style={{ color: 'var(--color-text-muted)', fontSize: 12, marginBottom: 12 }}>
              Sin acciones en esta sesión
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 6, marginBottom: 12 }}>
              {pilaAcciones.map((a, i) => (
                <div key={i} style={{
                  padding: '8px 10px',
                  background: 'var(--color-bg-light)',
                  borderRadius: 7,
                  border: '1px solid var(--color-sand)',
                  opacity: 1 - i * 0.2,
                  fontSize: 12,
                }}>
                  <span style={{ fontWeight: 600 }}>{a.codigo}</span>{' '}
                  <span style={{ color: 'var(--color-text-muted)' }}>{a.accion}</span>
                  <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 10, color: 'var(--color-text-muted)' }}>
                    {a.ts}
                  </div>
                </div>
              ))}
            </div>
          )}

          {msgDeshacer && (
            <div style={{ fontSize: 12, color: 'var(--color-accent)', marginBottom: 8 }}>{msgDeshacer}</div>
          )}
          <button className="btn-outline" onClick={handleDeshacer} style={{ width: '100%', justifyContent: 'center' }}>
            <RotateCcw size={13} /> Deshacer última acción
          </button>
        </div>

        {/* Zonas más activas */}
        <div className="card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 14 }}>
            <div>
              <div className="section-title">Zonas más activas</div>
              <div className="section-subtitle">Por visitas recibidas</div>
            </div>
            <DSBadge icon="🌳" label="BST O(log n)" />
          </div>
          {zonasOrdenadas.length === 0 ? (
            <div style={{ color: 'var(--color-text-muted)', fontSize: 12 }}>Sin datos de visitas</div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
              {zonasOrdenadas.map(([zona, count]) => (
                <div key={zona}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 12, marginBottom: 4 }}>
                    <span style={{ fontWeight: 500 }}>{zona}</span>
                    <span style={{ fontFamily: "'JetBrains Mono', monospace", color: 'var(--color-accent)' }}>
                      {count} visitas
                    </span>
                  </div>
                  <div style={{ height: 6, background: 'var(--color-bg-light)', borderRadius: 3, overflow: 'hidden' }}>
                    <div style={{
                      height: '100%',
                      width: `${(count / maxVisitas) * 100}%`,
                      background: 'linear-gradient(90deg, var(--color-accent), var(--color-accent-dark))',
                      borderRadius: 3,
                    }} />
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Fila 3 — Alertas + Grafo */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 380px', gap: 16 }}>

        {/* Alertas recientes */}
        <div className="card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 14 }}>
            <div>
              <div className="section-title">Alertas del Sistema</div>
              <div className="section-subtitle">{alertas.length} alertas activas</div>
            </div>
            <DSBadge icon="⚡" label="PriorityQueue O(log n)" color="var(--color-danger)" />
          </div>
          {alertasRecientes.length === 0 ? (
            <div style={{ color: 'var(--color-text-muted)', fontSize: 13, padding: '20px 0', textAlign: 'center' }}>
              No hay alertas activas
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
              {alertasRecientes.map((a) => {
                const nivelColor = {
                  CRITICA: 'var(--color-danger)',
                  ALTA: '#f97316',
                  MEDIA: 'var(--color-warning)',
                  BAJA: 'var(--color-text-muted)',
                }[a.nivel] || 'var(--color-text-muted)'

                return (
                  <div key={a.idAlerta} style={{
                    display: 'flex', gap: 12, alignItems: 'flex-start',
                    padding: '10px 12px',
                    background: 'var(--color-bg-light)',
                    borderRadius: 8,
                    borderLeft: `3px solid ${nivelColor}`,
                  }}>
                    <div style={{ flex: 1, minWidth: 0 }}>
                      <div style={{ display: 'flex', gap: 8, alignItems: 'center', marginBottom: 3 }}>
                        <StatusBadge value={a.nivel} />
                        <DSBadge icon="⚡" label="PriorityQueue" color={nivelColor} />
                      </div>
                      <div style={{ fontSize: 12, color: 'var(--color-text)' }}>{a.descripcion}</div>
                    </div>
                    <div style={{
                      fontFamily: "'JetBrains Mono', monospace",
                      fontSize: 10, color: 'var(--color-text-muted)',
                      flexShrink: 0, textAlign: 'right',
                    }}>
                      {a.fecha ? new Date(a.fecha).toLocaleDateString('es-CO') : ''}
                    </div>
                  </div>
                )
              })}
            </div>
          )}
        </div>

        {/* Grafo */}
        <div className="card">
          <GrafoPanel clientes={clientes} inmuebles={inmuebles} />
        </div>
      </div>
    </div>
  )
}
