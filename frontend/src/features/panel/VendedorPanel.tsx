import { useAuth } from '../../shared/auth/AuthContext'

/** Panel del VENDEDOR: gestión de su catálogo de productos (HU3-5, pendientes). */
export default function VendedorPanel() {
  const { sesion } = useAuth()
  return (
    <section>
      <h2>Panel de vendedor — {sesion?.nombre}</h2>
      <p>Gestiona los productos de tu marca y su ficha de ingredientes (INCI).</p>
      <div className="panel-grid">
        <div className="card tile">📦 Mis productos <small>(HU3-4)</small></div>
        <div className="card tile">🧾 Ficha INCI e INVIMA <small>(HU4)</small></div>
        <div className="card tile">📈 Inventario y stock <small>(HU9)</small></div>
      </div>
      <p className="nota">Funcionalidad en construcción: esta vista quedará habilitada al implementar el módulo de catálogo.</p>
    </section>
  )
}
