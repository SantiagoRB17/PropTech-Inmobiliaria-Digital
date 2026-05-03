import { useEffect, useState } from 'react'
import { BarChart3, Trophy, MapPin, Building2 } from 'lucide-react'
import DSBadge from '../components/ui/DSBadge'
import { getReporteZonas, getReporteAsesores, getReporteInmuebles } from '../api'

export default function Reportes() {
  const [zonas, setZonas] = useState({})
  const [asesores, setAsesores] = useState([])
  const [inmuebles, setInmuebles] = useState({})
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([getReporteZonas(), getReporteAsesores(), getReporteInmuebles()])
      .then(([z, a, i]) => {
        setZonas(z.data)
        setAsesores(a.data)
        setInmuebles(i.data)
      })
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <div className="loading-center"><div className="spinner" /></div>

  const zonasArr = Object.entries(zonas).sort(([, a], [, b]) => b - a)
  const maxZona = zonasArr[0]?.[1] || 1

  const inmueblesArr = Object.entries(inmuebles).sort(([, a], [, b]) => b - a)
  const maxInm = inmueblesArr[0]?.[1] || 1

  const maxCierres = asesores[0]?.cierresRealizados || 1

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 20 }}>

        {/* Zonas */}
        <div className="card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 16 }}>
            <div>
              <div className="section-title">Zonas por Actividad</div>
              <div className="section-subtitle">Ciudades con más visitas</div>
            </div>
            <DSBadge icon="🗺️" label="HashMap O(1)" />
          </div>
          {zonasArr.length === 0 ? (
            <div style={{ color: 'var(--color-text-muted)', fontSize: 13 }}>Sin datos de visitas</div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
              {zonasArr.map(([zona, count], i) => (
                <div key={zona}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 5 }}>
                    {i === 0 && <MapPin size={13} color="var(--color-accent)" />}
                    <span style={{ fontSize: 13, fontWeight: i === 0 ? 700 : 500, flex: 1 }}>{zona}</span>
                    <span className="mono" style={{ fontSize: 12, color: 'var(--color-accent)' }}>{count}</span>
                  </div>
                  <div style={{ height: 6, background: 'var(--color-bg-light)', borderRadius: 3 }}>
                    <div style={{
                      height: '100%', width: `${(count / maxZona) * 100}%`,
                      background: 'linear-gradient(90deg, var(--color-accent), var(--color-accent-dark))',
                      borderRadius: 3,
                    }} />
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Asesores */}
        <div className="card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 16 }}>
            <div>
              <div className="section-title">Efectividad Asesores</div>
              <div className="section-subtitle">Por cierres realizados</div>
            </div>
            <DSBadge icon="📋" label="ArrayList O(n)" />
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
            {asesores.map((a, i) => (
              <div key={a.identificacion}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 5 }}>
                  {i === 0 && <Trophy size={13} color="var(--color-gold)" />}
                  <span style={{ fontSize: 13, fontWeight: i === 0 ? 700 : 500, flex: 1 }}>{a.nombre}</span>
                  <span className="mono" style={{ fontSize: 12, color: i === 0 ? 'var(--color-gold)' : 'var(--color-accent)' }}>
                    {a.cierresRealizados}
                  </span>
                </div>
                <div style={{ height: 6, background: 'var(--color-bg-light)', borderRadius: 3 }}>
                  <div style={{
                    height: '100%', width: `${(a.cierresRealizados / maxCierres) * 100}%`,
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

        {/* Inmuebles con más visitas */}
        <div className="card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 16 }}>
            <div>
              <div className="section-title">Inmuebles por Visitas</div>
              <div className="section-subtitle">Más visitados</div>
            </div>
            <DSBadge icon="🗂️" label="HashMap O(1)" />
          </div>
          {inmueblesArr.length === 0 ? (
            <div style={{ color: 'var(--color-text-muted)', fontSize: 13 }}>Sin visitas registradas</div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
              {inmueblesArr.map(([codigo, count], i) => (
                <div key={codigo}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 5 }}>
                    {i === 0 && <Building2 size={13} color="var(--color-accent)" />}
                    <span className="mono" style={{ fontSize: 12, color: 'var(--color-accent)', flex: 1 }}>{codigo}</span>
                    <span className="mono" style={{ fontSize: 12 }}>{count} visitas</span>
                  </div>
                  <div style={{ height: 6, background: 'var(--color-bg-light)', borderRadius: 3 }}>
                    <div style={{
                      height: '100%', width: `${(count / maxInm) * 100}%`,
                      background: 'linear-gradient(90deg, var(--color-panel), var(--color-accent))',
                      borderRadius: 3,
                    }} />
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Nota académica */}
      <div className="card" style={{ background: '#00a6c008', borderColor: '#00a6c030' }}>
        <div style={{ display: 'flex', gap: 14, alignItems: 'flex-start' }}>
          <BarChart3 size={20} color="var(--color-accent)" style={{ flexShrink: 0, marginTop: 2 }} />
          <div>
            <div style={{ fontWeight: 700, fontSize: 14, marginBottom: 4 }}>Sobre los reportes</div>
            <p style={{ fontSize: 13, color: 'var(--color-text-muted)', lineHeight: 1.7 }}>
              Los rankings se calculan en tiempo real recorriendo las estructuras en memoria.
              Las zonas usan un <strong>HashMap O(1)</strong> para contar por ciudad.
              Los asesores se ordenan con un recorrido O(n) de la lista.
              Los inmuebles con más visitas se obtienen del repositorio de visitas.
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}
