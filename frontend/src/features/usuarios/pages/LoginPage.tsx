import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../../shared/auth/AuthContext'
import { rutaDePanel } from '../../../shared/auth/auth'
import type { RolUsuario } from '../../../shared/types'

/**
 * Pantalla de inicio de sesión y registro (módulo usuarios).
 * Tras autenticarse, redirige al panel que corresponde al rol del usuario.
 */
export default function LoginPage() {
  const { entrar, registrar } = useAuth()
  const navigate = useNavigate()

  const [modo, setModo] = useState<'login' | 'registro'>('login')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [nombre, setNombre] = useState('')
  const [rol, setRol] = useState<RolUsuario>('CLIENTE')
  const [error, setError] = useState<string | null>(null)
  const [cargando, setCargando] = useState(false)

  async function onSubmit(e: FormEvent) {
    e.preventDefault()
    setError(null)
    setCargando(true)
    try {
      if (modo === 'registro') {
        await registrar({ email, nombre, password, rol })
      }
      const sesion = await entrar(email, password)
      navigate(rutaDePanel(sesion.panel), { replace: true })
    } catch (err: unknown) {
      setError(mensajeDeError(err, modo))
    } finally {
      setCargando(false)
    }
  }

  return (
    <section className="auth">
      <h2>{modo === 'login' ? 'Iniciar sesión' : 'Crear cuenta'}</h2>

      <form onSubmit={onSubmit} className="card auth-form">
        {modo === 'registro' && (
          <label>
            Nombre
            <input value={nombre} onChange={(e) => setNombre(e.target.value)} required />
          </label>
        )}

        <label>
          Correo
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </label>

        <label>
          Contraseña
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            minLength={modo === 'registro' ? 8 : undefined}
            required
          />
        </label>

        {modo === 'registro' && (
          <label>
            Tipo de cuenta
            <select value={rol} onChange={(e) => setRol(e.target.value as RolUsuario)}>
              <option value="CLIENTE">Cliente (compra y perfil)</option>
              <option value="VENDEDOR">Vendedor (gestión de catálogo)</option>
              <option value="ADMIN">Administrador</option>
            </select>
          </label>
        )}

        {error && <p className="error">{error}</p>}

        <button type="submit" disabled={cargando}>
          {cargando ? 'Procesando…' : modo === 'login' ? 'Entrar' : 'Registrarme'}
        </button>
      </form>

      <p className="auth-switch">
        {modo === 'login' ? '¿No tienes cuenta?' : '¿Ya tienes cuenta?'}{' '}
        <button
          type="button"
          className="link"
          onClick={() => {
            setModo(modo === 'login' ? 'registro' : 'login')
            setError(null)
          }}
        >
          {modo === 'login' ? 'Regístrate' : 'Inicia sesión'}
        </button>
      </p>
    </section>
  )
}

function mensajeDeError(err: unknown, modo: 'login' | 'registro'): string {
  const e = err as { response?: { status?: number; data?: { message?: string } } }
  const status = e?.response?.status
  if (status === 401) return 'Correo o contraseña incorrectos.'
  if (status === 422) return e?.response?.data?.message ?? 'Ese correo ya está registrado.'
  if (status === 400) return 'Revisa los datos: la contraseña debe tener al menos 8 caracteres.'
  return modo === 'login' ? 'No se pudo iniciar sesión.' : 'No se pudo crear la cuenta.'
}
