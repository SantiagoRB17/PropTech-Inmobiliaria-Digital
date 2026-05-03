import { useState, useMemo } from 'react'
import DSBadge from './DSBadge'

/**
 * GrafoPanel — visualización SVG interactiva del grafo cliente-inmueble.
 *
 * Recibe una lista de clientes (cada uno con inmueblesConsultados y favoritos)
 * y construye nodos + aristas para mostrar las relaciones.
 *
 * Nodos turquesa = clientes, nodos dorados = inmuebles.
 * Hover sobre un nodo resalta sus aristas y muestra tooltip.
 */
export default function GrafoPanel({ clientes = [], inmuebles = [] }) {
  const [hovered, setHovered] = useState(null)

  // Construir nodos y aristas desde los datos del cliente
  const { nodos, aristas } = useMemo(() => {
    const nodoMap = {}
    const aristasList = []
    const totalClientes = clientes.length

    // Nodos cliente en semicírculo izquierdo
    clientes.forEach((c, i) => {
      const angulo = (Math.PI / (totalClientes + 1)) * (i + 1) + Math.PI / 2
      nodoMap['CLI-' + c.identificacion] = {
        id: 'CLI-' + c.identificacion,
        label: c.nombre?.split(' ')[0] ?? c.identificacion,
        tipo: 'cliente',
        x: 180 + Math.cos(angulo) * 130,
        y: 200 + Math.sin(angulo) * 140,
      }
    })

    // Recopilar inmuebles únicos referenciados
    const codigosInmueble = new Set()
    clientes.forEach((c) => {
      ;(c.inmueblesConsultados || []).forEach((cod) => codigosInmueble.add(cod))
      ;(c.favoritos || []).forEach((cod) => codigosInmueble.add(cod))
    })

    const inmueblesArr = [...codigosInmueble]
    const totalInm = inmueblesArr.length

    // Nodos inmueble en semicírculo derecho
    inmueblesArr.forEach((cod, i) => {
      const angulo = (Math.PI / (totalInm + 1)) * (i + 1) - Math.PI / 2
      const inmueble = inmuebles.find((inm) => inm.codigo === cod)
      nodoMap[cod] = {
        id: cod,
        label: inmueble ? inmueble.barrio ?? cod : cod,
        tipo: 'inmueble',
        x: 420 + Math.cos(angulo) * 130,
        y: 200 + Math.sin(angulo) * 140,
      }
    })

    // Aristas: cliente → inmuebles que consultó o tiene en favoritos
    clientes.forEach((c) => {
      const cliId = 'CLI-' + c.identificacion
      const relacionados = new Set([
        ...(c.inmueblesConsultados || []),
        ...(c.favoritos || []),
      ])
      relacionados.forEach((cod) => {
        if (nodoMap[cod]) {
          aristasList.push({ origen: cliId, destino: cod })
        }
      })
    })

    return { nodos: Object.values(nodoMap), aristas: aristasList }
  }, [clientes, inmuebles])

  const nodoHovered = hovered ? nodos.find((n) => n.id === hovered) : null

  const aristaResaltada = (arista) =>
    hovered && (arista.origen === hovered || arista.destino === hovered)

  if (clientes.length === 0) {
    return (
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: 200, color: 'var(--color-text-muted)', fontSize: 13 }}>
        No hay datos para mostrar el grafo.
      </div>
    )
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
        <div>
          <div className="section-title">Grafo de Relaciones</div>
          <div className="section-subtitle">{nodos.length} nodos · {aristas.length} aristas</div>
        </div>
        <DSBadge icon="🔗" label="Grafo BFS — O(V+E)" color="#00a6c0" />
      </div>

      <div style={{ background: 'linear-gradient(135deg, #1a2130 0%, #283b48 100%)', borderRadius: 12, overflow: 'hidden' }}>
        <svg width="100%" viewBox="0 0 600 400" style={{ display: 'block' }}>
          <defs>
            <filter id="glow">
              <feGaussianBlur stdDeviation="3" result="coloredBlur" />
              <feMerge>
                <feMergeNode in="coloredBlur" />
                <feMergeNode in="SourceGraphic" />
              </feMerge>
            </filter>
          </defs>

          {/* Aristas */}
          {aristas.map((a, i) => {
            const origen = nodos.find((n) => n.id === a.origen)
            const destino = nodos.find((n) => n.id === a.destino)
            if (!origen || !destino) return null
            const resaltada = aristaResaltada(a)
            return (
              <line
                key={i}
                x1={origen.x} y1={origen.y}
                x2={destino.x} y2={destino.y}
                stroke={resaltada ? '#00a6c0' : '#00a6c060'}
                strokeWidth={resaltada ? 2.5 : 1.5}
              />
            )
          })}

          {/* Nodos */}
          {nodos.map((n) => {
            const isCliente = n.tipo === 'cliente'
            const color = isCliente ? '#00a6c0' : '#f0c040'
            const r = isCliente ? 22 : 18
            const esHovered = hovered === n.id

            return (
              <g
                key={n.id}
                onMouseEnter={() => setHovered(n.id)}
                onMouseLeave={() => setHovered(null)}
                style={{ cursor: 'pointer' }}
              >
                {/* Halo */}
                <circle cx={n.x} cy={n.y} r={r + 8} fill={color + '12'} />
                {/* Anillo */}
                <circle cx={n.x} cy={n.y} r={r + 3} fill="none" stroke={color + '50'} strokeWidth={1} />
                {/* Cuerpo */}
                <circle
                  cx={n.x} cy={n.y} r={r}
                  fill={color + '30'}
                  stroke={color}
                  strokeWidth={esHovered ? 3 : 2}
                  filter={esHovered ? 'url(#glow)' : undefined}
                />
                {/* Label */}
                <text
                  x={n.x} y={n.y + r + 13}
                  textAnchor="middle"
                  fill={color}
                  fontSize={9}
                  fontFamily="'JetBrains Mono', monospace"
                >
                  {n.label.length > 10 ? n.label.slice(0, 9) + '…' : n.label}
                </text>
              </g>
            )
          })}

          {/* Tooltip al hover */}
          {nodoHovered && (
            <g>
              <rect
                x={nodoHovered.x - 40} y={nodoHovered.y - 44}
                width={80} height={20}
                rx={4} fill="#00a6c0"
              />
              <text
                x={nodoHovered.x} y={nodoHovered.y - 30}
                textAnchor="middle"
                fill="white"
                fontSize={9}
                fontFamily="'JetBrains Mono', monospace"
              >
                {nodoHovered.id}
              </text>
            </g>
          )}
        </svg>
      </div>

      {/* Leyenda */}
      <div style={{ display: 'flex', gap: 16, marginTop: 10, fontSize: 11, color: 'var(--color-text-muted)' }}>
        <span style={{ display: 'flex', alignItems: 'center', gap: 5 }}>
          <svg width={12} height={12}><circle cx={6} cy={6} r={5} fill="#00a6c030" stroke="#00a6c0" strokeWidth={1.5} /></svg>
          Clientes
        </span>
        <span style={{ display: 'flex', alignItems: 'center', gap: 5 }}>
          <svg width={12} height={12}><circle cx={6} cy={6} r={5} fill="#f0c04030" stroke="#f0c040" strokeWidth={1.5} /></svg>
          Inmuebles
        </span>
      </div>
    </div>
  )
}
