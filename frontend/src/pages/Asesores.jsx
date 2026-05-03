import { useEffect, useState } from 'react'
import { UserCheck, Plus, Trophy, Pencil, Trash2 } from 'lucide-react'
import DSBadge from '../components/ui/DSBadge'
import Modal from '../components/ui/Modal'
import { getAsesores, getAsesor, createAsesor, updateAsesor, deleteAsesor, getReporteAsesores } from '../api'

function iniciales(nombre = '') {
  return nombre.split(' ').slice(0, 2).map((n) => n[0]).join('').toUpperCase()
}

export default function Asesores() {
  const [asesores, setAsesores] = useState([])
  const [ranking, setRanking] = useState([])
  const [loading, setLoading] = useState(true)

  // Modal crear
  const [modalOpen, setModalOpen] = useState(false)
  const [formData, setFormData] = useState({
    identificacion: '', nombre: '', contacto: '', especialidad: '', zonaAsignada: '',
  })

  // Modal editar
  const [editOpen, setEditOpen] = useState(false)
  const [editId, setEditId] = useState('')
  const [editForm, setEditForm] = useState({
    nombre: '', contacto: '', especialidad: '', zonaAsignada: '',
  })

  const cargar = () => {
    setLoading(true)
    Promise.all([getAsesores(), getReporteAsesores()])
      .then(([a, r]) => {
        setAsesores(a.data)
        setRanking(r.data)
      })
      .finally(() => setLoading(false))
  }

  useEffect(() => { cargar() }, [])

  const handleCrear = async (e) => {
    e.preventDefault()
    if (!formData.identificacion.trim()) { alert('La identificación es obligatoria.'); return }
    if (!formData.nombre.trim()) { alert('El nombre es obligatorio.'); return }
    try {
      await createAsesor(formData)
      setModalOpen(false)
      setFormData({ identificacion: '', nombre: '', contacto: '', especialidad: '', zonaAsignada: '' })
      cargar()
    } catch (err) {
      alert(err.response?.data?.message || err.response?.data?.error || 'Error al registrar asesor')
    }
  }

  // Carga datos del asesor en el formulario de edición usando getAsesor
  const handleAbrirEditar = async (id) => {
    try {
      const r = await getAsesor(id)
      setEditId(id)
      setEditForm({
        nombre:       r.data.nombre       || '',
        contacto:     r.data.contacto     || '',
        especialidad: r.data.especialidad || '',
        zonaAsignada: r.data.zonaAsignada || '',
      })
      setEditOpen(true)
    } catch {
      alert('Error al cargar datos del asesor')
    }
  }

  const handleGuardarEdicion = async (e) => {
    e.preventDefault()
    try {
      await updateAsesor(editId, editForm)
      setEditOpen(false)
      cargar()
    } catch (err) {
      alert(err.response?.data?.message || 'Error al actualizar asesor')
    }
  }

  const handleEliminar = async (id, nombre) => {
    if (!confirm(`¿Eliminar al asesor ${nombre}? Esta acción no se puede deshacer.`)) return
    try {
      await deleteAsesor(id)
      cargar()
    } catch (err) {
      alert(err.response?.data?.message || 'Error al eliminar asesor')
    }
  }

  const maxCierres = ranking[0]?.cierresRealizados || 1

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <div style={{ fontSize: 15, fontWeight: 700 }}>{asesores.length} asesores registrados</div>
          <div style={{ fontSize: 12, color: 'var(--color-text-muted)' }}>Gestión de equipo comercial</div>
        </div>
        <button className="btn-primary" onClick={() => setModalOpen(true)}>
          <Plus size={14} /> Nuevo asesor
        </button>
      </div>

      {loading ? (
        <div className="loading-center"><div className="spinner" /></div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 320px', gap: 20 }}>

          {/* Tabla asesores */}
          <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
            <div style={{ padding: '16px 20px', borderBottom: '1px solid var(--color-sand)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div className="section-title" style={{ marginBottom: 0 }}>Equipo de Asesores</div>
              <DSBadge icon="🗂️" label="HashMap O(1)" />
            </div>
            <table className="data-table">
              <thead>
                <tr>
                  <th>Asesor</th>
                  <th>Especialidad</th>
                  <th>Zona</th>
                  <th>Inmuebles</th>
                  <th>Visitas</th>
                  <th>Cierres</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {asesores.map((a) => (
                  <tr key={a.identificacion}>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                        <div style={{
                          width: 32, height: 32, borderRadius: '50%',
                          background: 'linear-gradient(135deg, var(--color-accent), var(--color-panel))',
                          display: 'flex', alignItems: 'center', justifyContent: 'center',
                          color: '#fff', fontWeight: 700, fontSize: 11, flexShrink: 0,
                        }}>
                          {iniciales(a.nombre)}
                        </div>
                        <div>
                          <div style={{ fontWeight: 600, fontSize: 13 }}>{a.nombre}</div>
                          <div style={{ fontSize: 11, color: 'var(--color-text-muted)' }}>{a.contacto}</div>
                        </div>
                      </div>
                    </td>
                    <td style={{ fontSize: 12 }}>{a.especialidad}</td>
                    <td style={{ fontSize: 12, color: 'var(--color-text-muted)' }}>{a.zonaAsignada}</td>
                    <td className="mono" style={{ fontSize: 12 }}>{a.inmueblesAsignados?.length ?? 0}</td>
                    <td className="mono" style={{ fontSize: 12 }}>{a.visitasAgendadas?.length ?? 0}</td>
                    <td>
                      <span style={{
                        display: 'inline-flex', alignItems: 'center', gap: 4,
                        padding: '2px 8px', borderRadius: 20,
                        background: '#00a6c018', color: 'var(--color-accent)',
                        fontFamily: "'JetBrains Mono', monospace", fontSize: 11, fontWeight: 700,
                      }}>
                        {a.cierresRealizados}
                      </span>
                    </td>
                    <td>
                      <div style={{ display: 'flex', gap: 6 }}>
                        <button
                          className="btn-ghost"
                          style={{ fontSize: 11, padding: '3px 8px', display: 'flex', alignItems: 'center', gap: 4 }}
                          onClick={() => handleAbrirEditar(a.identificacion)}
                        >
                          <Pencil size={11} /> Editar
                        </button>
                        <button
                          className="btn-ghost"
                          style={{ fontSize: 11, padding: '3px 8px', color: 'var(--color-danger)', display: 'flex', alignItems: 'center', gap: 4 }}
                          onClick={() => handleEliminar(a.identificacion, a.nombre)}
                        >
                          <Trash2 size={11} /> Eliminar
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Ranking de efectividad */}
          <div className="card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 16 }}>
              <div>
                <div className="section-title">Ranking Efectividad</div>
                <div className="section-subtitle">Por cierres realizados</div>
              </div>
              <DSBadge icon="📋" label="ArrayList O(n)" />
            </div>
            {ranking.map((a, i) => (
              <div key={a.identificacion} style={{ marginBottom: 14 }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 5 }}>
                  {i === 0 && <Trophy size={14} color="var(--color-gold)" />}
                  <span style={{ fontSize: 13, fontWeight: 600, flex: 1 }}>{a.nombre}</span>
                  <span className="mono" style={{ fontSize: 12, color: 'var(--color-accent)' }}>
                    {a.cierresRealizados}
                  </span>
                </div>
                <div style={{ height: 6, background: 'var(--color-bg-light)', borderRadius: 3 }}>
                  <div style={{
                    height: '100%',
                    width: `${(a.cierresRealizados / maxCierres) * 100}%`,
                    background: i === 0
                      ? 'linear-gradient(90deg, var(--color-gold), #c49a10)'
                      : 'linear-gradient(90deg, var(--color-accent), var(--color-accent-dark))',
                    borderRadius: 3,
                  }} />
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Modal crear */}
      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Registrar Asesor">
        <form onSubmit={handleCrear}>
          {[
            { label: 'Identificación', key: 'identificacion', required: true },
            { label: 'Nombre',         key: 'nombre',         required: true },
            { label: 'Contacto (email)', key: 'contacto' },
            { label: 'Especialidad',   key: 'especialidad' },
            { label: 'Zona asignada',  key: 'zonaAsignada' },
          ].map(({ label, key, required }) => (
            <div key={key} className="field-group">
              <label className="field-label">{label}</label>
              <input className="field-input" required={required} value={formData[key]}
                onChange={(e) => setFormData({ ...formData, [key]: e.target.value })} />
            </div>
          ))}
          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 10, marginTop: 8 }}>
            <button type="button" className="btn-ghost" onClick={() => setModalOpen(false)}>Cancelar</button>
            <button type="submit" className="btn-primary">Guardar</button>
          </div>
        </form>
      </Modal>

      {/* Modal editar */}
      <Modal open={editOpen} onClose={() => setEditOpen(false)} title={`Editar asesor — ${editId}`}>
        <form onSubmit={handleGuardarEdicion}>
          {[
            { label: 'Nombre',         key: 'nombre',         required: true },
            { label: 'Contacto (email)', key: 'contacto' },
            { label: 'Especialidad',   key: 'especialidad' },
            { label: 'Zona asignada',  key: 'zonaAsignada' },
          ].map(({ label, key, required }) => (
            <div key={key} className="field-group">
              <label className="field-label">{label}</label>
              <input className="field-input" required={required} value={editForm[key] || ''}
                onChange={(e) => setEditForm({ ...editForm, [key]: e.target.value })} />
            </div>
          ))}
          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 10, marginTop: 8 }}>
            <button type="button" className="btn-ghost" onClick={() => setEditOpen(false)}>Cancelar</button>
            <button type="submit" className="btn-primary">Guardar cambios</button>
          </div>
        </form>
      </Modal>
    </div>
  )
}
