import { useEffect, useRef, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '@/lib/api'
import type { InspeccionResponse, TipoRespuesta } from '@/lib/api'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Separator } from '@/components/ui/separator'

const valores: { valor: TipoRespuesta; texto: string }[] = [
  { valor: 'SI', texto: 'Sí' },
  { valor: 'NO', texto: 'No' },
  { valor: 'NA', texto: 'N/A' },
]

export default function InspeccionDetallePage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [inspeccion, setInspeccion] = useState<InspeccionResponse | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [fichero, setFichero] = useState<File | null>(null)
  const fileInput = useRef<HTMLInputElement>(null)

  const cargar = () =>
    api
      .obtenerInspeccion(Number(id))
      .then(setInspeccion)
      .catch((e) => setError(e instanceof Error ? e.message : 'Error al cargar'))

  useEffect(() => {
    cargar()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id])

  const responder = async (itemId: number, valor: TipoRespuesta) => {
    try {
      await api.responder(Number(id), itemId, valor)
      await cargar()
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error al guardar la respuesta')
    }
  }

  const enviar = async () => {
    try {
      await api.enviar(Number(id))
      await cargar()
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error al enviar')
    }
  }

  const subirFoto = async () => {
    if (!fichero) return
    try {
      await api.adjuntar(Number(id), fichero)
      setFichero(null)
      if (fileInput.current) fileInput.current.value = ''
      await cargar()
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error al subir la foto')
    }
  }

  if (error) {
    return (
      <div className="mx-auto min-h-screen max-w-2xl px-4 py-6">
        <Button variant="ghost" onClick={() => navigate('/')}>
          ← Volver
        </Button>
        <p className="mt-4 text-destructive">{error}</p>
      </div>
    )
  }

  if (!inspeccion) {
    return (
      <div className="mx-auto min-h-screen max-w-2xl px-4 py-6">
        <p className="text-center text-muted-foreground">Cargando…</p>
      </div>
    )
  }

  const editable = inspeccion.estado === 'BORRADOR'
  const respondidos = inspeccion.respuestas.filter((r) => r.valor).length

  return (
    <div className="mx-auto min-h-screen max-w-2xl bg-background px-4 pb-24 pt-6">
      <Button variant="ghost" onClick={() => navigate('/')} data-testid="volver">
        ← Volver
      </Button>

      <div className="mt-2 mb-4 flex items-center justify-between">
        <h1 className="text-2xl font-semibold tracking-tight">{inspeccion.sitioNombre}</h1>
        <Badge data-testid="estado">
          {inspeccion.estado.charAt(0) + inspeccion.estado.slice(1).toLowerCase()}
        </Badge>
      </div>
      <p className="mb-4 text-sm text-muted-foreground">
        {respondidos}/{inspeccion.respuestas.length} ítems respondidos
      </p>

      <div className="flex flex-col gap-3">
        {inspeccion.respuestas.map((r) => (
          <Card key={r.itemId}>
            <CardContent className="p-4">
              <p className="text-base">{r.texto}</p>
              <div className="mt-3 flex gap-2">
                {valores.map(({ valor, texto }) => (
                  <button
                    key={valor}
                    data-testid={`respuesta-${r.itemId}-${valor}`}
                    disabled={!editable || r.valor === valor}
                    onClick={() => responder(r.itemId, valor)}
                    className={`min-h-12 flex-1 rounded-lg border text-sm font-medium ${r.valor === valor ? 'border-primary bg-primary text-primary-foreground' : 'bg-muted text-muted-foreground'}`}
                  >
                    {texto}
                  </button>
                ))}
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      <Separator className="my-6" />

      <Card className="mb-4">
        <CardContent className="p-4">
          <p className="mb-2 text-base font-medium">Fotos / adjuntos</p>
          {inspeccion.adjuntos.length === 0 ? (
            <p className="text-sm text-muted-foreground">Sin adjuntos</p>
          ) : (
            <ul className="mb-3 list-inside list-disc text-sm">
              {inspeccion.adjuntos.map((a) => (
                <li key={a.id}>{a.nombreArchivo}</li>
              ))}
            </ul>
          )}
          {editable && (
            <div className="flex items-center gap-3">
              <input
                ref={fileInput}
                type="file"
                accept="image/*"
                data-testid="input-foto"
                onChange={(e) => setFichero(e.target.files?.[0] ?? null)}
                className="text-sm"
              />
              <Button
                variant="secondary"
                data-testid="subir-foto"
                onClick={subirFoto}
                disabled={!fichero}
              >
                Subir foto
              </Button>
            </div>
          )}
        </CardContent>
      </Card>

      {editable && (
        <Button
          size="lg"
          className="fixed inset-x-4 bottom-6 mx-auto h-16 max-w-2xl text-lg"
          onClick={enviar}
          data-testid="enviar-inspeccion"
        >
          Enviar inspección
        </Button>
      )}
    </div>
  )
}
