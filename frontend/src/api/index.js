import axios from 'axios'

// El proxy de Vite (vite.config.js) redirige estas rutas al backend en :8080
const api = axios.create({ baseURL: '/' })

// ── Inmuebles ──
export const getInmuebles = () => api.get('/inmuebles')
export const getInmueble = (codigo) => api.get(`/inmuebles/${codigo}`)
export const createInmueble = (data) => api.post('/inmuebles', data)
export const updateInmueble = (codigo, data) => api.put(`/inmuebles/${codigo}`, data)
export const deleteInmueble = (codigo) => api.delete(`/inmuebles/${codigo}`)
export const deshacerInmueble = () => api.post('/inmuebles/deshacer')
export const cambiarDisponibilidad = (codigo, disponible) =>
  api.patch(`/inmuebles/${codigo}/disponibilidad`, { disponible })

// ── Búsqueda ──
export const buscarPorFiltros = (params) => api.get('/busqueda/filtros', { params })
export const buscarPorRangoPrecio = (precioMin, precioMax) =>
  api.get('/busqueda/rango-precio', { params: { precioMin, precioMax } })
export const buscarCompatibles = (idCliente) =>
  api.get(`/busqueda/compatibles/${idCliente}`)
export const getInmueblesOrdenados = () => api.get('/busqueda/ordenados-por-precio')

// ── Clientes ──
export const getClientes = () => api.get('/clientes')
export const getCliente = (id) => api.get(`/clientes/${id}`)
export const createCliente = (data) => api.post('/clientes', data)
export const updateCliente = (id, data) => api.put(`/clientes/${id}`, data)
export const deleteCliente = (id) => api.delete(`/clientes/${id}`)
export const agregarFavorito = (id, codigo) =>
  api.post(`/clientes/${id}/favoritos/${codigo}`)
export const quitarFavorito = (id, codigo) =>
  api.delete(`/clientes/${id}/favoritos/${codigo}`)
export const registrarConsulta = (id, codigo) =>
  api.post(`/clientes/${id}/consultas/${codigo}`)
export const descartarInmueble = (id, codigo) =>
  api.post(`/clientes/${id}/descartados/${codigo}`)

// ── Asesores ──
export const getAsesores = () => api.get('/asesores')
export const getAsesor = (id) => api.get(`/asesores/${id}`)
export const createAsesor = (data) => api.post('/asesores', data)
export const updateAsesor = (id, data) => api.put(`/asesores/${id}`, data)
export const deleteAsesor = (id) => api.delete(`/asesores/${id}`)

// ── Visitas ──
export const getVisitas = () => api.get('/visitas')
export const getVisita = (id) => api.get(`/visitas/${id}`)
export const createVisita = (data) => api.post('/visitas', data)
export const getVisitasPrioritarias = () => api.get('/visitas/prioritarias')
export const atenderSiguienteVisita = () => api.post('/visitas/atender-siguiente')
export const despacharVisitaPrioritaria = () => api.post('/visitas/despachar-prioritaria')
export const actualizarEstadoVisita = (id, estado) =>
  api.patch(`/visitas/${id}/estado`, { estado })

// ── Alertas ──
export const getAlertas = () => api.get('/alertas')
export const getAlertasHistorial = () => api.get('/alertas/historial')
export const resolverAlerta = (id) => api.patch(`/alertas/${id}/resolver`)

// ── Operaciones ──
export const getOperaciones = () => api.get('/operaciones')
export const getOperacionesHistorial = () => api.get('/operaciones/historial')
export const createOperacion = (data) => api.post('/operaciones', data)
export const cerrarOperacion = (id) => api.post(`/operaciones/${id}/cerrar`)
export const cancelarOperacion = (id) => api.post(`/operaciones/${id}/cancelar`)

// ── Recomendaciones ──
export const getRecomendaciones = (idCliente, max = 5) =>
  api.get(`/recomendaciones/${idCliente}`, { params: { max } })
export const getInteraccionesCliente = (idCliente) =>
  api.get(`/recomendaciones/${idCliente}/interacciones`)

// ── Reportes ──
export const getReporteZonas = () => api.get('/reportes/zonas-actividad')
export const getReporteAsesores = () => api.get('/reportes/asesores-efectividad')
export const getReporteInmuebles = () => api.get('/reportes/inmuebles-visitas')

// ── Detección de anomalías ──
export const ejecutarDeteccion = () => api.post('/deteccion/ejecutar')
export const detectarInmueblesSinCierre = () => api.get('/deteccion/inmuebles-sin-cierre')
export const detectarClientesSinSeguimiento = () => api.get('/deteccion/clientes-sin-seguimiento')
export const detectarAsesoresSobrecarga = () => api.get('/deteccion/asesores-sobrecarga')
export const detectarCambiosPrecioFrecuentes = () => api.get('/deteccion/cambios-precio-frecuentes')
export const detectarConcentracionZona = () => api.get('/deteccion/concentracion-zona')
