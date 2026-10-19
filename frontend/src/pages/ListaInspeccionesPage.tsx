import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api } from '@/lib/api'
import type { EstadoInspeccion, InspeccionResponse } from '@/lib/api'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'

const estados: (EstadoInspeccion | 'TODAS')[] = ['TODAS', 'BORRADOR', 'ENVIADA']

export default function ListaInspeccionesPage() {
  const [estado, setEstado] = useState<(typeof estados)[number]>('TODAS')
  const [inspecciones, setInspecciones] = useState<InspeccionResponse[] | null>(null)
  const navigate = useNavigate()

  useEffect(() => {
    api
      .listarInspecciones(estado === 'TODAS' ? undefined : estado)
      .then((page) => setInspecciones(page.content))
      .catch(() => setInspecciones([]))
  }, [estado])

  return (
    <div className="mx-auto min-h-screen max-w-2xl bg-background px-4 pb-24 pt-6">
      <h1 className="mb-4 text-2xl font-semibold tracking-tight">Inspecciones</h1>

      <div className="mb-4 flex gap-2" role="tablist" aria-label="Filtrar por estado">
        {estados.map((e) => (
          <button
            key={e}
            role="tab"
            aria-selected={estado === e}
            onClick={() => setEstado(e)}
            className={`min-h-12 flex-1 rounded-lg border px-3 text-sm font-medium ${estado === e ? 'bg-primary text-primary-foreground' : 'bg-muted text-muted-foreground'}`}
          >
            {e.charAt(0) + e.slice(1).toLowerCase()}
          </button>
        ))}
      </div>

      <div className="flex flex-col gap-3">
        {inspecciones === null ? (
          <p className="text-center text-muted-foreground">Cargando…</p>
        ) : inspecciones.length === 0 ? (
          <p className="text-center text-muted-foreground">Sin inspecciones</p>
        ) : (
          inspecciones.map((i) => (
            <Card key={i.id}>
              <CardContent
                className="cursor-pointer p-4"
                data-testid={`inspeccion-${i.id}`}
                onClick={() => navigate(`/inspecciones/${i.id}`)}
              >
                <div className="flex items-center justify-between">
                  <span className="text-base font-medium">{i.sitioNombre}</span>
                  <Badge variant={i.estado === 'ENVIADA' ? 'default' : 'outline'}>
                    {i.estado.charAt(0) + i.estado.slice(1).toLowerCase()}
                  </Badge>
                </div>
                <p className="mt-1 text-sm text-muted-foreground">
                  {i.inspector} · {new Date(i.fechaInicio).toLocaleDateString('es-ES')}
                </p>
              </CardContent>
            </Card>
          ))
        )}
      </div>

      <Button
        asChild
        size="lg"
        className="fixed inset-x-4 bottom-6 max-w-2xl mx-auto h-16 text-lg"
        data-testid="nueva-inspeccion"
      >
        <Link to="/inspecciones/nueva">+ Nueva inspección</Link>
      </Button>
    </div>
  )
}
