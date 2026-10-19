import { obtenerAccessToken } from './msal'

const BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:1419'

export type EstadoInspeccion = 'BORRADOR' | 'ENVIADA'
export type TipoRespuesta = 'SI' | 'NO' | 'NA'

export interface ItemResponse {
  id: number
  texto: string
  orden: number
  tipo: string
}

export interface PlantillaResponse {
  id: number
  nombre: string
  items: ItemResponse[]
}

export interface RespuestaResponse {
  itemId: number
  texto: string
  orden: number
  valor: string | null
  comentario: string | null
}

export interface AdjuntoResponse {
  id: number
  nombreArchivo: string
  url: string
  subidoEn: string
}

export interface InspeccionResponse {
  id: number
  sitioId: number
  sitioNombre: string
  inspector: string
  fechaInicio: string
  fechaFin: string | null
  latitud: number | null
  longitud: number | null
  estado: EstadoInspeccion
  notas: string | null
  respuestas: RespuestaResponse[]
  adjuntos: AdjuntoResponse[]
}

export interface SitioResponse {
  id: number
  nombre: string
  direccion: string | null
  contacto: string | null
  activo: boolean
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export class ApiError extends Error {
  status: number

  constructor(status: number, message: string) {
    super(message)
    this.status = status
  }
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = await obtenerAccessToken()
  const headers: Record<string, string> = { ...(options.headers as Record<string, string> | undefined) }
  if (token) headers.Authorization = `Bearer ${token}`

  const res = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers,
  })

  if (res.status === 401) {
    const ErrorNoAuth = new ApiError(401, 'Sesión no válida. Vuelve a iniciar sesión.')
    // limpia storage para forzar nuevo login en el siguiente intento
    tokensLimpios()
    throw ErrorNoAuth
  }

  if (!res.ok) {
    let mensaje = res.statusText
    try {
      const body = await res.json()
      if (body?.mensaje ?? body?.message) mensaje = body.mensaje ?? body.message
    } catch {
      // respuesta sin cuerpo JSON
    }
    throw new ApiError(res.status, mensaje)
  }

  if (res.status === 204) return undefined as T
  return res.json() as Promise<T>
}

function tokensLimpios() {
  try {
    for (const key of Object.keys(localStorage)) {
      if (key.startsWith('msal.')) localStorage.removeItem(key)
    }
  } catch {
    // storage no disponible
  }
}

export const api = {
  listarInspecciones: (estado?: EstadoInspeccion, page = 0, size = 20) => {
    const params = new URLSearchParams({ page: String(page), size: String(size) })
    if (estado) params.set('estado', estado)
    return request<Page<InspeccionResponse>>(`/api/inspecciones?${params}`)
  },
  obtenerInspeccion: (id: number) => request<InspeccionResponse>(`/api/inspecciones/${id}`),
  crearInspeccion: (body: { sitioId: number; plantillaId: number; notas?: string; latitud?: number; longitud?: number }) =>
    request<InspeccionResponse>('/api/inspecciones', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    }),
  responder: (inspeccionId: number, itemId: number, valor: TipoRespuesta, comentario?: string) =>
    request<RespuestaResponse>(`/api/inspecciones/${inspeccionId}/items/${itemId}/respuesta`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ valor, comentario }),
    }),
  enviar: (id: number) =>
    request<InspeccionResponse>(`/api/inspecciones/${id}/enviar`, { method: 'POST' }),
  adjuntar: (id: number, archivo: File) => {
    const form = new FormData()
    form.append('archivo', archivo)
    return request<AdjuntoResponse>(`/api/inspecciones/${id}/adjuntos`, {
      method: 'POST',
      body: form,
    })
  },
  listarPlantillas: () => request<PlantillaResponse[]>('/api/plantillas'),
  listarSitios: (page = 0, size = 50) => request<Page<SitioResponse>>(`/api/sitios?page=${page}&size=${size}`),
}
