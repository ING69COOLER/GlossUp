# GlossUP · Frontend (PWA)

Cliente web de **GlossUP** implementado como **Progressive Web App** (ADR-03), que consume la
API REST del backend.

## Stack
- **React 19 + TypeScript** sobre **Vite**
- **PWA** con `vite-plugin-pwa` (service worker + manifest, instalable en móvil)
- **React Router** para el enrutado
- **Axios** como cliente HTTP (con inyección automática del token JWT)

## Estructura (por features, espejo de los módulos del backend)
```
frontend/
├── index.html
├── vite.config.ts            # PWA + proxy /api -> localhost:8080
└── src/
    ├── main.tsx
    ├── app/                  # shell: App.tsx (router + navbar), HomePage
    ├── shared/
    │   ├── api/              # apiClient (axios + JWT), health
    │   ├── components/       # componentes reutilizables
    │   ├── hooks/
    │   └── types/            # tipos de dominio (espejo de las entidades del back)
    ├── styles/
    └── features/
        ├── usuarios/         # login / registro
        ├── perfil/           # perfil dermatológico (HU1, HU2)
        ├── catalogo/         # productos e INCI (HU3, HU4, HU12, HU13, HU5)
        ├── compatibilidad/   # motor de alertas (HU6, HU7, HU8)
        └── recomendacion/    # carrito, checkout, IA (HU9, HU10, HU11, HU14)
```
Cada feature tiene `pages/`, `components/` y `api/`.

## Requisitos
- Node 20+ (tienes Node 26)
- El backend corriendo en `http://localhost:8080` (para que funcione el proxy `/api`)

## Puesta en marcha
```bash
cd frontend
npm install          # instala dependencias
npm run dev          # servidor de desarrollo en http://localhost:5173
```

## Comandos
```bash
npm run dev        # desarrollo (HMR)
npm run build      # compila a dist/ (TypeScript + Vite)
npm run preview    # sirve la build de producción localmente
npm run lint       # oxlint
```

## Variables de entorno
Copia `.env.example` a `.env` y ajusta:
| Variable | Por defecto | Descripción |
|----------|-------------|-------------|
| `VITE_API_URL` | `/api` | Base del backend. En dev usa el proxy de Vite; en prod, la URL pública de la API. |

## Despliegue
`Dockerfile` incluido: construye la app y la sirve como estáticos con **nginx** (SPA fallback).
