import { expect, Page, test } from '@playwright/test'

const inspeccion = {
  id: 1,
  sitioId: 1,
  sitioNombre: 'Sitio Central',
  inspector: 'inspector@demo',
  fechaInicio: '2026-10-18T10:00:00',
  fechaFin: null,
  latitud: 40.0,
  longitud: -3.0,
  estado: 'BORRADOR',
  notas: 'prueba',
  respuestas: [
    { itemId: 10, texto: 'Puerta correcta', orden: 1, valor: null, comentario: null },
    { itemId: 11, texto: 'Extintores revisados', orden: 2, valor: null, comentario: null },
  ],
  adjuntos: [],
}

const plantillas = [
  {
    id: 5,
    nombre: 'Checklist seguridad',
    items: [
      { id: 10, texto: 'Puerta correcta', orden: 1, tipo: 'SI' },
      { id: 11, texto: 'Extintores revisados', orden: 2, tipo: 'SI' },
    ],
  },
]

function mockearApi(page: Page) {
  const estado: typeof inspeccion = {
    ...inspeccion,
    respuestas: inspeccion.respuestas.map((r) => ({ ...r })),
  }

  page.route('**/api/inspecciones**', (route) => {
    const method = route.request().method()
    const url = route.request().url()
    if (method === 'GET') {
      void route.fulfill({
        json: url.includes('/api/inspecciones/') ? estado : { content: [estado], totalElements: 1, totalPages: 1, number: 0, size: 20 },
      })
    } else if (url.includes('/enviar')) {
      estado.estado = 'ENVIADA'
      void route.fulfill({ json: estado })
    } else {
      void route.fulfill({ json: estado })
    }
  })

  page.route('**/api/inspecciones/1/items/*/respuesta', (route) => {
    const body = route.request().postDataJSON() as { valor: string }
    const respuesta = estado.respuestas.find((r) => r.itemId === 10)
    if (respuesta) respuesta.valor = body.valor
    void route.fulfill({
      json: { itemId: 10, texto: 'Puerta correcta', orden: 1, valor: body.valor, comentario: null },
    })
  })

  page.route('**/api/plantillas', (route) => void route.fulfill({ json: plantillas }))
  page.route('**/api/sitios*', (route) =>
    void route.fulfill({
      json: {
        content: [{ id: 1, nombre: 'Sitio Central', direccion: null, contacto: null, activo: true }],
        totalElements: 1,
        totalPages: 1,
        number: 0,
        size: 50,
      },
    }),
  )
}

test('navegacion completa de inspeccion sin autenticacion', async ({ page }) => {
  mockearApi(page)

  await page.goto('/')
  await expect(page.getByRole('heading', { name: 'Inspecciones' })).toBeVisible()
  await expect(page.getByText('Sitio Central').first()).toBeVisible()

  await page.getByTestId('nueva-inspeccion').click()
  await expect(page.getByRole('heading', { name: 'Nueva inspección' })).toBeVisible()

  await page.getByTestId('sitio-1').click()
  await page.getByTestId('plantilla-5').click()
  await page.getByTestId('crear-inspeccion').click()

  await expect(page.getByTestId('estado')).toHaveText('Borrador')
  await expect(page.getByText('Puerta correcta')).toBeVisible()

  await page.getByTestId('respuesta-10-SI').click()
  await expect(page.getByTestId('respuesta-10-SI')).toBeDisabled()

  await page.getByTestId('enviar-inspeccion').click()
  await expect(page.getByTestId('estado')).toHaveText('Enviada')
})
