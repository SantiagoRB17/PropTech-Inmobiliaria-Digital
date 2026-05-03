import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Solo aplica el proxy a peticiones de API (XHR/fetch), no a navegaciones del browser
const apiBypass = (req) => req.headers.accept?.includes('text/html') ? '/' : null

export default defineConfig({
  plugins: [react()],
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: './src/test/setup.js',
  },
  server: {
    port: 5173,
    proxy: {
      '/inmuebles':      { target: 'http://localhost:8080', changeOrigin: true, bypass: apiBypass },
      '/clientes':       { target: 'http://localhost:8080', changeOrigin: true, bypass: apiBypass },
      '/asesores':       { target: 'http://localhost:8080', changeOrigin: true, bypass: apiBypass },
      '/visitas':        { target: 'http://localhost:8080', changeOrigin: true, bypass: apiBypass },
      '/alertas':        { target: 'http://localhost:8080', changeOrigin: true, bypass: apiBypass },
      '/operaciones':    { target: 'http://localhost:8080', changeOrigin: true, bypass: apiBypass },
      '/busqueda':       { target: 'http://localhost:8080', changeOrigin: true, bypass: apiBypass },
      '/recomendaciones':{ target: 'http://localhost:8080', changeOrigin: true, bypass: apiBypass },
      '/reportes':       { target: 'http://localhost:8080', changeOrigin: true, bypass: apiBypass },
      '/deteccion':      { target: 'http://localhost:8080', changeOrigin: true, bypass: apiBypass },
    },
  },
})
