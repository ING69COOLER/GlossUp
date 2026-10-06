import { useEffect, useState } from 'react'
import { ping, type PingResponse } from '../shared/api/health'

/** Página de inicio: muestra el estado de conexión con el backend. */
export default function HomePage() {
  const [estado, setEstado] = useState<PingResponse | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    ping()
      .then(setEstado)
      .catch(() => setError('No se pudo conectar con el backend (¿está arriba en :8080?)'))
  }, [])

  return (
    <section>
      <h2>Bienvenido a GlossUP</h2>
      <p>Ecommerce de cosmética con motor de compatibilidad dermatológica.</p>

      <div className="card">
        <strong>Estado del backend:</strong>{' '}
        {estado && <span style={{ color: 'green' }}>{estado.status} ✓ ({estado.service})</span>}
        {error && <span style={{ color: 'crimson' }}>{error}</span>}
        {!estado && !error && <span>comprobando…</span>}
      </div>
    </section>
  )
}
