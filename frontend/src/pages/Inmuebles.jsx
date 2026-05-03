import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Building2, Bed, Bath, Square, Plus, Star, Search } from 'lucide-react'
import DSBadge from '../components/ui/DSBadge'
import StatusBadge from '../components/ui/StatusBadge'
import Modal from '../components/ui/Modal'
import {
  getInmuebles, buscarPorFiltros, buscarPorRangoPrecio, getInmueblesOrdenados,
  createInmueble, getAsesores, getClientes, agregarFavorito,
} from '../api'

const TIPOS = ['CASA', 'APARTAMENTO', 'LOCAL_COMERCIAL', 'OFICINA', 'LOTE', 'BODEGA']
const FINALIDADES = ['VENTA', 'ARRIENDO']

function formatPrecio(p) {
  if (!p && p !== 0) return '—'
  if (p >= 1_000_000) return `$${(p / 1_000_000).toFixed(1)}M`
  return `$${(p / 1000).toFixed(0)}K`
}

export default function Inmuebles() {
  const navigate = useNavigate()
  const [inmuebles, setInmuebles] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const [filtroTipo, setFiltroTipo] = useState('')
  const [filtroFinalidad, setFiltroFinalidad] = useState('')
  const [soloDisponibles, setSoloDisponibles] = useState(false)
  const [searchText, setSearchText] = useState('')

  // BST inorden — vista ordenada por precio ascendente
  const [ordenActivo, setOrdenActivo] = useState(false)

  // BST precio — '' en lugar de 0 para evitar el cero inicial al tipear
  const [precioMin, setPrecioMin] = useState('')
  const [precioMax, setPrecioMax] = useState('')
  const [bstActivo, setBstActivo] = useState(false)

  // Modal crear
  const [modalOpen, setModalOpen] = useState(false)
  const [asesores, setAsesores] = useState([])
  const [formData, setFormData] = useState({
    codigo: '', direccion: '', ciudad: '', barrio: '',
    tipo: 'APARTAMENTO', finalidad: 'VENTA',
    precio: '', area: '', habitaciones: '', banos: '',
    estado: 'NUEVO', disponible: true, codigoAsesor: '',
  })

  // Modal favoritos
  const [clientes, setClientes] = useState([])
  const [favInmueble, setFavInmueble] = useState(null)
  const [favClienteId, setFavClienteId] = useState('')
  const [favMsg, setFavMsg] = useState('')

  const cargar = () => {
    const params = {}
    if (filtroTipo) params.tipo = filtroTipo
    if (filtroFinalidad) params.finalidad = filtroFinalidad
    if (soloDisponibles) params.soloDisponibles = true

    const req = (filtroTipo || filtroFinalidad || soloDisponibles)
      ? buscarPorFiltros(params)
      : getInmuebles()

    setLoading(true)
    req
      .then((res) => setInmuebles(res.data))
      .catch(() => setError('Error cargando inmuebles'))
      .finally(() => setLoading(false))
  }

  useEffect(() => { cargar() }, [filtroTipo, filtroFinalidad, soloDisponibles])
  useEffect(() => {
    getAsesores().then((r) => setAsesores(r.data)).catch(() => {})
    getClientes().then((r) => setClientes(r.data)).catch(() => {})
  }, [])

  const aplicarBST = () => {
    const min = precioMin === '' ? 0 : Number(precioMin)
    const max = precioMax === '' ? 9_999_999_999 : Number(precioMax)
    setLoading(true)
    setBstActivo(true)
    buscarPorRangoPrecio(min, max)
      .then((r) => setInmuebles(r.data))
      .catch(() => setError('Error en búsqueda BST'))
      .finally(() => setLoading(false))
  }

  const limpiarBST = () => {
    setBstActivo(false)
    setPrecioMin('')
    setPrecioMax('')
    cargar()
  }

  const aplicarOrden = () => {
    setLoading(true)
    setOrdenActivo(true)
    setBstActivo(false)
    getInmueblesOrdenados()
      .then((r) => setInmuebles(r.data))
      .catch(() => setError('Error al obtener listado ordenado'))
      .finally(() => setLoading(false))
  }

  const limpiarOrden = () => {
    setOrdenActivo(false)
    cargar()
  }

  const inmueblesFiltrados = inmuebles.filter((inm) => {
    if (filtroTipo && inm.tipo !== filtroTipo) return false
    if (filtroFinalidad && inm.finalidad !== filtroFinalidad) return false
    if (soloDisponibles && !inm.disponible) return false
    return !searchText ||
      inm.codigo?.toLowerCase().includes(searchText.toLowerCase()) ||
      inm.ciudad?.toLowerCase().includes(searchText.toLowerCase()) ||
      inm.barrio?.toLowerCase().includes(searchText.toLowerCase())
  })

  const handleCrear = async (e) => {
    e.preventDefault()
    try {
      await createInmueble({
        ...formData,
        precio: Number(formData.precio),
        area: Number(formData.area),
        habitaciones: Number(formData.habitaciones),
        banos: Number(formData.banos),
      })
      setModalOpen(false)
      setFormData({
        codigo: '', direccion: '', ciudad: '', barrio: '',
        tipo: 'APARTAMENTO', finalidad: 'VENTA',
        precio: '', area: '', habitaciones: '', banos: '',
        estado: 'NUEVO', disponible: true, codigoAsesor: '',
      })
      cargar()
    } catch (err) {
      alert(err.response?.data?.message || err.response?.data?.error || 'Error al crear el inmueble')
    }
  }

  const handleAgregarFavorito = async () => {
    if (!favClienteId) { alert('Selecciona un cliente.'); return }
    try {
      await agregarFavorito(favClienteId, favInmueble)
      setFavMsg('✓ Agregado a favoritos')
      setTimeout(() => { setFavInmueble(null); setFavMsg(''); setFavClienteId('') }, 1200)
    } catch (err) {
      alert(err.response?.data?.message || 'Error al agregar favorito')
    }
  }

  return (
    <div style={{ display: 'flex', gap: 20, alignItems: 'flex-start' }}>

      {/* Panel izquierdo — Filtros */}
      <div style={{ width: 260, flexShrink: 0, display: 'flex', flexDirection: 'column', gap: 16 }}>
        <div className="card">
          <div style={{ position: 'relative', marginBottom: 12 }}>
            <Search size={13} style={{ position: 'absolute', left: 10, top: '50%', transform: 'translateY(-50%)', color: 'var(--color-text-muted)' }} />
            <input
              className="field-input"
              style={{ width: '100%', paddingLeft: 30 }}
              placeholder="Buscar por código, ciudad…"
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
            />
          </div>

          <div style={{ marginBottom: 12 }}>
            <div className="section-title" style={{ marginBottom: 8 }}>Tipo</div>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6 }}>
              <button className={`pill ${!filtroTipo ? 'active' : ''}`} onClick={() => setFiltroTipo('')}>Todos</button>
              {TIPOS.map((t) => (
                <button key={t} className={`pill ${filtroTipo === t ? 'active' : ''}`} onClick={() => setFiltroTipo(t)}>
                  {t.replace('_', ' ')}
                </button>
              ))}
            </div>
          </div>

          <div style={{ marginBottom: 12 }}>
            <div className="section-title" style={{ marginBottom: 8 }}>Finalidad</div>
            <div style={{ display: 'flex', gap: 6 }}>
              <button className={`pill ${!filtroFinalidad ? 'active' : ''}`} onClick={() => setFiltroFinalidad('')}>Todas</button>
              {FINALIDADES.map((f) => (
                <button key={f} className={`pill ${filtroFinalidad === f ? 'active' : ''}`} onClick={() => setFiltroFinalidad(f)}>
                  {f}
                </button>
              ))}
            </div>
          </div>

          <label style={{ display: 'flex', alignItems: 'center', gap: 8, fontSize: 13, cursor: 'pointer' }}>
            <input type="checkbox" checked={soloDisponibles} onChange={(e) => setSoloDisponibles(e.target.checked)} />
            Solo disponibles
          </label>
        </div>

        {/* Panel BST */}
        <div className="card" style={{ background: '#00a6c008', borderColor: '#00a6c030' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
            <div className="section-title">Rango de Precio</div>
            <DSBadge icon="🌳" label="BST O(log n)" />
          </div>

          <div className="field-group">
            <label className="field-label">Precio mínimo</label>
            <input
              type="number" className="field-input" min={0}
              placeholder="0"
              value={precioMin}
              onChange={(e) => setPrecioMin(e.target.value)}
            />
          </div>
          <div className="field-group">
            <label className="field-label">Precio máximo</label>
            <input
              type="number" className="field-input" min={0}
              placeholder="Sin límite"
              value={precioMax}
              onChange={(e) => setPrecioMax(e.target.value)}
            />
          </div>

          <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 10, color: 'var(--color-accent)', background: '#00a6c010', padding: '6px 8px', borderRadius: 6, marginBottom: 10 }}>
            inorder(BST, {precioMin ? formatPrecio(Number(precioMin)) : '0'}, {precioMax ? formatPrecio(Number(precioMax)) : '∞'})
          </div>

          <div style={{ display: 'flex', gap: 8 }}>
            <button className="btn-primary" onClick={aplicarBST} style={{ flex: 1, justifyContent: 'center' }}>
              Aplicar BST
            </button>
            {bstActivo && (
              <button className="btn-ghost" onClick={limpiarBST}>✕</button>
            )}
          </div>
        </div>
      </div>

      {/* Panel derecho — Grid */}
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
          <div>
            <span style={{ fontWeight: 700, fontSize: 15 }}>{inmueblesFiltrados.length} inmuebles</span>
            {bstActivo && <span style={{ marginLeft: 8, fontSize: 12, color: 'var(--color-accent)' }}>— Rango BST</span>}
            {ordenActivo && <span style={{ marginLeft: 8, fontSize: 12, color: 'var(--color-accent)' }}>— Inorden BST ↑ precio</span>}
          </div>
          <div style={{ display: 'flex', gap: 10 }}>
            <DSBadge icon="🗂️" label="HashMap O(1)" />
            <button
              className={ordenActivo ? 'btn-primary' : 'btn-outline'}
              style={{ fontSize: 12 }}
              onClick={ordenActivo ? limpiarOrden : aplicarOrden}
            >
              {ordenActivo ? '✕ Sin orden' : '↑ Precio (BST)'}
            </button>
            <button className="btn-primary" onClick={() => setModalOpen(true)}>
              <Plus size={14} /> Nuevo
            </button>
          </div>
        </div>

        {loading ? (
          <div className="loading-center"><div className="spinner" /> Cargando…</div>
        ) : error ? (
          <div className="error-banner">{error}</div>
        ) : inmueblesFiltrados.length === 0 ? (
          <div className="empty-state">
            <Building2 size={40} />
            <div>No se encontraron inmuebles con los filtros seleccionados.</div>
          </div>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(240px, 1fr))', gap: 16 }}>
            {inmueblesFiltrados.map((inm) => (
              <PropCard
                key={inm.codigo}
                inmueble={inm}
                onClick={() => navigate(`/inmuebles/${inm.codigo}`)}
                onFav={() => { setFavInmueble(inm.codigo); setFavClienteId('') }}
              />
            ))}
          </div>
        )}
      </div>

      {/* Modal favoritos */}
      {favInmueble && (
        <Modal open={true} onClose={() => { setFavInmueble(null); setFavMsg('') }} title="Agregar a favoritos">
          <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
            <p style={{ fontSize: 13, color: 'var(--color-text-muted)' }}>
              Selecciona el cliente que guarda <strong>{favInmueble}</strong> como favorito. Se registrará una arista en el grafo de relaciones.
            </p>
            <div className="field-group">
              <label className="field-label">Cliente</label>
              <select className="field-input" value={favClienteId} onChange={(e) => setFavClienteId(e.target.value)}>
                <option value="">Seleccionar cliente…</option>
                {clientes.map((c) => (
                  <option key={c.identificacion} value={c.identificacion}>{c.nombre} ({c.identificacion})</option>
                ))}
              </select>
            </div>
            {favMsg && <div style={{ color: 'var(--color-success)', fontSize: 13 }}>{favMsg}</div>}
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 10 }}>
              <button className="btn-ghost" onClick={() => { setFavInmueble(null); setFavMsg('') }}>Cancelar</button>
              <button className="btn-primary" onClick={handleAgregarFavorito} disabled={!favClienteId}>
                <Star size={13} /> Guardar favorito
              </button>
            </div>
          </div>
        </Modal>
      )}

      {/* Modal crear inmueble */}
      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Registrar Inmueble">
        <form onSubmit={handleCrear}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 12px' }}>
            {[
              { label: 'Código', key: 'codigo', required: true },
              { label: 'Ciudad', key: 'ciudad', required: true },
              { label: 'Dirección', key: 'direccion', required: true },
              { label: 'Barrio', key: 'barrio' },
            ].map(({ label, key, required }) => (
              <div key={key} className="field-group">
                <label className="field-label">{label}</label>
                <input
                  className="field-input" required={required}
                  value={formData[key]}
                  onChange={(e) => setFormData({ ...formData, [key]: e.target.value })}
                />
              </div>
            ))}
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 12px' }}>
            <div className="field-group">
              <label className="field-label">Tipo</label>
              <select className="field-input" value={formData.tipo} onChange={(e) => setFormData({ ...formData, tipo: e.target.value })}>
                {TIPOS.map((t) => <option key={t} value={t}>{t}</option>)}
              </select>
            </div>
            <div className="field-group">
              <label className="field-label">Finalidad</label>
              <select className="field-input" value={formData.finalidad} onChange={(e) => setFormData({ ...formData, finalidad: e.target.value })}>
                {FINALIDADES.map((f) => <option key={f} value={f}>{f}</option>)}
              </select>
            </div>
            <div className="field-group">
              <label className="field-label">Estado</label>
              <select className="field-input" value={formData.estado} onChange={(e) => setFormData({ ...formData, estado: e.target.value })}>
                {['NUEVO', 'USADO', 'EN_CONSTRUCCION', 'REMODELADO'].map((s) => <option key={s} value={s}>{s}</option>)}
              </select>
            </div>
            <div className="field-group">
              <label className="field-label">Asesor</label>
              <select className="field-input" required value={formData.codigoAsesor} onChange={(e) => setFormData({ ...formData, codigoAsesor: e.target.value })}>
                <option value="">Seleccionar…</option>
                {asesores.map((a) => <option key={a.identificacion} value={a.identificacion}>{a.nombre}</option>)}
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
                  value={formData[key]}
                  onChange={(e) => setFormData({ ...formData, [key]: e.target.value })}
                />
              </div>
            ))}
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

function PropCard({ inmueble, onClick, onFav }) {
  const { codigo, ciudad, barrio, tipo, finalidad, precio, habitaciones, banos, area, disponible } = inmueble

  const iconoTipo = {
    CASA: '🏠', APARTAMENTO: '🏢', OFICINA: '🏛️',
    LOCAL_COMERCIAL: '🏪', BODEGA: '🏭', LOTE: '🌿',
  }[tipo] || '🏠'

  return (
    <div
      className="card"
      style={{ padding: 0, overflow: 'hidden', cursor: 'pointer', transition: 'transform 0.15s, box-shadow 0.15s' }}
      onClick={onClick}
      onMouseEnter={(e) => { e.currentTarget.style.transform = 'translateY(-2px)'; e.currentTarget.style.boxShadow = '0 4px 20px rgba(0,0,0,0.08)' }}
      onMouseLeave={(e) => { e.currentTarget.style.transform = ''; e.currentTarget.style.boxShadow = '' }}
    >
      {/* Imagen placeholder */}
      <div style={{ height: 130, background: 'var(--color-bg-light)', display: 'flex', alignItems: 'center', justifyContent: 'center', position: 'relative' }}>
        <span style={{ fontSize: 36 }}>{iconoTipo}</span>
        <div style={{ position: 'absolute', top: 8, left: 8 }}>
          <StatusBadge value={disponible ? 'DISPONIBLE' : 'NO_DISPONIBLE'} />
        </div>
        <div style={{ position: 'absolute', top: 8, right: 8 }}>
          <StatusBadge value={finalidad} />
        </div>
        <div style={{ position: 'absolute', bottom: 8, left: 8 }}>
          <span className="mono" style={{ fontSize: 10, color: 'var(--color-text-muted)', background: 'rgba(255,255,255,0.85)', padding: '2px 6px', borderRadius: 4 }}>
            {codigo}
          </span>
        </div>
      </div>

      {/* Cuerpo */}
      <div style={{ padding: '14px 16px' }}>
        <div style={{ fontWeight: 700, fontSize: 14, color: 'var(--color-text)', marginBottom: 2 }}>
          {tipo?.replace('_', ' ')}
        </div>
        <div style={{ fontSize: 12, color: 'var(--color-text-muted)', marginBottom: 8 }}>
          {barrio}, {ciudad}
        </div>
        <div style={{ fontSize: 18, fontWeight: 800, color: 'var(--color-accent)', marginBottom: 10 }}>
          {formatPrecio(precio)}
        </div>
        <div style={{ display: 'flex', gap: 12, fontSize: 12, color: 'var(--color-text-mid)' }}>
          {habitaciones > 0 && <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}><Bed size={12} />{habitaciones}</span>}
          {banos > 0 && <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}><Bath size={12} />{banos}</span>}
          {area > 0 && <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}><Square size={12} />{area}m²</span>}
        </div>
        <div style={{ display: 'flex', gap: 8, marginTop: 12 }}>
          <button
            className="btn-outline"
            style={{ flex: 1, justifyContent: 'center', fontSize: 12, padding: '6px 10px' }}
            onClick={(e) => { e.stopPropagation(); onClick() }}
          >
            Ver detalle
          </button>
          <button
            className="btn-ghost"
            style={{ padding: '6px 10px' }}
            title="Agregar a favoritos de un cliente"
            onClick={(e) => { e.stopPropagation(); onFav() }}
          >
            <Star size={13} />
          </button>
        </div>
      </div>
    </div>
  )
}
