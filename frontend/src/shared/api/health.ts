import { apiClient } from './apiClient'

export interface PingResponse {
  service: string
  status: string
  timestamp: string
}

/** Comprueba la conectividad con el backend (endpoint público /ping). */
export async function ping(): Promise<PingResponse> {
  const { data } = await apiClient.get<PingResponse>('/ping', { baseURL: '/' })
  return data
}
