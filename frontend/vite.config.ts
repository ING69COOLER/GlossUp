import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'
import { VitePWA } from 'vite-plugin-pwa'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    // PWA (ADR-03): service worker + manifest para instalación en móvil
    VitePWA({
      registerType: 'autoUpdate',
      manifest: {
        name: 'GlossUP',
        short_name: 'GlossUP',
        description: 'Ecommerce de cosmética con motor de compatibilidad dermatológica',
        theme_color: '#e5739e',
        background_color: '#ffffff',
        display: 'standalone',
        start_url: '/',
        icons: [
          { src: 'pwa-192x192.png', sizes: '192x192', type: 'image/png' },
          { src: 'pwa-512x512.png', sizes: '512x512', type: 'image/png' },
        ],
      },
    }),
  ],
  server: {
    port: 5173,
    // Proxy de /api al backend Spring Boot en desarrollo (evita problemas de CORS)
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
