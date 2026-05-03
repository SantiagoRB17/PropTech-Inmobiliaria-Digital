import { useEffect, useState } from 'react'
import { Calendar, ArrowRight, Zap, Plus } from 'lucide-react'
import DSBadge from '../components/ui/DSBadge'
import StatusBadge from '../components/ui/StatusBadge'
import Modal from '../components/ui/Modal'
import {
  getVisitas, getVisita, getVisitasPrioritarias, atenderSiguienteVisita,
  despacharVisitaPrioritaria, createVisita, actualizarEstadoVisita,
  getClientes, getInmuebles, getAsesores,
} from '../api'

const ESTADOS_VISITA = ['PENDIENTE', 'CONFIRMADA', 'REALIZADA', 'CANCELADA', 'REPROGRAMADA']
const ESTADOS_TERMINALES = ['REALIZADA', 'CANCELADA']

export default function Visitas() {
  const [visitas, setVisitas] = useState([])
  const [prioritarias, setPrioritarias] = useState([])
  const [loading, setLoading] = useState(true)
  const [msg, setMsg] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [clientes, setClientes] = useState([])
  const [inmuebles, setInmuebles] = useState([])
  const [asesores, setAsesores] = useState([])
  const [formData, setFormData] = useState({
    codigoInmueble: '', idCliente: '', idAsesor: '',
    fecha: '', hora: '', observaciones: '',
  })

  const cargar = () => {
    setLoading(true)
    Promise.all([getVisitas(), getVisitasPrioritarias()])
      .then(([v, p]) => {
        setVisitas(v.data)
        setPrioritarias(p.data)
      })
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    cargar()
    Promise.all([getClientes(), getInmuebles(), getAsesores()]).then(([c, i, a]) => {
      setClientes(c.data)
      setInmuebles(i.data.filter((inm) => inm.disponible))
      setAsesores(a.data)
    })
  }, [])

  const mostrarMsg = (texto) => {
    setMsg(texto)
    setTimeout(() => setMsg(''), 3000)
  }

  const handleAtender = async () => {
    try {
      const r = await atenderSiguienteVisita()
      mostrarMsg(`✓ Visita ${r.data.idVisita} movida a cola de prioridad`)
      cargar()
    } catch (e) {
      mostrarMsg(e.response?.data?.message || 'No hay solicitudes pendientes en la cola.')
    }
  }

  const handleDespachar = async () => {
    try {
      const r = await despacharVisitaPrioritaria()
      mostrarMsg(`✓ Visita ${r.data.idVisita} despachada y confirmada`)
      cargar()
    } catch (e) {
      mostrarMsg(e.response?.data?.message || 'No hay visitas en la cola de prioridad.')
    }
  }

  // Cambia el estado de una visita y recarga solo esa fila usando getVisita
  const handleCambiarEstado = async (idVisita, nuevoEstado) => {
    try {
      await actualizarEstadoVisita(idVisita, nuevoEstado)
      const r = await getVisita(idVisita)
      setVisitas((prev) => prev.map((v) => v.idVisita === idVisita ? r.data : v))
    } catch (e) {
      mostrarMsg(e.response?.data?.message || 'Error al actualizar estado')
    }
  }

  const handleCrear = async (e) => {
    e.preventDefault()
    if (!formData.codigoInmueble) { alert('Selecciona un inmueble.'); return }
    if (!formData.idCliente)      { alert('Selecciona un cliente.'); return }
    if (!formData.idAsesor)       { alert('Selecciona un asesor.'); return }
    if (!formData.fecha)          { alert('Indica la fecha.'); return }
    if (!formData.hora)           { alert('Indica la hora.'); return }
    // <input type="time"> devuelve "HH:MM" sin segundos; Jackson necesita "HH:MM:SS"
    const hora = formData.hora.length === 5 ? formData.hora + ':00' : formData.hora
    try {
      await createVisita({ ...formData, hora })
      setModalOpen(false)
      setFormData({ codigoInmueble: '', idCliente: '', idAsesor: '', fecha: '', hora: '', observaciones: '' })
      cargar()
    } catch (err) {
      alert(err.response?.data?.message || err.response?.data?.error || 'Error al agendar visita')
    }
  }

  const pendientes = visitas.filter((v) => v.estado === 'PENDIENTE')

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>

      {msg && <div style={{ background: '#e6f7f2', color: '#1a9e6e', border: '1px solid #1a9e6e40', borderRadius: 8, padding: '10px 16px', fontSize: 13 }}>{msg}</div>}

      {/* Controles de colas */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
        <div className="card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 14 }}>
            <div>
              <div className="section-title">Cola de Atención (FIFO)</div>
              <div className="section-subtitle">{pendientes.length} solicitudes pendientes</div>
            </div>
            <DSBadge icon="📦" label="Cola FIFO — O(1)" />
          </div>
          <p style={{ fontSize: 13, color: 'var(--color-text-muted)', marginBottom: 14, lineHeight: 1.6 }}>
            Las visitas se atienden en orden de llegada. Al atender la siguiente, pasa a la cola de prioridad.
          </p>
          <button className="btn-primary" onClick={handleAtender}>
            <ArrowRight size={14} /> Atender siguiente (FIFO)
          </button>
        </div>

        <div className="card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 14 }}>
            <div>
              <div className="section-title">Cola de Prioridad (Max-Heap)</div>
              <div className="section-subtitle">{prioritarias.length} visitas en espera</div>
            </div>
            <DSBadge icon="⚡" label="Max-Heap O(log n)" color="var(--color-warning)" />
          </div>
          <p style={{ fontSize: 13, color: 'var(--color-text-muted)', marginBottom: 14, lineHeight: 1.6 }}>
            El Max-Heap garantiza que los clientes VIP y con búsqueda activa sean despachados primero.
          </p>
          <button className="btn-primary" onClick={handleDespachar} style={{ background: 'linear-gradient(90deg, var(--color-warning), #b37008)' }}>
            <Zap size={14} /> Despachar prioritaria (Heap)
          </button>
        </div>
      </div>

      {/* Tabla de visitas */}
      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '16px 20px', borderBottom: '1px solid var(--color-sand)' }}>
          <div className="section-title" style={{ marginBottom: 0 }}>Todas las visitas ({visitas.length})</div>
          <div style={{ display: 'flex', gap: 10 }}>
            <DSBadge icon="🗂️" label="HashMap O(1)" />
            <button className="btn-primary" onClick={() => setModalOpen(true)}>
              <Plus size={14} /> Agendar visita
            </button>
          </div>
        </div>

        {loading ? (
          <div className="loading-center"><div className="spinner" /></div>
        ) : visitas.length === 0 ? (
          <div className="empty-state"><Calendar size={40} /><div>No hay visitas registradas.</div></div>
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Inmueble</th>
                <th>Cliente</th>
                <th>Asesor</th>
                <th>Fecha</th>
                <th>Hora</th>
                <th>Prioridad</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              {visitas.map((v) => (
                <tr key={v.idVisita}>
                  <td><span className="mono" style={{ fontSize: 11, color: 'var(--color-accent)' }}>{v.idVisita}</span></td>
                  <td style={{ fontSize: 13, fontWeight: 600 }}>{v.codigoInmueble}</td>
                  <td style={{ fontSize: 13 }}>{v.idCliente}</td>
                  <td style={{ fontSize: 12, color: 'var(--color-text-muted)' }}>{v.idAsesor}</td>
                  <td><span className="mono" style={{ fontSize: 11 }}>{v.fecha}</span></td>
                  <td><span className="mono" style={{ fontSize: 11 }}>{v.hora}</span></td>
                  <td>
                    <span style={{
                      display: 'inline-flex', alignItems: 'center',
                      padding: '2px 8px', borderRadius: 20,
                      background: '#00a6c018', color: 'var(--color-accent)',
                      fontFamily: "'JetBrains Mono', monospace", fontSize: 11, fontWeight: 600,
                    }}>
                      P{v.prioridad}
                    </span>
                  </td>
                  <td>
                    {ESTADOS_TERMINALES.includes(v.estado) ? (
                      <StatusBadge value={v.estado} />
                    ) : (
                      <select
                        className="field-input"
                        style={{ fontSize: 11, padding: '3px 6px', width: 'auto', minWidth: 130 }}
                        value={v.estado}
                        onChange={(e) => handleCambiarEstado(v.idVisita, e.target.value)}
                      >
                        {ESTADOS_VISITA.filter((s) => !ESTADOS_TERMINALES.includes(s) || s === v.estado).map((s) => (
                          <option key={s} value={s}>{s}</option>
                        ))}
                        {ESTADOS_TERMINALES.map((s) => (
                          <option key={s} value={s}>{s}</option>
                        ))}
                      </select>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Modal agendar visita */}
      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Agendar Visita">
        <form onSubmit={handleCrear}>
          <div className="field-group">
            <label className="field-label">Inmueble</label>
            <select className="field-input" required value={formData.codigoInmueble}
              onChange={(e) => setFormData({ ...formData, codigoInmueble: e.target.value })}>
              <option value="">Seleccionar inmueble…</option>
              {inmuebles.map((i) => <option key={i.codigo} value={i.codigo}>{i.codigo} — {i.barrio}, {i.ciudad}</option>)}
            </select>
          </div>
          <div className="field-group">
            <label className="field-label">Cliente</label>
            <select className="field-input" required value={formData.idCliente}
              onChange={(e) => setFormData({ ...formData, idCliente: e.target.value })}>
              <option value="">Seleccionar cliente…</option>
              {clientes.map((c) => <option key={c.identificacion} value={c.identificacion}>{c.nombre} ({c.tipoCliente})</option>)}
            </select>
          </div>
          <div className="field-group">
            <label className="field-label">Asesor</label>
            <select className="field-input" required value={formData.idAsesor}
              onChange={(e) => setFormData({ ...formData, idAsesor: e.target.value })}>
              <option value="">Seleccionar asesor…</option>
              {asesores.map((a) => <option key={a.identificacion} value={a.identificacion}>{a.nombre}</option>)}
            </select>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 12px' }}>
            <div className="field-group">
              <label className="field-label">Fecha</label>
              <input type="date" className="field-input" required value={formData.fecha}
                onChange={(e) => setFormData({ ...formData, fecha: e.target.value })} />
            </div>
            <div className="field-group">
              <label className="field-label">Hora</label>
              <input type="time" className="field-input" required value={formData.hora}
                onChange={(e) => setFormData({ ...formData, hora: e.target.value })} />
            </div>
          </div>
          <div className="field-group">
            <label className="field-label">Observaciones</label>
            <textarea className="field-input" rows={3} value={formData.observaciones}
              onChange={(e) => setFormData({ ...formData, observaciones: e.target.value })} />
          </div>
          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 10 }}>
            <button type="button" className="btn-ghost" onClick={() => setModalOpen(false)}>Cancelar</button>
            <button type="submit" className="btn-primary">Agendar</button>
          </div>
        </form>
      </Modal>
    </div>
  )
}
