import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import GrafoPanel from '../components/ui/GrafoPanel'

const clientesMock = [
  {
    identificacion: '001',
    nombre: 'María García',
    inmueblesConsultados: ['INM-001'],
    favoritos: ['INM-001'],
  },
]

const inmueblesMock = [
  { codigo: 'INM-001', barrio: 'El Poblado', ciudad: 'Medellín' },
]

describe('GrafoPanel', () => {
  it('muestra mensaje cuando no hay clientes', () => {
    render(<GrafoPanel clientes={[]} inmuebles={[]} />)
    expect(screen.getByText(/No hay datos/i)).toBeInTheDocument()
  })

  it('renderiza el SVG cuando hay datos', () => {
    const { container } = render(
      <GrafoPanel clientes={clientesMock} inmuebles={inmueblesMock} />
    )
    expect(container.querySelector('svg')).toBeTruthy()
  })

  it('muestra título del grafo', () => {
    render(<GrafoPanel clientes={clientesMock} inmuebles={inmueblesMock} />)
    expect(screen.getByText(/Grafo de Relaciones/i)).toBeInTheDocument()
  })
})
