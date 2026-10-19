import { MsalProvider, useMsal, UnauthenticatedTemplate, AuthenticatedTemplate } from '@azure/msal-react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import './index.css'
import InspeccionDetallePage from './pages/InspeccionDetallePage'
import ListaInspeccionesPage from './pages/ListaInspeccionesPage'
import NuevaInspeccionPage from './pages/NuevaInspeccionPage'
import { msalEnabled, msalInstance, nombreUsuario } from './lib/msal'
import { Button } from './components/ui/button'

function Cabecera() {
  const { instance } = useMsal()

  return (
    <header className="mx-auto flex max-w-2xl items-center justify-between px-4 pt-6" data-testid="cabecera">
      <span className="text-sm text-muted-foreground" data-testid="usuario">
        {nombreUsuario() ?? 'Usuario'}
      </span>
      <Button variant="outline" size="sm" onClick={() => instance.logoutRedirect()}>
        Salir
      </Button>
    </header>
  )
}

function PantallaLogin() {
  const { instance } = useMsal()

  return (
    <div className="flex min-h-screen items-center justify-center bg-background px-4">
      <div className="flex flex-col items-center gap-6 text-center">
        <h1 className="text-2xl font-semibold tracking-tight">Inspecciones de campo</h1>
        <p className="max-w-xs text-sm text-muted-foreground">
          Inicia sesión con tu cuenta de la organización para continuar
        </p>
        <Button size="lg" className="h-14 px-8 text-base" onClick={() => instance.loginRedirect()}>
          Iniciar sesión
        </Button>
      </div>
    </div>
  )
}

function Rutas() {
  return (
    <Routes>
      <Route path="/" element={<ListaInspeccionesPage />} />
      <Route path="/inspecciones/nueva" element={<NuevaInspeccionPage />} />
      <Route path="/inspecciones/:id" element={<InspeccionDetallePage />} />
    </Routes>
  )
}

function ShellConMsal() {
  return (
    <>
      <UnauthenticatedTemplate>
        <PantallaLogin />
      </UnauthenticatedTemplate>
      <AuthenticatedTemplate>
        <Cabecera />
        <Rutas />
      </AuthenticatedTemplate>
    </>
  )
}

function ShellSinMsal() {
  return (
    <>
      <div className="mx-auto max-w-2xl px-4 pt-6 text-sm text-muted-foreground" data-testid="modo-dev">
        Modo sin autenticación (la config de Entra ID no está cargada)
      </div>
      <Rutas />
    </>
  )
}

createRoot(document.getElementById('root')!).render(
  <BrowserRouter>
    {msalEnabled ? (
      <MsalProvider instance={msalInstance}>
        <ShellConMsal />
      </MsalProvider>
    ) : (
      <ShellSinMsal />
    )}
  </BrowserRouter>,
)
