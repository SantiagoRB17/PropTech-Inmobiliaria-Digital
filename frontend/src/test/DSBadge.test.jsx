import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import DSBadge from '../components/ui/DSBadge'

describe('DSBadge', () => {
  it('muestra el label correctamente', () => {
    render(<DSBadge icon="🌳" label="BST O(log n)" />)
    expect(screen.getByText(/BST O\(log n\)/)).toBeInTheDocument()
  })

  it('muestra el icono', () => {
    render(<DSBadge icon="📦" label="Cola FIFO" />)
    expect(screen.getByText(/📦/)).toBeInTheDocument()
  })

  it('usa el color por defecto cuando no se pasa color', () => {
    const { container } = render(<DSBadge icon="⚡" label="Stack" />)
    const badge = container.firstChild
    expect(badge).toHaveStyle({ color: '#00a6c0' })
  })

  it('aplica el color personalizado', () => {
    const { container } = render(<DSBadge icon="⚡" label="PriorityQueue" color="#c0392b" />)
    const badge = container.firstChild
    expect(badge).toHaveStyle({ color: '#c0392b' })
  })
})
