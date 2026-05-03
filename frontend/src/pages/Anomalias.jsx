import { useState } from 'react'
import { ShieldAlert, Play, Building2, Users, UserCheck, TrendingUp, MapPin } from 'lucide-react'
import DSBadge from '../components/ui/DSBadge'
import StatusBadge from '../components/ui/StatusBadge'
import {
  ejecutarDeteccion, detectarInmueblesSinCierre,
  detectarClientesSinSeguimiento, detectarAsesoresSobrecarga,
  detectarCambiosPrecioFrecuentes, detectarConcentracionZona,
} from '../api'

export default function Anomalias() {
  const [alertas, setAlertas] = useState([])
  const [loading, setLoading] = useState(false)
  const [msg, setMsg] = useState('')
  const [modulo, setModulo] = useState('')

  const ejecutar = async (fn, nombre) => {
    setLoading(true)
    setModulo(nombre)
    try {
      const r = await fn()
      setAlertas(r.data)
      setMsg(r.data.length === 0
        ? `✓ ${nombre}: No se detectaron anomalías.`
        : `Se encontraron ${r.data.length} anomalías en "${nombre}".`)
    } catch {
      setMsg('Error al ejecutar la detección.')
    } finally {
      setLoading(false)
    }
  }

  const MODULOS = [
    {
      icon: <Play size={16} />,
      label: 'Detección completa',
      desc: 'Ejecuta todos los módulos de detección simultáneamente.',
      fn: ejecutarDeteccion,
      color: '#00a6c0',
    },
    {
      icon: <Building2 size={16} />,
      label: 'Inmuebles sin cierre',
      desc: 'Detecta inmuebles con muchas visitas pero sin operación registrada.',
      fn: detectarInmueblesSinCierre,
      color: '#d4890a',
    },
    {
      icon: <Users size={16} />,
      label: 'Clientes sin seguimiento',
      desc: 'Detecta clientes con visitas realizadas pero sin operación asociada.',
      fn: detectarClientesSinSeguimiento,
      color: '#d4890a',
    },
    {
      icon: <UserCheck size={16} />,
      label: 'Asesores sobrecargados',
      desc: 'Detecta asesores con una cantidad excesiva de visitas agendadas.',
      fn: detectarAsesoresSobrecarga,
      color: '#c0392b',
    },
    {
      icon: <TrendingUp size={16} />,
      label: 'Cambios de precio frecuentes',
      desc: 'Detecta inmuebles cuyo precio ha cambiado 2 o más veces sin registrar cierre.',
      fn: detectarCambiosPrecioFrecuentes,
      color: '#7c3aed',
    },
    {
      icon: <MapPin size={16} />,
      label: 'Concentración por zona',
      desc: 'Detecta ciudades con alta concentración de visitas en el período actual.',
      fn: detectarConcentracionZona,
      color: '#0891b2',
    },
  ]

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
      {/* Encabezado */}
      <div className="card" style={{ background: '#1a212e', borderColor: '#283b48' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 12 }}>
          <div>
            <div style={{ fontSize: 15, fontWeight: 700, color: '#d8d7ce', marginBottom: 4 }}>
              Detección de Comportamientos Inusuales
            </div>
            <div style={{ fontSize: 12, color: '#6b7f8e', lineHeight: 1.6 }}>
              Analiza las estructuras en memoria para identificar patrones de riesgo:
              inmuebles sin conversión, clientes sin seguimiento, asesores con sobrecarga.
            </div>
          </div>
          <DSBadge icon="🔍" label="Análisis O(n)" color="#00a6c0" />
        </div>
      </div>

      {/* Módulos */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: 14 }}>
        {MODULOS.map(({ icon, label, desc, fn, color }) => (
          <div key={label} className="card" style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
            <div style={{ display: 'flex', gap: 10, alignItems: 'flex-start' }}>
              <div style={{ width: 36, height: 36, borderRadius: 9, background: color + '18', display: 'flex', alignItems: 'center', justifyContent: 'center', color, flexShrink: 0 }}>
                {icon}
              </div>
              <div>
                <div style={{ fontWeight: 700, fontSize: 14, marginBottom: 3 }}>{label}</div>
                <div style={{ fontSize: 12, color: 'var(--color-text-muted)', lineHeight: 1.6 }}>{desc}</div>
              </div>
            </div>
            <button
              className="btn-primary"
              style={{ background: `linear-gradient(90deg, ${color}, ${color}cc)`, justifyContent: 'center', width: '100%' }}
              onClick={() => ejecutar(fn, label)}
              disabled={loading}
            >
              <Play size={13} /> Ejecutar
            </button>
          </div>
        ))}
      </div>

      {/* Mensajes */}
      {msg && (
        <div style={{
          background: alertas.length === 0 ? '#e6f7f2' : '#fef3e2',
          color: alertas.length === 0 ? '#1a9e6e' : 'var(--color-warning)',
          border: `1px solid ${alertas.length === 0 ? '#1a9e6e40' : '#d4890a40'}`,
          borderRadius: 8, padding: '10px 16px', fontSize: 13,
        }}>
          {msg}
        </div>
      )}

      {/* Resultados */}
      {loading ? (
        <div className="loading-center"><div className="spinner" /> Analizando…</div>
      ) : alertas.length > 0 && (
        <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
          <div style={{ padding: '14px 20px', borderBottom: '1px solid var(--color-sand)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <div className="section-title" style={{ marginBottom: 0 }}>Anomalías detectadas en: {modulo}</div>
            </div>
            <DSBadge icon="⚡" label="PriorityQueue" color="var(--color-danger)" />
          </div>
          <table className="data-table">
            <thead>
              <tr>
                <th>Nivel</th>
                <th>Tipo</th>
                <th>Descripción</th>
                <th>Entidad</th>
                <th>Fecha</th>
              </tr>
            </thead>
            <tbody>
              {alertas.map((a) => {
                const color = { CRITICA: 'var(--color-danger)', ALTA: '#f97316', MEDIA: 'var(--color-warning)', BAJA: 'var(--color-text-muted)' }[a.nivel]
                return (
                  <tr key={a.idAlerta}>
                    <td><StatusBadge value={a.nivel} /></td>
                    <td><DSBadge icon="🔍" label={a.tipo?.replace(/_/g, ' ')} color={color} /></td>
                    <td style={{ fontSize: 13 }}>{a.descripcion}</td>
                    <td><span className="mono" style={{ fontSize: 11, color: 'var(--color-accent)' }}>{a.entidadRelacionada}</span></td>
                    <td><span className="mono" style={{ fontSize: 10, color: 'var(--color-text-muted)' }}>
                      {a.fecha ? new Date(a.fecha).toLocaleDateString('es-CO') : '—'}
                    </span></td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
      )}

      {!loading && alertas.length === 0 && !msg && (
        <div className="empty-state">
          <ShieldAlert size={48} />
          <div>Selecciona un módulo de detección para analizar el sistema.</div>
          <div style={{ fontSize: 12 }}>Los análisis se ejecutan sobre los datos actuales en memoria.</div>
        </div>
      )}
    </div>
  )
}
