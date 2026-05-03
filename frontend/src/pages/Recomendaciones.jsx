import { useEffect, useState } from 'react'
import { Network, Building2, Bed, Bath, Square, SlidersHorizontal } from 'lucide-react'
import DSBadge from '../components/ui/DSBadge'
import StatusBadge from '../components/ui/StatusBadge'
import GrafoPanel from '../components/ui/GrafoPanel'
import { getClientes, getInmuebles, getRecomendaciones, buscarCompatibles, getInteraccionesCliente } from '../api'

function formatPrecio(p) {
  if (!p) return '—'
  if (p >= 1_000_000) return `$${(p / 1_000_000).toFixed(1)}M`
  return `$${(p / 1000).toFixed(0)}K`
}

function CardInmueble({ inm }) {
  const icono = { CASA: '🏠', APARTAMENTO: '🏢', OFICINA: '🏛️', LOCAL_COMERCIAL: '🏪', BODEGA: '🏭', LOTE: '🌿' }[inm.tipo] || '🏠'
  return (
    <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
      <div style={{ height: 90, background: 'var(--color-bg-light)', display: 'flex', alignItems: 'center', justifyContent: 'center', position: 'relative' }}>
        <span style={{ fontSize: 30 }}>{icono}</span>
        <div style={{ position: 'absolute', top: 6, left: 6 }}>
          <StatusBadge value={inm.finalidad} />
        </div>
        <div style={{ position: 'absolute', bottom: 4, left: 6 }}>
          <span className="mono" style={{ fontSize: 9, color: 'var(--color-text-muted)', background: 'rgba(255,255,255,0.85)', padding: '1px 5px', borderRadius: 3 }}>
            {inm.codigo}
          </span>
        </div>
      </div>
      <div style={{ padding: '10px 12px' }}>
        <div style={{ fontSize: 13, fontWeight: 700, marginBottom: 2 }}>{inm.tipo?.replace('_', ' ')}</div>
        <div style={{ fontSize: 11, color: 'var(--color-text-muted)', marginBottom: 6 }}>{inm.barrio}, {inm.ciudad}</div>
        <div style={{ fontSize: 15, fontWeight: 800, color: 'var(--color-accent)', marginBottom: 6 }}>
          {formatPrecio(inm.precio)}
        </div>
        <div style={{ display: 'flex', gap: 10, fontSize: 11, color: 'var(--color-text-mid)' }}>
          {inm.habitaciones > 0 && <span style={{ display: 'flex', alignItems: 'center', gap: 3 }}><Bed size={11} />{inm.habitaciones}</span>}
          {inm.banos > 0 && <span style={{ display: 'flex', alignItems: 'center', gap: 3 }}><Bath size={11} />{inm.banos}</span>}
          {inm.area > 0 && <span style={{ display: 'flex', alignItems: 'center', gap: 3 }}><Square size={11} />{inm.area}m²</span>}
        </div>
      </div>
    </div>
  )
}

export default function Recomendaciones() {
  const [clientes, setClientes]       = useState([])
  const [inmuebles, setInmuebles]     = useState([])
  const [clienteId, setClienteId]     = useState('')
  const [recomendados, setRecomendados] = useState([])
  const [compatibles, setCompatibles]   = useState([])
  const [interacciones, setInteracciones] = useState([])
  const [loading, setLoading]         = useState(false)
  const [buscado, setBuscado]         = useState(false)

  useEffect(() => {
    Promise.all([getClientes(), getInmuebles()]).then(([c, i]) => {
      setClientes(c.data)
      setInmuebles(i.data)
    })
  }, [])

  const buscar = async () => {
    if (!clienteId) return
    setLoading(true)
    setBuscado(true)
    try {
      const [recom, comp, inter] = await Promise.all([
        getRecomendaciones(clienteId, 6),
        buscarCompatibles(clienteId),
        getInteraccionesCliente(clienteId),
      ])
      setRecomendados(recom.data)
      setCompatibles(comp.data)
      setInteracciones(inter.data)
    } catch {
      setRecomendados([])
      setCompatibles([])
      setInteracciones([])
    } finally {
      setLoading(false)
    }
  }

  const clienteSeleccionado = clientes.find((c) => c.identificacion === clienteId)

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>

      {/* Selector de cliente */}
      <div className="card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 16 }}>
          <div>
            <div className="section-title">Motor de Recomendaciones</div>
            <div className="section-subtitle">BFS sobre el grafo + compatibilidad por perfil</div>
          </div>
          <div style={{ display: 'flex', gap: 8 }}>
            <DSBadge icon="🔗" label="Grafo BFS — O(V+E)" />
            <DSBadge icon="🔍" label="Filtro perfil — O(n)" />
          </div>
        </div>

        <div style={{ display: 'flex', gap: 12, alignItems: 'flex-end' }}>
          <div className="field-group" style={{ flex: 1, marginBottom: 0 }}>
            <label className="field-label">Seleccionar cliente</label>
            <select className="field-input" value={clienteId} onChange={(e) => setClienteId(e.target.value)}>
              <option value="">Seleccionar…</option>
              {clientes.map((c) => (
                <option key={c.identificacion} value={c.identificacion}>
                  {c.nombre} ({c.tipoCliente})
                </option>
              ))}
            </select>
          </div>
          <button className="btn-primary" onClick={buscar} disabled={!clienteId} style={{ height: 38 }}>
            <Network size={14} /> Generar recomendaciones
          </button>
        </div>

        {clienteSeleccionado && (
          <div style={{ marginTop: 12, padding: '10px 14px', background: 'var(--color-bg-light)', borderRadius: 8, fontSize: 13, display: 'flex', gap: 20, flexWrap: 'wrap', alignItems: 'center' }}>
            <span><strong>Presupuesto:</strong> {formatPrecio(clienteSeleccionado.presupuesto)}</span>
            <span><strong>Tipo deseado:</strong> {clienteSeleccionado.tipoDeseado?.replace('_', ' ') || '—'}</span>
            <span><strong>Mín. habitaciones:</strong> {clienteSeleccionado.habitacionesMin ?? 0}</span>
            <span><strong>Consultas previas:</strong> {clienteSeleccionado.inmueblesConsultados?.length ?? 0}</span>
            <StatusBadge value={clienteSeleccionado.tipoCliente} />
            <StatusBadge value={clienteSeleccionado.estadoBusqueda} />
          </div>
        )}
      </div>

      {loading ? (
        <div className="loading-center"><div className="spinner" /> Ejecutando algoritmos…</div>
      ) : buscado && (
        <>
          {/* Sección 1: BFS — Por historial de interacciones */}
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 14 }}>
              <div style={{ fontWeight: 700, fontSize: 14 }}>
                Por historial de interacciones — BFS
              </div>
              <DSBadge icon="🔗" label="Grafo O(V+E)" />
            </div>
            <p style={{ fontSize: 12, color: 'var(--color-text-muted)', marginBottom: 14, lineHeight: 1.6 }}>
              Inmuebles que otros clientes con intereses similares han consultado, usando BFS a 2 saltos en el grafo.
              Se excluyen los inmuebles ya descartados por el cliente.
            </p>
            {recomendados.length === 0 ? (
              <div className="empty-state" style={{ padding: '20px 0' }}>
                <Network size={32} />
                <div>Sin recomendaciones por historial. El cliente necesita más interacciones en el grafo.</div>
              </div>
            ) : (
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: 12 }}>
                {recomendados.map((inm) => <CardInmueble key={inm.codigo} inm={inm} />)}
              </div>
            )}
          </div>

          {/* Sección 2: Por perfil del cliente */}
          <div style={{ borderTop: '1px solid var(--color-sand)', paddingTop: 20 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 14 }}>
              <div style={{ fontWeight: 700, fontSize: 14 }}>
                Por perfil del cliente — Filtro de compatibilidad
              </div>
              <DSBadge icon="🔍" label="Filtro O(n)" />
            </div>
            <p style={{ fontSize: 12, color: 'var(--color-text-muted)', marginBottom: 14, lineHeight: 1.6 }}>
              Inmuebles disponibles que cumplen: precio ≤ presupuesto · tipo deseado · habitaciones ≥ mínimo · ciudad en zonas de interés.
            </p>
            {compatibles.length === 0 ? (
              <div className="empty-state" style={{ padding: '20px 0' }}>
                <SlidersHorizontal size={32} />
                <div>Ningún inmueble disponible cumple el perfil del cliente.</div>
              </div>
            ) : (
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: 12 }}>
                {compatibles.map((inm) => <CardInmueble key={inm.codigo} inm={inm} />)}
              </div>
            )}
          </div>
        </>
      )}

      {/* Sección 3: Interacciones registradas en el grafo */}
      {buscado && !loading && (
        <div style={{ borderTop: '1px solid var(--color-sand)', paddingTop: 20 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 10 }}>
            <div style={{ fontWeight: 700, fontSize: 14 }}>Interacciones registradas en el grafo</div>
            <DSBadge icon="🔗" label="Grafo — vecinos O(1)" />
          </div>
          <p style={{ fontSize: 12, color: 'var(--color-text-muted)', marginBottom: 10, lineHeight: 1.6 }}>
            Códigos de inmuebles con los que este cliente tiene arista en el GrafoClienteInmueble
            (consultas, favoritos, negociaciones).
          </p>
          {interacciones.length === 0 ? (
            <div style={{ fontSize: 13, color: 'var(--color-text-muted)' }}>
              Sin interacciones registradas en el grafo.
            </div>
          ) : (
            <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
              {interacciones.map((cod) => (
                <span key={cod} className="mono" style={{
                  fontSize: 11, padding: '3px 8px', borderRadius: 5,
                  background: '#00a6c012', color: 'var(--color-accent)',
                  border: '1px solid #00a6c030',
                }}>
                  {cod}
                </span>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Grafo visual */}
      <div className="card">
        <GrafoPanel clientes={clientes} inmuebles={inmuebles} />
      </div>
    </div>
  )
}
