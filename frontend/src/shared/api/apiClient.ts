import axios from 'axios'

/**
 * Cliente HTTP central para hablar con el backend GlossUP.
 *
 * La URL base se toma de la variable de entorno VITE_API_URL; por defecto usa "/api",
 * que en desarrollo el proxy de Vite redirige al backend en http://localhost:8080.
 */
export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? '/api',
  headers: { 'Content-Type': 'application/json' },
})

// Clave con la que se guarda el token JWT en el navegador
const TOKEN_KEY = 'glossup_token'

export function guardarToken(token: string) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function obtenerToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

export function borrarToken() {
  localStorage.removeItem(TOKEN_KEY)
}

// Adjunta automáticamente el token Bearer a cada petición si existe (RNF-11)
apiClient.interceptors.request.use((config) => {
  const token = obtenerToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Si el backend responde 401, limpia el token (sesión expirada)
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      borrarToken()
    }
    return Promise.reject(error)
  },
)
