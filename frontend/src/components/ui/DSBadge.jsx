/**
 * DSBadge — muestra qué estructura de datos alimenta un panel.
 * Aparece en cada sección para conectar la UI con el concepto académico.
 *
 * Props:
 *   icon  — emoji o símbolo (ej: "🌳", "📦")
 *   label — nombre + complejidad (ej: "BST O(log n)")
 *   color — color hexadecimal del acento (default: turquesa)
 */
export default function DSBadge({ icon = '⚡', label, color = '#00a6c0' }) {
  return (
    <span
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        gap: 5,
        padding: '3px 8px',
        background: color + '18',
        border: `1px solid ${color}40`,
        borderRadius: 6,
        fontFamily: "'JetBrains Mono', monospace",
        fontSize: 10,
        color,
        whiteSpace: 'nowrap',
      }}
    >
      {icon} {label}
    </span>
  )
}
