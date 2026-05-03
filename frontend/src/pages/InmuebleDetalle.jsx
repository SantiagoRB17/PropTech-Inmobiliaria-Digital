import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { ArrowLeft, Building2, Bed, Bath, Square, MapPin, User, Pencil, ClipboardList, Trash2 } from 'lucide-react'
import StatusBadge from '../components/ui/StatusBadge'
import DSBadge from '../components/ui/DSBadge'
import Modal from '../components/ui/Modal'
import { getInmueble, cambiarDisponibilidad, updateInmueble, deleteInmueble, getClientes, registrarConsulta } from '../api'

const TIPOS = ['CASA', 'APARTAMENTO', 'LOCAL_COMERCIAL', 'OFICINA', 'LOTE', 'BODEGA']
const FINALIDADES = ['VENTA', 'ARRIENDO']
const ESTADOS = ['NUEVO', 'USADO', 'EN_CONSTRUCCION', 'REMODELADO']

function formatPrecio(p) {
  if (!p) return '—'
  if (p >= 1_000_000) return `$${(p / 1_000_000).toFixed(1)} millones`
  return `$${(p / 1000).toFixed(0)}K`
}

export default function InmuebleDetalle() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [inmueble, setInmueble] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [editOpen, setEditOpen] = useState(false)
  const [editForm, setEditForm] = useState({})
  const [editMsg, setEditMsg] = useState('')
  const [consultaOpen, setConsultaOpen] = useState(false)
  const [clientes, setClientes] = useState([])
  const [consultaClienteId, setConsultaClienteId] = useState('')
  const [consultaMsg, setConsultaMsg] = useState('')

  const cargar = () => {
    setLoading(true)
    getInmueble(id)
      .then((r) => {
        setInmueble(r.data)
        setEditForm({
          direccion: r.data.direccion || '',
          ciudad: r.data.ciudad || '',
          barrio: r.data.barrio || '',
          tipo: r.data.tipo || 'APARTAMENTO',
          finalidad: r.data.finalidad || 'VENTA',
          precio: r.data.precio ?? '',
          area: r.data.area ?? '',
          habitaciones: r.data.habitaciones ?? '',
          banos: r.data.banos ?? '',
          estado: r.data.estado || 'NUEVO',
        })
      })
      .catch(() => setError('Inmueble no encontrado.'))
      .finally(() => setLoading(false))
  }

  useEffect(() => { cargar() }, [id])

  const abrirConsulta = () => {
    getClientes().then((r) => {
      setClientes(r.data)
      setConsultaClienteId(r.data[0]?.identificacion || '')
      setConsultaOpen(true)
    }).catch(() => alert('Error al cargar clientes'))
  }

  const handleRegistrarConsulta = async (e) => {
    e.preventDefault()
    if (!consultaClienteId) { alert('Selecciona un cliente'); return }
    try {
      await registrarConsulta(consultaClienteId, id)
      setConsultaOpen(false)
      setConsultaMsg(`✓ Consulta registrada para ${clientes.find(c => c.identificacion === consultaClienteId)?.nombre || consultaClienteId}`)
      setTimeout(() => setConsultaMsg(''), 4000)
    } catch (err) {
      alert(err.response?.data?.message || err.response?.data?.error || 'Error al registrar consulta')
    }
  }

  const toggleDisponibilidad = async () => {
    try {
      await cambiarDisponibilidad(id, !inmueble.disponible)
      setInmueble({ ...inmueble, disponible: !inmueble.disponible })
    } catch {
      alert('Error al cambiar disponibilidad')
    }
  }

  const handleEliminar = async () => {
    if (!confirm(`¿Eliminar el inmueble ${id}? Esta acción no se puede deshacer.`)) return
    try {
      await deleteInmueble(id)
      navigate('/inmuebles')
    } catch (err) {
      alert(err.response?.data?.message || 'Error al eliminar el inmueble')
    }
  }

  const handleGuardarEdicion = async (e) => {
    e.preventDefault()
    try {
      await updateInmueble(id, {
        ...inmueble,
        ...editForm,
        precio: Number(editForm.precio),
        area: Number(editForm.area),
        habitaciones: Number(editForm.habitaciones),
        banos: Number(editForm.banos),
      })
      setEditOpen(false)
      setEditMsg('✓ Cambios guardados')
      cargar()
      setTimeout(() => setEditMsg(''), 3000)
    } catch (err) {
      alert(err.response?.data?.message || err.response?.data?.error || 'Error al actualizar el inmueble')
    }
  }

  if (loading) return <div className="loading-center"><div className="spinner" /> Cargando…</div>
  if (error) return <div className="error-banner">{error}</div>
  if (!inmueble) return null

  const iconoTipo = {
    CASA: '🏠', APARTAMENTO: '🏢', OFICINA: '🏛️',
    LOCAL_COMERCIAL: '🏪', BODEGA: '🏭', LOTE: '🌿',
  }[inmueble.tipo] || '🏠'

  return (
    <div style={{ maxWidth: 700 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <button className="btn-ghost" onClick={() => navigate(-1)}>
          <ArrowLeft size={14} /> Volver
        </button>
        <div style={{ display: 'flex', gap: 8 }}>
          <button className="btn-outline" onClick={abrirConsulta}>
            <ClipboardList size={13} /> Registrar consulta
          </button>
          <button className="btn-outline" onClick={() => setEditOpen(true)}>
            <Pencil size={13} /> Editar inmueble
          </button>
          <button className="btn-ghost" style={{ color: 'var(--color-danger)' }} onClick={handleEliminar}>
            <Trash2 size={13} /> Eliminar
          </button>
        </div>
      </div>

      {editMsg && (
        <div style={{ background: '#e6f7f2', color: '#1a9e6e', border: '1px solid #1a9e6e40', borderRadius: 8, padding: '10px 16px', fontSize: 13, marginBottom: 16 }}>
          {editMsg}
        </div>
      )}
      {consultaMsg && (
        <div style={{ background: '#e6f7f2', color: '#1a9e6e', border: '1px solid #1a9e6e40', borderRadius: 8, padding: '10px 16px', fontSize: 13, marginBottom: 16 }}>
          {consultaMsg}
        </div>
      )}

      <div className="card" style={{ marginBottom: 16 }}>
        {/* Encabezado visual */}
        <div style={{ height: 200, background: 'var(--color-bg-light)', borderRadius: 8, display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: 20, position: 'relative' }}>
          <span style={{ fontSize: 64 }}>{iconoTipo}</span>
          <div style={{ position: 'absolute', top: 12, left: 12, display: 'flex', gap: 8 }}>
            <StatusBadge value={inmueble.disponible ? 'DISPONIBLE' : 'NO_DISPONIBLE'} />
            <StatusBadge value={inmueble.finalidad} />
          </div>
          <div style={{ position: 'absolute', bottom: 12, left: 12 }}>
            <span className="mono" style={{ fontSize: 12, color: 'var(--color-text-muted)', background: 'rgba(255,255,255,0.9)', padding: '3px 8px', borderRadius: 5 }}>
              {inmueble.codigo}
            </span>
          </div>
        </div>

        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: 12 }}>
          <div>
            <h2 style={{ fontSize: 20, fontWeight: 800, marginBottom: 4 }}>
              {inmueble.tipo?.replace('_', ' ')} en {inmueble.finalidad?.toLowerCase()}
            </h2>
            <div style={{ display: 'flex', alignItems: 'center', gap: 6, color: 'var(--color-text-muted)', fontSize: 14 }}>
              <MapPin size={14} /> {inmueble.direccion}, {inmueble.barrio}, {inmueble.ciudad}
            </div>
          </div>
          <div style={{ textAlign: 'right' }}>
            <div style={{ fontSize: 26, fontWeight: 800, color: 'var(--color-accent)' }}>
              {formatPrecio(inmueble.precio)}
            </div>
            <div style={{ fontSize: 12, color: 'var(--color-text-muted)' }}>
              {inmueble.finalidad === 'ARRIENDO' ? '/ mes' : 'precio venta'}
            </div>
          </div>
        </div>
      </div>

      {/* Detalles */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
        <div className="card">
          <div className="section-title" style={{ marginBottom: 14 }}>Características</div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            {inmueble.habitaciones > 0 && (
              <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                <Bed size={16} color="var(--color-text-muted)" />
                <span style={{ fontSize: 13 }}>{inmueble.habitaciones} habitaciones</span>
              </div>
            )}
            {inmueble.banos > 0 && (
              <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                <Bath size={16} color="var(--color-text-muted)" />
                <span style={{ fontSize: 13 }}>{inmueble.banos} baños</span>
              </div>
            )}
            {inmueble.area > 0 && (
              <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                <Square size={16} color="var(--color-text-muted)" />
                <span style={{ fontSize: 13 }}>{inmueble.area} m²</span>
              </div>
            )}
            <div style={{ display: 'flex', gap: 8, marginTop: 4 }}>
              <StatusBadge value={inmueble.estado} />
            </div>
          </div>
        </div>

        <div className="card">
          <div className="section-title" style={{ marginBottom: 14 }}>Gestión</div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
              <User size={16} color="var(--color-text-muted)" />
              <span style={{ fontSize: 13 }}>Asesor: <strong>{inmueble.codigoAsesor}</strong></span>
            </div>
            <DSBadge icon="🗂️" label="HashMap O(1)" />
            <button
              className={inmueble.disponible ? 'btn-outline' : 'btn-primary'}
              onClick={toggleDisponibilidad}
              style={{ marginTop: 8 }}
            >
              <Building2 size={13} />
              {inmueble.disponible ? 'Marcar no disponible' : 'Marcar disponible'}
            </button>
          </div>
        </div>
      </div>

      {/* Modal registrar consulta */}
      <Modal open={consultaOpen} onClose={() => setConsultaOpen(false)} title={`Registrar consulta — ${inmueble.codigo}`}>
        <p style={{ fontSize: 13, color: 'var(--color-text-muted)', marginBottom: 16 }}>
          Asocia la consulta de un cliente a este inmueble. Esto crea una arista en el grafo de interacciones
          y actualiza el historial del cliente.
        </p>
        <form onSubmit={handleRegistrarConsulta}>
          <div className="field-group">
            <label className="field-label">Cliente</label>
            <select
              className="field-input"
              value={consultaClienteId}
              onChange={(e) => setConsultaClienteId(e.target.value)}
            >
              {clientes.map((c) => (
                <option key={c.identificacion} value={c.identificacion}>
                  {c.nombre} ({c.identificacion})
                </option>
              ))}
            </select>
          </div>
          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 10, marginTop: 8 }}>
            <button type="button" className="btn-ghost" onClick={() => setConsultaOpen(false)}>Cancelar</button>
            <button type="submit" className="btn-primary">Registrar</button>
          </div>
        </form>
      </Modal>

      {/* Modal edición */}
      <Modal open={editOpen} onClose={() => setEditOpen(false)} title={`Editar — ${inmueble.codigo}`}>
        <form onSubmit={handleGuardarEdicion}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 12px' }}>
            {[
              { label: 'Ciudad', key: 'ciudad', required: true },
              { label: 'Barrio', key: 'barrio' },
              { label: 'Dirección', key: 'direccion', required: true },
            ].map(({ label, key, required }) => (
              <div key={key} className="field-group">
                <label className="field-label">{label}</label>
                <input
                  className="field-input" required={required}
                  value={editForm[key] || ''}
                  onChange={(e) => setEditForm({ ...editForm, [key]: e.target.value })}
                />
              </div>
            ))}
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 12px' }}>
            <div className="field-group">
              <label className="field-label">Tipo</label>
              <select className="field-input" value={editForm.tipo || ''} onChange={(e) => setEditForm({ ...editForm, tipo: e.target.value })}>
                {TIPOS.map((t) => <option key={t} value={t}>{t}</option>)}
              </select>
            </div>
            <div className="field-group">
              <label className="field-label">Finalidad</label>
              <select className="field-input" value={editForm.finalidad || ''} onChange={(e) => setEditForm({ ...editForm, finalidad: e.target.value })}>
                {FINALIDADES.map((f) => <option key={f} value={f}>{f}</option>)}
              </select>
            </div>
            <div className="field-group">
              <label className="field-label">Estado</label>
              <select className="field-input" value={editForm.estado || ''} onChange={(e) => setEditForm({ ...editForm, estado: e.target.value })}>
                {ESTADOS.map((s) => <option key={s} value={s}>{s}</option>)}
              </select>
            </div>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr 1fr', gap: '0 12px' }}>
            {[
              { label: 'Precio', key: 'precio' },
              { label: 'Área (m²)', key: 'area' },
              { label: 'Hab.', key: 'habitaciones' },
              { label: 'Baños', key: 'banos' },
            ].map(({ label, key }) => (
              <div key={key} className="field-group">
                <label className="field-label">{label}</label>
                <input
                  type="number" className="field-input" min={0}
                  value={editForm[key] ?? ''}
                  onChange={(e) => setEditForm({ ...editForm, [key]: e.target.value })}
                />
              </div>
            ))}
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 10, marginTop: 8 }}>
            <button type="button" className="btn-ghost" onClick={() => setEditOpen(false)}>Cancelar</button>
            <button type="submit" className="btn-primary">Guardar cambios</button>
          </div>
        </form>
      </Modal>
    </div>
  )
}
