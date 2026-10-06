import { BrowserRouter, Link, Navigate, Route, Routes } from 'react-router-dom'
import HomePage from './HomePage'
import LoginPage from '../features/usuarios/pages/LoginPage'
import PerfilPage from '../features/perfil/pages/PerfilPage'
import CatalogoPage from '../features/catalogo/pages/CatalogoPage'
import CompatibilidadPage from '../features/compatibilidad/pages/CompatibilidadPage'
import CarritoPage from '../features/recomendacion/pages/CarritoPage'

/** Shell de la aplicación: barra de navegación + enrutado de las features. */
export default function App() {
  return (
    <BrowserRouter>
      <header className="topbar">
        <Link to="/" className="brand">GlossUP</Link>
        <nav>
          <Link to="/catalogo">Catálogo</Link>
          <Link to="/compatibilidad">Compatibilidad</Link>
          <Link to="/perfil">Mi perfil</Link>
          <Link to="/carrito">Carrito</Link>
          <Link to="/login">Entrar</Link>
        </nav>
      </header>

      <main className="container">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/perfil" element={<PerfilPage />} />
          <Route path="/catalogo" element={<CatalogoPage />} />
          <Route path="/compatibilidad" element={<CompatibilidadPage />} />
          <Route path="/carrito" element={<CarritoPage />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </BrowserRouter>
  )
}
