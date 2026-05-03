import DSBadge from './DSBadge'

/**
 * KpiCard — tarjeta de métrica principal para el dashboard.
 *
 * Props:
 *   icon       — componente Lucide React (ej: <Building2 size={20}/>)
 *   label      — descripción del indicador
 *   value      — valor numérico a mostrar
 *   color      — color del acento
 *   dsIcon     — emoji de la estructura de datos
 *   dsLabel    — texto del badge de estructura
 *   delta      — texto de cambio/contexto (opcional)
 */
export default function KpiCard({ icon, label, value, color, dsIcon, dsLabel, delta }) {
  return (
    <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div
          style={{
            width: 40, height: 40,
            borderRadius: 10,
            background: color + '18',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            color,
          }}
        >
          {icon}
        </div>
        <DSBadge icon={dsIcon} label={dsLabel} color={color} />
      </div>

      <div>
        <div style={{ fontSize: 30, fontWeight: 800, color: 'var(--color-text)', lineHeight: 1 }}>
          {value ?? '—'}
        </div>
        <div style={{ fontSize: 12, color: 'var(--color-text-muted)', marginTop: 4 }}>{label}</div>
      </div>

      {delta && (
        <div style={{ fontSize: 11, color, fontWeight: 500 }}>{delta}</div>
      )}
    </div>
  )
}
