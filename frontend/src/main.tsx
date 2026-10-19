import { createRoot } from 'react-dom/client'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import './index.css'
import InspeccionDetallePage from './pages/InspeccionDetallePage'
import ListaInspeccionesPage from './pages/ListaInspeccionesPage'
import NuevaInspeccionPage from './pages/NuevaInspeccionPage'

createRoot(document.getElementById('root')!).render(
  <BrowserRouter>
    <Routes>
      <Route path="/" element={<ListaInspeccionesPage />} />
      <Route path="/inspecciones/nueva" element={<NuevaInspeccionPage />} />
      <Route path="/inspecciones/:id" element={<InspeccionDetallePage />} />
    </Routes>
  </BrowserRouter>,
)
