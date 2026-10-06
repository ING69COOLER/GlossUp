/**
 * Tipos de dominio compartidos, alineados con las entidades del backend (V1__init.sql).
 * Se irán ampliando a medida que se implementen las Historias de Usuario.
 */

export type TipoPiel = 'NORMAL' | 'SECA' | 'GRASA' | 'MIXTA' | 'SENSIBLE'
export type Severidad = 'BAJA' | 'MEDIA' | 'ALTA'
export type RolUsuario = 'CLIENTE' | 'VENDEDOR' | 'ADMIN'

export interface Usuario {
  id: number
  email: string
  nombre: string
  roles: RolUsuario[]
}

export interface PerfilDermatologico {
  id: number
  usuarioId: number
  ph?: number
  tipoPiel?: TipoPiel
  tipoCabello?: string
  afecciones: string[]
  alergias: string[]
}

export interface Ingrediente {
  id: number
  nombreInci: string
  descripcion?: string
}

export interface Producto {
  id: number
  nombre: string
  marca?: string
  categoria?: string
  precio: number
  stock: number
  ph?: number
  registroInvima?: string
  ingredientes: Ingrediente[]
}

export interface Alerta {
  severidad: Severidad
  mensaje: string
  causa: string
}

export interface ResultadoCompatibilidad {
  compatible: boolean
  alertas: Alerta[]
}
