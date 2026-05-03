import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import StatusBadge from '../components/ui/StatusBadge'

describe('StatusBadge', () => {
  it('muestra DISPONIBLE', () => {
    render(<StatusBadge value="DISPONIBLE" />)
    expect(screen.getByText('DISPONIBLE')).toBeInTheDocument()
  })

  it('muestra CRITICA', () => {
    render(<StatusBadge value="CRITICA" />)
    expect(screen.getByText('CRITICA')).toBeInTheDocument()
  })

  it('no renderiza cuando value es null', () => {
    const { container } = render(<StatusBadge value={null} />)
    expect(container.firstChild).toBeNull()
  })

  it('aplica color verde para DISPONIBLE', () => {
    const { container } = render(<StatusBadge value="DISPONIBLE" />)
    const badge = container.firstChild
    expect(badge).toHaveStyle({ color: '#1a9e6e' })
  })

  it('aplica color rojo para CRITICA', () => {
    const { container } = render(<StatusBadge value="CRITICA" />)
    const badge = container.firstChild
    expect(badge).toHaveStyle({ color: '#c0392b' })
  })
})
