import { X } from 'lucide-react'

/**
 * Modal — contenedor genérico para formularios y confirmaciones.
 *
 * Props:
 *   open     — boolean, controla visibilidad
 *   onClose  — función al cerrar
 *   title    — string del encabezado
 *   children — contenido del cuerpo
 */
export default function Modal({ open, onClose, title, children }) {
  if (!open) return null
  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-box" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <span className="modal-title">{title}</span>
          <button
            onClick={onClose}
            style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--color-text-muted)' }}
          >
            <X size={18} />
          </button>
        </div>
        {children}
      </div>
    </div>
  )
}
