import { useEffect, useState } from 'react'
import { FileText, Plus } from 'lucide-react'
import DSBadge from '../components/ui/DSBadge'
import StatusBadge from '../components/ui/StatusBadge'
import Modal from '../components/ui/Modal'
import {
  getOperaciones, getOperacionesHistorial, cerrarOperacion,
  cancelarOperacion, createOperacion, getInmuebles, getClientes, getAsesores
} from '../api'

function formatPrecio(p) {
  if (!p) return '—'
  if (p >= 1_000_000) return `$${(p / 1_000_000).toFixed(1)}M`
  return `$${(p / 1000).toFixed(0)}K`
}

export default function Operaciones() {
  const [operaciones, setOperaciones] = useState([])
  const [historialOps, setHistorialOps] = useState([])
  const [vista, setVista] = useState('activas')
  const [loading, setLoading] = useState(true)
  const [msg, setMsg] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [inmuebles, setInmuebles] = useState([])
  const [clientes, setClientes] = useState([])
  const [asesores, setAsesores] = useState([])
  const [formData, setFormData] = useState({
    codigoInmueble: '', idCliente: '', idAsesor: '',
    tipo: 'VENTA', valorAcordado: '', comision: '',
    fecha: new Date().toISOString().split('T')[0],
  })

  const cargar = () => {
    setLoading(true)
    Promise.all([getOperaciones(), getOperacionesHistorial()])
      .then(([activas, hist]) => {
        setOperaciones(activas.data)
        setHistorialOps(hist.data)
      })
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    cargar()
    Promise.all([getInmuebles(), getClientes(), getAsesores()]).then(([i, c, a]) => {
      setInmuebles(i.data.filter((inm) => inm.disponible))
      setClientes(c.data)
      setAsesores(a.data)
    })
  }, [])

  const activas = operaciones.filter((op) => op.estado === 'EN_PROCESO')
  // getOperacionesHistorial devuelve las operaciones en orden de registro (ArrayList)
  const historial = historialOps.filter((op) => op.estado !== 'EN_PROCESO')
  const datos = vista === 'activas' ? activas : historial

  const mostrarMsg = (texto) => { setMsg(texto); setTimeout(() => setMsg(''), 4000) }

  const handleCerrar = async (id) => {
    try {
      await cerrarOperacion(id)
      mostrarMsg('✓ Operación cerrada exitosamente')
      cargar()
    } catch (e) {
      mostrarMsg(e.response?.data?.message || 'Error al cerrar operación')
    }
  }

  const handleCancelar = async (id) => {
    if (!confirm('¿Confirmar cancelación?')) return
    try {
      await cancelarOperacion(id)
      mostrarMsg('Operación cancelada.')
      cargar()
    } catch (e) {
      mostrarMsg(e.response?.data?.message || 'Error al cancelar operación')
    }
  }

  const handleCrear = async (e) => {
    e.preventDefault()
    if (!formData.codigoInmueble) { alert('Selecciona un inmueble.'); return }
    if (!formData.idCliente)      { alert('Selecciona un cliente.'); return }
    if (!formData.idAsesor)       { alert('Selecciona un asesor.'); return }
    if (!formData.valorAcordado)  { alert('Ingresa el valor acordado.'); return }
    try {
      await createOperacion({
        ...formData,
        valorAcordado: Number(formData.valorAcordado),
        comision: Number(formData.comision) || 0,
      })
      setModalOpen(false)
      setFormData({
        codigoInmueble: '', idCliente: '', idAsesor: '',
        tipo: 'VENTA', valorAcordado: '', comision: '',
        fecha: new Date().toISOString().split('T')[0],
      })
      cargar()
    } catch (err) {
      alert(err.response?.data?.message || err.response?.data?.error || 'Error al registrar operación')
    }
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
      {msg && <div style={{ background: '#e6f7f2', color: '#1a9e6e', border: '1px solid #1a9e6e40', borderRadius: 8, padding: '10px 16px', fontSize: 13 }}>{msg}</div>}

      {/* Tabs y acciones */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ display: 'flex', gap: 8 }}>
          <button className={`pill ${vista === 'activas' ? 'active' : ''}`} onClick={() => setVista('activas')}>
            Activas ({activas.length})
          </button>
          <button className={`pill ${vista === 'historial' ? 'active' : ''}`} onClick={() => setVista('historial')}>
            Historial ({historial.length})
          </button>
        </div>
        <div style={{ display: 'flex', gap: 10 }}>
          <DSBadge icon="🗂️" label="HashMap O(1)" />
          <button className="btn-primary" onClick={() => setModalOpen(true)}>
            <Plus size={14} /> Nueva operación
          </button>
        </div>
      </div>

      {/* Tabla */}
      {loading ? (
        <div className="loading-center"><div className="spinner" /></div>
      ) : datos.length === 0 ? (
        <div className="empty-state"><FileText size={40} /><div>No hay operaciones {vista === 'activas' ? 'activas' : 'en el historial'}.</div></div>
      ) : (
        <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
          <table className="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Inmueble</th>
                <th>Cliente</th>
                <th>Tipo</th>
                <th>Valor</th>
                <th>Comisión</th>
                <th>Fecha</th>
                <th>Estado</th>
                {vista === 'activas' && <th>Acciones</th>}
              </tr>
            </thead>
            <tbody>
              {datos.map((op) => (
                <tr key={op.idOperacion}>
                  <td><span className="mono" style={{ fontSize: 11, color: 'var(--color-accent)' }}>{op.idOperacion.slice(0, 8)}…</span></td>
                  <td style={{ fontWeight: 600, fontSize: 13 }}>{op.codigoInmueble}</td>
                  <td style={{ fontSize: 13 }}>{op.idCliente}</td>
                  <td><StatusBadge value={op.tipo} /></td>
                  <td style={{ fontWeight: 600, fontSize: 13, color: 'var(--color-text-mid)' }}>{formatPrecio(op.valorAcordado)}</td>
                  <td style={{ fontSize: 12, color: 'var(--color-text-muted)' }}>{formatPrecio(op.comision)}</td>
                  <td><span className="mono" style={{ fontSize: 11 }}>{op.fecha}</span></td>
                  <td><StatusBadge value={op.estado} /></td>
                  {vista === 'activas' && (
                    <td>
                      <div style={{ display: 'flex', gap: 6 }}>
                        {op.estado === 'EN_PROCESO' && (
                          <>
                            <button className="btn-ghost" style={{ fontSize: 11, color: 'var(--color-success)' }} onClick={() => handleCerrar(op.idOperacion)}>
                              ✓ Cerrar
                            </button>
                            <button className="btn-ghost" style={{ fontSize: 11 }} onClick={() => handleCancelar(op.idOperacion)}>
                              Cancelar
                            </button>
                          </>
                        )}
                      </div>
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Modal crear */}
      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Registrar Operación">
        <form onSubmit={handleCrear}>
          <div className="field-group">
            <label className="field-label">Inmueble disponible</label>
            <select className="field-input" required value={formData.codigoInmueble}
              onChange={(e) => setFormData({ ...formData, codigoInmueble: e.target.value })}>
              <option value="">Seleccionar…</option>
              {inmuebles.map((i) => <option key={i.codigo} value={i.codigo}>{i.codigo} — {i.finalidad}, {i.ciudad}</option>)}
            </select>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 12px' }}>
            <div className="field-group">
              <label className="field-label">Cliente</label>
              <select className="field-input" required value={formData.idCliente}
                onChange={(e) => setFormData({ ...formData, idCliente: e.target.value })}>
                <option value="">Seleccionar…</option>
                {clientes.map((c) => <option key={c.identificacion} value={c.identificacion}>{c.nombre}</option>)}
              </select>
            </div>
            <div className="field-group">
              <label className="field-label">Asesor</label>
              <select className="field-input" required value={formData.idAsesor}
                onChange={(e) => setFormData({ ...formData, idAsesor: e.target.value })}>
                <option value="">Seleccionar…</option>
                {asesores.map((a) => <option key={a.identificacion} value={a.identificacion}>{a.nombre}</option>)}
              </select>
            </div>
            <div className="field-group">
              <label className="field-label">Tipo de operación</label>
              <select className="field-input" value={formData.tipo}
                onChange={(e) => setFormData({ ...formData, tipo: e.target.value })}>
                {['VENTA', 'ARRIENDO', 'RENOVACION'].map((t) => <option key={t} value={t}>{t}</option>)}
              </select>
            </div>
            <div className="field-group">
              <label className="field-label">Fecha</label>
              <input type="date" className="field-input" value={formData.fecha}
                onChange={(e) => setFormData({ ...formData, fecha: e.target.value })} />
            </div>
            <div className="field-group">
              <label className="field-label">Valor acordado</label>
              <input type="number" className="field-input" min={0} value={formData.valorAcordado}
                onChange={(e) => setFormData({ ...formData, valorAcordado: e.target.value })} />
            </div>
            <div className="field-group">
              <label className="field-label">Comisión</label>
              <input type="number" className="field-input" min={0} value={formData.comision}
                onChange={(e) => setFormData({ ...formData, comision: e.target.value })} />
            </div>
          </div>
          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 10, marginTop: 8 }}>
            <button type="button" className="btn-ghost" onClick={() => setModalOpen(false)}>Cancelar</button>
            <button type="submit" className="btn-primary">Registrar</button>
          </div>
        </form>
      </Modal>
    </div>
  )
}
