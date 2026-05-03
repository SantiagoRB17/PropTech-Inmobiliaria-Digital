/**
 * StatusBadge — pill con colores semánticos para estados de entidades.
 * Cubre: disponibilidad de inmuebles, estados de visita, niveles de alerta, etc.
 */

const PALETA = {
  // Disponibilidad inmuebles
  DISPONIBLE:       { bg: '#e6f7f2', color: '#1a9e6e', border: '#1a9e6e40' },
  NO_DISPONIBLE:    { bg: '#fdecea', color: '#c0392b', border: '#c0392b40' },

  // Estados de visita
  PENDIENTE:        { bg: '#283b4815', color: '#283b48', border: '#283b4840' },
  CONFIRMADA:       { bg: '#e6f7f2',   color: '#1a9e6e', border: '#1a9e6e40' },
  REALIZADA:        { bg: '#e6f7f2',   color: '#1a9e6e', border: '#1a9e6e40' },
  CANCELADA:        { bg: '#fdecea',   color: '#c0392b', border: '#c0392b40' },
  REPROGRAMADA:     { bg: '#fff3e0',   color: '#f97316', border: '#f9731640' },

  // Niveles de alerta
  CRITICA:          { bg: '#fdecea',   color: '#c0392b', border: '#c0392b40' },
  ALTA:             { bg: '#fff3e0',   color: '#f97316', border: '#f9731640' },
  MEDIA:            { bg: '#fef3e2',   color: '#d4890a', border: '#d4890a40' },
  BAJA:             { bg: '#283b4815', color: '#283b48', border: '#283b4840' },

  // Estado de operación
  EN_PROCESO:       { bg: '#e8f4fd',   color: '#0369a1', border: '#0369a140' },
  CERRADA:          { bg: '#e6f7f2',   color: '#1a9e6e', border: '#1a9e6e40' },

  // Estado de búsqueda cliente
  ACTIVO:           { bg: '#e6f7f2',   color: '#1a9e6e', border: '#1a9e6e40' },
  PAUSADO:          { bg: '#fef3e2',   color: '#d4890a', border: '#d4890a40' },
  CERRADO:          { bg: '#283b4815', color: '#283b48', border: '#283b4840' },

  // Tipo cliente
  VIP:              { bg: '#fef9e7',   color: '#d4890a', border: '#d4890a40' },
  COMPRADOR:        { bg: '#e8f4fd',   color: '#0369a1', border: '#0369a140' },
  ARRENDATARIO:     { bg: '#f0f4ff',   color: '#4f46e5', border: '#4f46e540' },

  // Finalidad inmueble
  VENTA:            { bg: '#e8f4fd',   color: '#0369a1', border: '#0369a140' },
  ARRIENDO:         { bg: '#f0f4ff',   color: '#4f46e5', border: '#4f46e540' },
}

export default function StatusBadge({ value }) {
  if (!value) return null
  const key = String(value).toUpperCase().replace(/ /g, '_')
  const estilo = PALETA[key] || { bg: '#f0efeb', color: '#6b7f8e', border: '#d8d7ce' }

  return (
    <span
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        padding: '3px 10px',
        borderRadius: 20,
        border: `1px solid ${estilo.border}`,
        background: estilo.bg,
        color: estilo.color,
        fontSize: 11,
        fontWeight: 600,
        whiteSpace: 'nowrap',
      }}
    >
      {value}
    </span>
  )
}
