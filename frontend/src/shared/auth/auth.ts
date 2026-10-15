import { apiClient, guardarToken, borrarToken } from '../api/apiClient'
import type { RolUsuario } from '../types'

/** Panel al que entra un usuario según su rol principal. */
export type Panel = RolUsuario // 'CLIENTE' | 'VENDEDOR' | 'ADMIN'

export interface SesionUsuario {
  usuarioId: number
  nombre: string
  email: string
  roles: RolUsuario[]
  panel: Panel
}

interface LoginResponse extends SesionUsuario {
  token: string
  tipo: string
}

const USER_KEY = 'glossup_usuario'

/** Inicia sesión: guarda el token (para el interceptor Axios) y los datos del usuario. */
export async function login(email: string, password: string): Promise<SesionUsuario> {
  const { data } = await apiClient.post<LoginResponse>('/auth/login', { email, password })
  guardarToken(data.token)
  const sesion: SesionUsuario = {
    usuarioId: data.usuarioId,
    nombre: data.nombre,
    email: data.email,
    roles: data.roles,
    panel: data.panel,
  }
  try {
    localStorage.setItem(USER_KEY, JSON.stringify(sesion))
  } catch {
    /* almacenamiento no disponible: la sesión vivirá solo en memoria de React */
  }
  return sesion
}

/** Registra un usuario nuevo (no inicia sesión automáticamente). */
export async function registrar(input: {
  email: string
  nombre: string
  password: string
  rol?: RolUsuario
}): Promise<void> {
  await apiClient.post('/auth/register', input)
}

export function logout() {
  borrarToken()
  try {
    localStorage.removeItem(USER_KEY)
  } catch {
    /* ignore */
  }
}

/** Recupera la sesión persistida (si la hay). */
export function sesionActual(): SesionUsuario | null {
  try {
    const raw = localStorage.getItem(USER_KEY)
    return raw ? (JSON.parse(raw) as SesionUsuario) : null
  } catch {
    return null
  }
}

/** Ruta del panel correspondiente a un rol. */
export function rutaDePanel(panel: Panel): string {
  switch (panel) {
    case 'ADMIN':
      return '/admin'
    case 'VENDEDOR':
      return '/vendedor'
    default:
      return '/cliente'
  }
}
