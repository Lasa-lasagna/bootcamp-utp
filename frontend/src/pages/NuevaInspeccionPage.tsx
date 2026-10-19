import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '@/lib/api'
import type { InspeccionResponse, PlantillaResponse, SitioResponse } from '@/lib/api'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'

interface Ubicacion {
  latitud: number
  longitud: number
}

export default function NuevaInspeccionPage() {
  const [sitios, setSitios] = useState<SitioResponse[] | null>(null)
  const [plantillas, setPlantillas] = useState<PlantillaResponse[] | null>(null)
  const [sitioId, setSitioId] = useState<number | null>(null)
  const [plantillaId, setPlantillaId] = useState<number | null>(null)
  const [notas, setNotas] = useState('')
  const [ubicacion, setUbicacion] = useState<Ubicacion | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [creando, setCreando] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    api.listarSitios().then((p) => setSitios(p.content)).catch(() => setSitios([]))
    api.listarPlantillas().then(setPlantillas).catch(() => setPlantillas([]))
    if ('geolocation' in navigator) {
      navigator.geolocation.getCurrentPosition(
        (pos) =>
          setUbicacion({ latitud: pos.coords.latitude, longitud: pos.coords.longitude }),
        () => undefined,
      )
    }
  }, [])

  const crear = async () => {
    if (!sitioId || !plantillaId) {
      setError('Selecciona un sitio y una plantilla')
      return
    }
    setCreando(true)
    try {
      const creada: InspeccionResponse = await api.crearInspeccion({
        sitioId,
        plantillaId,
        notas: notas || undefined,
        latitud: ubicacion?.latitud,
        longitud: ubicacion?.longitud,
      })
      navigate(`/inspecciones/${creada.id}`)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error al crear la inspección')
      setCreando(false)
    }
  }

  return (
    <div className="mx-auto min-h-screen max-w-2xl bg-background px-4 py-6">
      <Button variant="ghost" onClick={() => navigate('/')} data-testid="volver">
        ← Volver
      </Button>
      <h1 className="mb-4 text-2xl font-semibold tracking-tight">Nueva inspección</h1>

      <Card className="mb-4">
        <CardContent className="flex flex-col gap-4 p-4">
          <div>
            <Label htmlFor="sitio" className="mb-2 block">
              Sitio
            </Label>
            <div className="flex flex-col gap-2">
              {sitios === null ? (
                <p className="text-sm text-muted-foreground">Cargando…</p>
              ) : sitios.length === 0 ? (
                <p className="text-sm text-muted-foreground">Sin sitios activos</p>
              ) : (
                sitios.map((s) => (
                  <button
                    key={s.id}
                    data-testid={`sitio-${s.id}`}
                    onClick={() => setSitioId(s.id)}
                    className={`min-h-14 rounded-lg border px-4 text-left text-base ${sitioId === s.id ? 'border-primary bg-primary/10 font-medium' : ''}`}
                  >
                    {s.nombre}
                  </button>
                ))
              )}
            </div>
          </div>

          <div>
            <Label htmlFor="plantilla" className="mb-2 block">
              Plantilla de checklist
            </Label>
            <div className="flex flex-col gap-2">
              {plantillas === null ? (
                <p className="text-sm text-muted-foreground">Cargando…</p>
              ) : plantillas.length === 0 ? (
                <p className="text-sm text-muted-foreground">Sin plantillas</p>
              ) : (
                plantillas.map((p) => (
                  <button
                    key={p.id}
                    data-testid={`plantilla-${p.id}`}
                    onClick={() => setPlantillaId(p.id)}
                    className={`min-h-14 rounded-lg border px-4 text-left text-base ${plantillaId === p.id ? 'border-primary bg-primary/10 font-medium' : ''}`}
                  >
                    {p.nombre} ({p.items.length} ítems)
                  </button>
                ))
              )}
            </div>
          </div>

          <div>
            <Label htmlFor="notas" className="mb-2 block">
              Observaciones iniciales (opcional)
            </Label>
            <Textarea
              id="notas"
              value={notas}
              onChange={(e) => setNotas(e.target.value)}
              className="min-h-24 text-base"
            />
          </div>

          <p className="text-sm text-muted-foreground" data-testid="ubicacion">
            {ubicacion
              ? `📍 Ubicación capturada (${ubicacion.latitud.toFixed(5)}, ${ubicacion.longitud.toFixed(5)})`
              : '📍 Ubicación no capturada'}
          </p>

          {error && <p className="text-sm text-destructive">{error}</p>}
        </CardContent>
      </Card>

      <Button
        size="lg"
        className="h-16 w-full text-lg"
        onClick={crear}
        disabled={creando}
        data-testid="crear-inspeccion"
      >
        {creando ? 'Creando…' : 'Comenzar inspección'}
      </Button>
    </div>
  )
}
