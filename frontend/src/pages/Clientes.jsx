import { useEffect, useState } from 'react'
import { Users, Plus, Search, Pencil, Trash2 } from 'lucide-react'
import DSBadge from '../components/ui/DSBadge'
import StatusBadge from '../components/ui/StatusBadge'
import GrafoPanel from '../components/ui/GrafoPanel'
import Modal from '../components/ui/Modal'
import {
  getClientes, getInmuebles, createCliente, getCliente,
  updateCliente, deleteCliente,
  registrarConsulta, descartarInmueble, quitarFavorito,
} from '../api'

const TIPOS_CLIENTE    = ['COMPRADOR', 'ARRENDATARIO', 'VIP']
const TIPOS_INMUEBLE   = ['CASA', 'APARTAMENTO', 'LOCAL_COMERCIAL', 'OFICINA', 'BODEGA', 'LOTE']
const ESTADOS_BUSQUEDA = ['ACTIVO', 'PAUSADO', 'CERRADO']

function iniciales(nombre = '') {
  return nombre.split(' ').slice(0, 2).map((n) => n[0]).join('').toUpperCase()
}

function formatPrecio(p) {
  if (!p) return '—'
  if (p >= 1_000_000) return `$${(p / 1_000_000).toFixed(1)}M`
  return `$${(p / 1000).toFixed(0)}K`
}

export default function Clientes() {
  const [clientes, setClientes]               = useState([])
  const [inmuebles, setInmuebles]             = useState([])
  const [loading, setLoading]                 = useState(true)
  const [search, setSearch]                   = useState('')
  const [modalOpen, setModalOpen]             = useState(false)
  const [clienteDetalle, setClienteDetalle]   = useState(null)
  const [editando, setEditando]               = useState(false)
  const [editForm, setEditForm]               = useState({})
  const [interaccionCodigo, setInteraccionCodigo] = useState('')
  const [interaccionMsg, setInteraccionMsg]       = useState('')

  const [formData, setFormData] = useState({
    identificacion: '', nombre: '', correo: '', telefono: '',
    tipoCliente: 'COMPRADOR', presupuesto: '',
    tipoDeseado: 'APARTAMENTO', habitacionesMin: 1,
    estadoBusqueda: 'ACTIVO', zonasInteres: '',
  })

  const cargar = () => {
    setLoading(true)
    Promise.all([getClientes(), getInmuebles()])
      .then(([cli, inm]) => { setClientes(cli.data); setInmuebles(inm.data) })
      .finally(() => setLoading(false))
  }

  useEffect(() => { cargar() }, [])

  const clientesFiltrados = clientes.filter((c) =>
    !search ||
    c.nombre?.toLowerCase().includes(search.toLowerCase()) ||
    c.identificacion?.toLowerCase().includes(search.toLowerCase())
  )

  const handleCrear = async (e) => {
    e.preventDefault()
    if (!formData.identificacion.trim()) { alert('La identificación es obligatoria.'); return }
    if (!formData.nombre.trim())         { alert('El nombre es obligatorio.'); return }
    try {
      await createCliente({
        ...formData,
        presupuesto:     Number(formData.presupuesto),
        habitacionesMin: Number(formData.habitacionesMin),
        zonasInteres: formData.zonasInteres
          ? formData.zonasInteres.split(',').map((z) => z.trim()).filter(Boolean)
          : [],
      })
      setModalOpen(false)
      setFormData({
        identificacion: '', nombre: '', correo: '', telefono: '',
        tipoCliente: 'COMPRADOR', presupuesto: '',
        tipoDeseado: 'APARTAMENTO', habitacionesMin: 1,
        estadoBusqueda: 'ACTIVO', zonasInteres: '',
      })
      cargar()
    } catch (err) {
      alert(err.response?.data?.message || err.response?.data?.error || 'Error al crear el cliente')
    }
  }

  const recargarDetalle = (id) =>
    getCliente(id).then((r) => { setClienteDetalle(r.data); setInteraccionMsg('') })

  // Abre el detalle y precarga el formulario de edición
  const abrirDetalle = (id) =>
    getCliente(id).then((r) => {
      const c = r.data
      setClienteDetalle(c)
      setEditando(false)
      setInteraccionCodigo('')
      setInteraccionMsg('')
      setEditForm({
        nombre:          c.nombre          || '',
        correo:          c.correo          || '',
        telefono:        c.telefono        || '',
        tipoCliente:     c.tipoCliente     || 'COMPRADOR',
        presupuesto:     c.presupuesto     ?? '',
        tipoDeseado:     c.tipoDeseado     || 'APARTAMENTO',
        habitacionesMin: c.habitacionesMin ?? 1,
        estadoBusqueda:  c.estadoBusqueda  || 'ACTIVO',
        zonasInteres:    (c.zonasInteres   || []).join(', '),
      })
    })

  const handleGuardarEdicion = async (e) => {
    e.preventDefault()
    try {
      await updateCliente(clienteDetalle.identificacion, {
        ...editForm,
        presupuesto:     Number(editForm.presupuesto),
        habitacionesMin: Number(editForm.habitacionesMin),
        zonasInteres: editForm.zonasInteres
          ? editForm.zonasInteres.split(',').map((z) => z.trim()).filter(Boolean)
          : [],
      })
      setEditando(false)
      recargarDetalle(clienteDetalle.identificacion)
      cargar()
    } catch (err) {
      alert(err.response?.data?.message || 'Error al actualizar cliente')
    }
  }

  const handleEliminar = async (id, nombre) => {
    if (!confirm(`¿Eliminar al cliente ${nombre}? Esta acción no se puede deshacer.`)) return
    try {
      await deleteCliente(id)
      setClienteDetalle(null)
      cargar()
    } catch (err) {
      alert(err.response?.data?.message || 'Error al eliminar cliente')
    }
  }

  const handleQuitarFavorito = async (codigoInmueble) => {
    try {
      await quitarFavorito(clienteDetalle.identificacion, codigoInmueble)
      recargarDetalle(clienteDetalle.identificacion)
    } catch (err) {
      alert(err.response?.data?.message || 'Error al quitar favorito')
    }
  }

  const handleRegistrarConsulta = async () => {
    if (!interaccionCodigo) { alert('Selecciona un inmueble.'); return }
    try {
      await registrarConsulta(clienteDetalle.identificacion, interaccionCodigo)
      setInteraccionMsg('✓ Consulta registrada y arista agregada al grafo.')
      recargarDetalle(clienteDetalle.identificacion)
    } catch (err) {
      alert(err.response?.data?.message || 'Error al registrar consulta')
    }
  }

  const handleDescartar = async () => {
    if (!interaccionCodigo) { alert('Selecciona un inmueble.'); return }
    try {
      await descartarInmueble(clienteDetalle.identificacion, interaccionCodigo)
      setInteraccionMsg('✓ Inmueble descartado. No aparecerá en recomendaciones futuras.')
      recargarDetalle(clienteDetalle.identificacion)
    } catch (err) {
      alert(err.response?.data?.message || 'Error al descartar')
    }
  }

  const cerrarDetalle = () => {
    setClienteDetalle(null)
    setEditando(false)
    setInteraccionCodigo('')
    setInteraccionMsg('')
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>

      {/* Encabezado */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <div style={{ position: 'relative' }}>
            <Search size={13} style={{ position: 'absolute', left: 10, top: '50%', transform: 'translateY(-50%)', color: 'var(--color-text-muted)' }} />
            <input
              className="field-input"
              style={{ width: 260, paddingLeft: 30 }}
              placeholder="Buscar por nombre o ID…"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
          <DSBadge icon="🗂️" label="HashMap O(1)" />
        </div>
        <button className="btn-primary" onClick={() => setModalOpen(true)}>
          <Plus size={14} /> Nuevo cliente
        </button>
      </div>

      {/* Tabla */}
      {loading ? (
        <div className="loading-center"><div className="spinner" /> Cargando clientes…</div>
      ) : clientesFiltrados.length === 0 ? (
        <div className="empty-state"><Users size={40} /><div>No se encontraron clientes.</div></div>
      ) : (
        <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
          <table className="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Zonas / Tipo deseado</th>
                <th>Presupuesto</th>
                <th>Tipo</th>
                <th>Búsqueda</th>
                <th>Consultas</th>
                <th>Favoritos</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {clientesFiltrados.map((c, i) => (
                <tr key={c.identificacion} style={{ background: i % 2 === 0 ? '#fff' : '#f0efeb50' }}>
                  <td>
                    <span className="mono" style={{ fontSize: 12, color: 'var(--color-accent)' }}>
                      {c.identificacion}
                    </span>
                  </td>
                  <td>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                      <div style={{
                        width: 30, height: 30, borderRadius: '50%',
                        background: 'linear-gradient(135deg, #00a6c080, #283b4880)',
                        display: 'flex', alignItems: 'center', justifyContent: 'center',
                        color: '#fff', fontWeight: 700, fontSize: 11, flexShrink: 0,
                      }}>
                        {iniciales(c.nombre)}
                      </div>
                      <div>
                        <div style={{ fontWeight: 600, fontSize: 13 }}>{c.nombre}</div>
                        <div style={{ fontSize: 11, color: 'var(--color-text-muted)' }}>{c.correo}</div>
                      </div>
                    </div>
                  </td>
                  <td style={{ fontSize: 12 }}>
                    <div style={{ color: 'var(--color-text-muted)' }}>{c.zonasInteres?.slice(0, 2).join(', ') || '—'}</div>
                    {c.tipoDeseado && (
                      <div style={{ fontSize: 11, color: 'var(--color-accent)', marginTop: 2 }}>
                        {c.tipoDeseado.replace('_', ' ')} · mín {c.habitacionesMin ?? 0} hab.
                      </div>
                    )}
                  </td>
                  <td style={{ fontWeight: 600, fontSize: 13, color: 'var(--color-text-mid)' }}>
                    {formatPrecio(c.presupuesto)}
                  </td>
                  <td><StatusBadge value={c.tipoCliente} /></td>
                  <td><StatusBadge value={c.estadoBusqueda} /></td>
                  <td style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 12 }}>
                    {c.inmueblesConsultados?.length ?? 0}
                  </td>
                  <td style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 12 }}>
                    {c.favoritos?.length ?? 0}
                  </td>
                  <td>
                    <div style={{ display: 'flex', gap: 6 }}>
                      <button
                        className="btn-ghost"
                        style={{ fontSize: 11, padding: '4px 8px' }}
                        onClick={() => abrirDetalle(c.identificacion)}
                      >
                        Ver
                      </button>
                      <button
                        className="btn-ghost"
                        style={{ fontSize: 11, padding: '4px 8px', color: 'var(--color-danger)', display: 'flex', alignItems: 'center', gap: 3 }}
                        onClick={() => handleEliminar(c.identificacion, c.nombre)}
                      >
                        <Trash2 size={11} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Grafo */}
      <div className="card">
        <GrafoPanel clientes={clientes} inmuebles={inmuebles} />
      </div>

      {/* ── Modal detalle / edición de cliente ── */}
      {clienteDetalle && (
        <Modal open={true} onClose={cerrarDetalle} title={`Cliente — ${clienteDetalle.nombre}`}>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>

            {/* Acciones del modal */}
            <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
              {!editando ? (
                <>
                  <button className="btn-outline" style={{ fontSize: 12, display: 'flex', alignItems: 'center', gap: 5 }}
                    onClick={() => setEditando(true)}>
                    <Pencil size={12} /> Editar
                  </button>
                  <button className="btn-ghost" style={{ fontSize: 12, color: 'var(--color-danger)', display: 'flex', alignItems: 'center', gap: 5 }}
                    onClick={() => handleEliminar(clienteDetalle.identificacion, clienteDetalle.nombre)}>
                    <Trash2 size={12} /> Eliminar
                  </button>
                </>
              ) : (
                <button className="btn-ghost" style={{ fontSize: 12 }} onClick={() => setEditando(false)}>
                  Cancelar edición
                </button>
              )}
            </div>

            {/* ── MODO LECTURA ── */}
            {!editando && (
              <>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                  {[
                    { label: 'Identificación', value: clienteDetalle.identificacion },
                    { label: 'Teléfono',       value: clienteDetalle.telefono || '—' },
                    { label: 'Correo',         value: clienteDetalle.correo || '—' },
                    { label: 'Presupuesto',    value: formatPrecio(clienteDetalle.presupuesto) },
                  ].map(({ label, value }) => (
                    <div key={label}>
                      <div style={{ fontSize: 11, color: 'var(--color-text-muted)', marginBottom: 2 }}>{label}</div>
                      <div style={{ fontSize: 13, fontWeight: 600 }}>{value}</div>
                    </div>
                  ))}
                </div>

                <div style={{ background: 'var(--color-bg-light)', borderRadius: 8, padding: '10px 14px' }}>
                  <div style={{ fontSize: 11, color: 'var(--color-text-muted)', marginBottom: 8, fontWeight: 600, textTransform: 'uppercase', letterSpacing: 0.5 }}>
                    Perfil de búsqueda
                  </div>
                  <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', marginBottom: 8 }}>
                    <StatusBadge value={clienteDetalle.tipoCliente} />
                    <StatusBadge value={clienteDetalle.estadoBusqueda} />
                    {clienteDetalle.tipoDeseado && <StatusBadge value={clienteDetalle.tipoDeseado} />}
                  </div>
                  {clienteDetalle.habitacionesMin > 0 && (
                    <div style={{ fontSize: 12, color: 'var(--color-text-mid)' }}>
                      Habitaciones mínimas: <strong>{clienteDetalle.habitacionesMin}</strong>
                    </div>
                  )}
                </div>

                {clienteDetalle.zonasInteres?.length > 0 && (
                  <div>
                    <div style={{ fontSize: 11, color: 'var(--color-text-muted)', marginBottom: 6 }}>Zonas de interés</div>
                    <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
                      {clienteDetalle.zonasInteres.map((z) => (
                        <span key={z} style={{ fontSize: 12, background: 'var(--color-bg-light)', border: '1px solid var(--color-sand)', borderRadius: 6, padding: '2px 8px' }}>{z}</span>
                      ))}
                    </div>
                  </div>
                )}

                {/* Historial de interacciones */}
                <div>
                  <div style={{ fontSize: 11, color: 'var(--color-text-muted)', marginBottom: 8, fontWeight: 600, textTransform: 'uppercase', letterSpacing: 0.5 }}>
                    Historial de interacciones
                  </div>
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 8 }}>
                    {[
                      { label: 'Consultados',  lista: clienteDetalle.inmueblesConsultados,  color: '#00a6c0', bg: '#00a6c012', border: '#00a6c030', removible: false },
                      { label: 'Favoritos ⭐', lista: clienteDetalle.favoritos,              color: '#c49a10', bg: '#f0c04018', border: '#f0c04040', removible: true  },
                      { label: 'Descartados',  lista: clienteDetalle.inmueblesDescartados,  color: '#c0392b', bg: '#c0392b12', border: '#c0392b30', removible: false },
                      { label: 'Negociados',   lista: clienteDetalle.inmueblesNegociados,   color: '#1a9e6e', bg: '#1a9e6e12', border: '#1a9e6e30', removible: false },
                    ].map(({ label, lista, color, bg, border, removible }) => (
                      <div key={label} style={{ background: 'var(--color-bg-light)', borderRadius: 8, padding: '10px 12px', border: `1px solid ${border}` }}>
                        <div style={{ fontSize: 11, color, fontWeight: 700, marginBottom: 6 }}>
                          {label} <span style={{ fontFamily: "'JetBrains Mono', monospace" }}>({lista?.length ?? 0})</span>
                        </div>
                        {lista?.length > 0 ? (
                          <div style={{ display: 'flex', gap: 4, flexWrap: 'wrap' }}>
                            {lista.map((cod) => (
                              <span key={cod} style={{ display: 'inline-flex', alignItems: 'center', gap: 3, fontSize: 10, background: bg, color, border: `1px solid ${border}`, borderRadius: 4, padding: '2px 6px' }}>
                                <span className="mono">{cod}</span>
                                {removible && (
                                  <button
                                    onClick={() => handleQuitarFavorito(cod)}
                                    style={{ background: 'none', border: 'none', cursor: 'pointer', color, padding: 0, lineHeight: 1, fontSize: 11 }}
                                    title="Quitar de favoritos"
                                  >
                                    ✕
                                  </button>
                                )}
                              </span>
                            ))}
                          </div>
                        ) : (
                          <div style={{ fontSize: 11, color: 'var(--color-text-muted)' }}>Sin registros</div>
                        )}
                      </div>
                    ))}
                  </div>
                </div>

                {/* Registrar interacción */}
                <div style={{ borderTop: '1px solid var(--color-sand)', paddingTop: 12 }}>
                  <div style={{ fontSize: 11, color: 'var(--color-text-muted)', marginBottom: 8, fontWeight: 600, textTransform: 'uppercase', letterSpacing: 0.5 }}>
                    Registrar interacción
                  </div>
                  <div style={{ display: 'flex', gap: 8, alignItems: 'flex-end' }}>
                    <div className="field-group" style={{ flex: 1, marginBottom: 0 }}>
                      <label className="field-label">Inmueble</label>
                      <select className="field-input" value={interaccionCodigo} onChange={(e) => setInteraccionCodigo(e.target.value)}>
                        <option value="">Seleccionar…</option>
                        {inmuebles.map((inm) => (
                          <option key={inm.codigo} value={inm.codigo}>
                            {inm.codigo} — {inm.tipo?.replace('_', ' ')}, {inm.ciudad}
                          </option>
                        ))}
                      </select>
                    </div>
                    <button className="btn-primary" style={{ whiteSpace: 'nowrap' }} onClick={handleRegistrarConsulta} disabled={!interaccionCodigo}>
                      Registrar consulta
                    </button>
                    <button className="btn-ghost" style={{ whiteSpace: 'nowrap', color: '#c0392b', border: '1px solid #c0392b40' }} onClick={handleDescartar} disabled={!interaccionCodigo}>
                      Descartar
                    </button>
                  </div>
                  {interaccionMsg && (
                    <div style={{ marginTop: 8, fontSize: 12, color: 'var(--color-success)' }}>
                      {interaccionMsg}
                    </div>
                  )}
                </div>

                <DSBadge icon="🔗" label="Grafo O(V+E)" />
              </>
            )}

            {/* ── MODO EDICIÓN ── */}
            {editando && (
              <form onSubmit={handleGuardarEdicion}>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 12px' }}>
                  {[
                    { label: 'Nombre',   key: 'nombre',   required: true },
                    { label: 'Correo',   key: 'correo' },
                    { label: 'Teléfono', key: 'telefono' },
                  ].map(({ label, key, required }) => (
                    <div key={key} className="field-group">
                      <label className="field-label">{label}</label>
                      <input className="field-input" required={required} value={editForm[key] || ''}
                        onChange={(e) => setEditForm({ ...editForm, [key]: e.target.value })} />
                    </div>
                  ))}
                </div>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 12px' }}>
                  <div className="field-group">
                    <label className="field-label">Tipo de cliente</label>
                    <select className="field-input" value={editForm.tipoCliente} onChange={(e) => setEditForm({ ...editForm, tipoCliente: e.target.value })}>
                      {TIPOS_CLIENTE.map((t) => <option key={t} value={t}>{t}</option>)}
                    </select>
                  </div>
                  <div className="field-group">
                    <label className="field-label">Presupuesto</label>
                    <input type="number" className="field-input" min={0} value={editForm.presupuesto}
                      onChange={(e) => setEditForm({ ...editForm, presupuesto: e.target.value })} />
                  </div>
                  <div className="field-group">
                    <label className="field-label">Tipo inmueble deseado</label>
                    <select className="field-input" value={editForm.tipoDeseado} onChange={(e) => setEditForm({ ...editForm, tipoDeseado: e.target.value })}>
                      {TIPOS_INMUEBLE.map((t) => <option key={t} value={t}>{t.replace('_', ' ')}</option>)}
                    </select>
                  </div>
                  <div className="field-group">
                    <label className="field-label">Habitaciones mínimas</label>
                    <input type="number" className="field-input" min={0} value={editForm.habitacionesMin}
                      onChange={(e) => setEditForm({ ...editForm, habitacionesMin: e.target.value })} />
                  </div>
                  <div className="field-group">
                    <label className="field-label">Estado de búsqueda</label>
                    <select className="field-input" value={editForm.estadoBusqueda} onChange={(e) => setEditForm({ ...editForm, estadoBusqueda: e.target.value })}>
                      {ESTADOS_BUSQUEDA.map((s) => <option key={s} value={s}>{s}</option>)}
                    </select>
                  </div>
                  <div className="field-group">
                    <label className="field-label">Zonas de interés (separadas por coma)</label>
                    <input className="field-input" placeholder="Medellín, Bogotá" value={editForm.zonasInteres}
                      onChange={(e) => setEditForm({ ...editForm, zonasInteres: e.target.value })} />
                  </div>
                </div>
                <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 10, marginTop: 8 }}>
                  <button type="button" className="btn-ghost" onClick={() => setEditando(false)}>Cancelar</button>
                  <button type="submit" className="btn-primary">Guardar cambios</button>
                </div>
              </form>
            )}
          </div>
        </Modal>
      )}

      {/* ── Modal crear cliente ── */}
      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Registrar Cliente">
        <form onSubmit={handleCrear}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 12px' }}>
            {[
              { label: 'Identificación', key: 'identificacion', required: true },
              { label: 'Nombre',         key: 'nombre',         required: true },
              { label: 'Correo',         key: 'correo' },
              { label: 'Teléfono',       key: 'telefono' },
            ].map(({ label, key, required }) => (
              <div key={key} className="field-group">
                <label className="field-label">{label}</label>
                <input className="field-input" required={required} value={formData[key]}
                  onChange={(e) => setFormData({ ...formData, [key]: e.target.value })} />
              </div>
            ))}
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 12px' }}>
            <div className="field-group">
              <label className="field-label">Tipo de cliente</label>
              <select className="field-input" value={formData.tipoCliente} onChange={(e) => setFormData({ ...formData, tipoCliente: e.target.value })}>
                {TIPOS_CLIENTE.map((t) => <option key={t} value={t}>{t}</option>)}
              </select>
            </div>
            <div className="field-group">
              <label className="field-label">Presupuesto</label>
              <input type="number" className="field-input" min={0} value={formData.presupuesto}
                onChange={(e) => setFormData({ ...formData, presupuesto: e.target.value })} />
            </div>
            <div className="field-group">
              <label className="field-label">Tipo de inmueble deseado</label>
              <select className="field-input" value={formData.tipoDeseado} onChange={(e) => setFormData({ ...formData, tipoDeseado: e.target.value })}>
                {TIPOS_INMUEBLE.map((t) => <option key={t} value={t}>{t.replace('_', ' ')}</option>)}
              </select>
            </div>
            <div className="field-group">
              <label className="field-label">Habitaciones mínimas</label>
              <input type="number" className="field-input" min={0} value={formData.habitacionesMin}
                onChange={(e) => setFormData({ ...formData, habitacionesMin: e.target.value })} />
            </div>
            <div className="field-group">
              <label className="field-label">Estado de búsqueda</label>
              <select className="field-input" value={formData.estadoBusqueda} onChange={(e) => setFormData({ ...formData, estadoBusqueda: e.target.value })}>
                {ESTADOS_BUSQUEDA.map((s) => <option key={s} value={s}>{s}</option>)}
              </select>
            </div>
            <div className="field-group">
              <label className="field-label">Zonas de interés (separadas por coma)</label>
              <input className="field-input" placeholder="Medellín, El Poblado, Bogotá" value={formData.zonasInteres}
                onChange={(e) => setFormData({ ...formData, zonasInteres: e.target.value })} />
            </div>
          </div>
          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 10, marginTop: 8 }}>
            <button type="button" className="btn-ghost" onClick={() => setModalOpen(false)}>Cancelar</button>
            <button type="submit" className="btn-primary">Guardar</button>
          </div>
        </form>
      </Modal>
    </div>
  )
}
