import { apiClient } from '../../../shared/api/apiClient'

/** Ingrediente de la ficha INCI de un producto. */
export interface IngredienteFicha {
  nombreInci: string
  funcion?: string | null
  concentracion?: number | null
}

/** Producto tal como lo devuelve la API del catálogo del vendedor. */
export interface ProductoVendedor {
  id: number
  vendedorId: number
  nombre: string
  marca?: string | null
  categoria?: string | null
  precio: number
  stock: number
  ph?: number | null
  registroInvima?: string | null
  imagenUrl?: string | null
  activo: boolean
  ingredientes: IngredienteFicha[]
}

/** Datos del formulario de crear/editar (el stock se gestiona aparte en edición). */
export interface DatosProducto {
  nombre: string
  marca?: string
  categoria?: string
  precio: number
  stock: number
  ph?: number | null
  registroInvima?: string
  imagenUrl?: string
}

const BASE = '/catalogo/productos'

export async function listarMisProductos(): Promise<ProductoVendedor[]> {
  const { data } = await apiClient.get<ProductoVendedor[]>(`${BASE}/mios`)
  return data
}

export async function crearProducto(d: DatosProducto): Promise<ProductoVendedor> {
  const { data } = await apiClient.post<ProductoVendedor>(BASE, d)
  return data
}

export async function editarProducto(id: number, d: Omit<DatosProducto, 'stock'>): Promise<ProductoVendedor> {
  const { data } = await apiClient.put<ProductoVendedor>(`${BASE}/${id}`, d)
  return data
}

export async function actualizarStock(id: number, stock: number): Promise<ProductoVendedor> {
  const { data } = await apiClient.put<ProductoVendedor>(`${BASE}/${id}/stock`, { stock })
  return data
}

export async function agregarIngrediente(
  id: number,
  nombreInci: string,
  concentracion?: number | null,
  funcion?: string | null,
): Promise<ProductoVendedor> {
  const { data } = await apiClient.post<ProductoVendedor>(`${BASE}/${id}/ingredientes`, {
    nombreInci,
    concentracion,
    funcion,
  })
  return data
}

export async function quitarIngrediente(id: number, inci: string): Promise<ProductoVendedor> {
  const { data } = await apiClient.delete<ProductoVendedor>(`${BASE}/${id}/ingredientes`, { params: { inci } })
  return data
}

export async function publicarProducto(id: number): Promise<ProductoVendedor> {
  const { data } = await apiClient.post<ProductoVendedor>(`${BASE}/${id}/publicar`)
  return data
}

export async function despublicarProducto(id: number): Promise<ProductoVendedor> {
  const { data } = await apiClient.post<ProductoVendedor>(`${BASE}/${id}/despublicar`)
  return data
}

/** Extrae el mensaje de error de dominio que envía el backend (ApiError.message). */
export function mensajeDeError(e: unknown): string {
  const resp = (e as { response?: { data?: { message?: string; detalles?: string[] } } })?.response?.data
  if (resp?.detalles?.length) return resp.detalles.join('. ')
  return resp?.message ?? 'Ocurrió un error. Inténtalo de nuevo.'
}
