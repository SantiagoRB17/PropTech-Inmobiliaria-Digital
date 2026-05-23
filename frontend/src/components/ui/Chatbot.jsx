import { useState, useRef, useEffect } from 'react'
import { MessageCircle, X, Send } from 'lucide-react'
import { sendChatMessage } from '../../api/index'

const MENSAJE_INICIAL = {
  role: 'assistant',
  content: 'Hola, soy el asistente de PropTech. ¿En qué te puedo ayudar?'
}

export default function Chatbot() {
  const [open, setOpen] = useState(false)
  const [messages, setMessages] = useState([MENSAJE_INICIAL])
  const [input, setInput] = useState('')
  const [loading, setLoading] = useState(false)
  const bottomRef = useRef(null)

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages, loading])

  const send = async () => {
    if (!input.trim() || loading) return
    const texto = input.trim()
    setInput('')
    setMessages(prev => [...prev, { role: 'user', content: texto }])
    setLoading(true)
    try {
      const { data } = await sendChatMessage(texto)
      setMessages(prev => [...prev, { role: 'assistant', content: data.reply }])
    } catch {
      setMessages(prev => [...prev, {
        role: 'assistant',
        content: 'No pude conectarme con el asistente. Verifica que n8n esté activo y el flujo publicado.'
      }])
    } finally {
      setLoading(false)
    }
  }

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      send()
    }
  }

  return (
    <>
      {open && (
        <div style={{
          position: 'fixed', bottom: '88px', right: '24px',
          width: '360px', height: '480px',
          background: 'var(--color-surface)',
          borderRadius: 'var(--radius-card)',
          boxShadow: '0 8px 32px rgba(0,0,0,0.2)',
          display: 'flex', flexDirection: 'column',
          zIndex: 1000, overflow: 'hidden',
          border: '1px solid var(--color-sand)'
        }}>

          {/* Header */}
          <div style={{
            background: 'var(--color-bg-dark)',
            padding: '14px 16px',
            display: 'flex', alignItems: 'center', justifyContent: 'space-between',
            flexShrink: 0
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <MessageCircle size={16} color='var(--color-accent)' />
              <span style={{ color: '#fff', fontWeight: 600, fontSize: '14px' }}>
                Asistente PropTech
              </span>
            </div>
            <button
              onClick={() => setOpen(false)}
              style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--color-text-muted)', display: 'flex' }}
            >
              <X size={16} />
            </button>
          </div>

          {/* Mensajes */}
          <div style={{
            flex: 1, overflowY: 'auto', padding: '14px',
            display: 'flex', flexDirection: 'column', gap: '10px'
          }}>
            {messages.map((msg, i) => (
              <div
                key={i}
                style={{
                  alignSelf: msg.role === 'user' ? 'flex-end' : 'flex-start',
                  background: msg.role === 'user' ? 'var(--color-accent)' : 'var(--color-bg-light)',
                  color: msg.role === 'user' ? '#fff' : 'var(--color-text)',
                  padding: '9px 13px',
                  borderRadius: msg.role === 'user' ? '14px 14px 3px 14px' : '14px 14px 14px 3px',
                  maxWidth: '82%',
                  fontSize: '13px',
                  lineHeight: '1.55',
                  whiteSpace: 'pre-wrap',
                  wordBreak: 'break-word'
                }}
              >
                {msg.content}
              </div>
            ))}

            {loading && (
              <div style={{
                alignSelf: 'flex-start',
                background: 'var(--color-bg-light)',
                padding: '9px 13px',
                borderRadius: '14px 14px 14px 3px',
                fontSize: '13px',
                color: 'var(--color-text-muted)'
              }}>
                Escribiendo...
              </div>
            )}

            <div ref={bottomRef} />
          </div>

          {/* Input */}
          <div style={{
            padding: '10px 12px',
            borderTop: '1px solid var(--color-sand)',
            display: 'flex', gap: '8px', alignItems: 'flex-end',
            flexShrink: 0
          }}>
            <textarea
              value={input}
              onChange={e => setInput(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="Escribe tu consulta… (Enter para enviar)"
              rows={1}
              style={{
                flex: 1, resize: 'none',
                border: '1px solid var(--color-sand)',
                borderRadius: '8px',
                padding: '8px 10px',
                fontSize: '13px',
                fontFamily: 'DM Sans, sans-serif',
                outline: 'none',
                background: 'var(--color-bg-light)',
                color: 'var(--color-text)',
                lineHeight: '1.4'
              }}
            />
            <button
              onClick={send}
              disabled={!input.trim() || loading}
              style={{
                background: 'var(--color-accent)',
                border: 'none',
                borderRadius: '8px',
                padding: '8px 10px',
                cursor: input.trim() && !loading ? 'pointer' : 'not-allowed',
                opacity: input.trim() && !loading ? 1 : 0.45,
                color: '#fff',
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                flexShrink: 0
              }}
            >
              <Send size={15} />
            </button>
          </div>
        </div>
      )}

      {/* Burbuja flotante */}
      <button
        onClick={() => setOpen(o => !o)}
        title="Asistente PropTech"
        style={{
          position: 'fixed', bottom: '24px', right: '24px',
          width: '54px', height: '54px',
          background: 'var(--color-accent)',
          border: 'none',
          borderRadius: '50%',
          cursor: 'pointer',
          boxShadow: '0 4px 18px rgba(0,166,192,0.45)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          zIndex: 1001,
          color: '#fff',
          transition: 'transform 0.15s ease'
        }}
        onMouseEnter={e => e.currentTarget.style.transform = 'scale(1.08)'}
        onMouseLeave={e => e.currentTarget.style.transform = 'scale(1)'}
      >
        {open ? <X size={22} /> : <MessageCircle size={22} />}
      </button>
    </>
  )
}
