# Módulo: catalogo

Gestión del catálogo de productos y la ontología de ingredientes (lista INCI). Estructura e
indexa la información química que consume el motor de compatibilidad.

**Proceso de negocio:** Gestión de catálogo y ontología de ingredientes.

## Historias de usuario (Jira)
- **SCRUM-30 · HU3** — Registro Manual de Producto y Composición Química (5 pts) · Sprint 2
- **SCRUM-31 · HU4** — Modificación y Eliminación de Productos (3 pts) · Sprint 2
- **SCRUM-40 · HU12** — Búsqueda Básica de Productos por Palabra Clave (3 pts) · Sprint 2
- **SCRUM-32 · HU5** — Lectura OCR Asistida de Ingredientes desde Imagen (8 pts) · Sprint 3
- **SCRUM-41 · HU13** — Filtrado Avanzado por Atributos Dermatológicos (5 pts) · Sprint 3

**RNF relacionados:** RNF-04 (ficha INCI obligatoria), registro INVIMA.

## Estructura hexagonal
- `domain/model` → `Producto`, `Ingrediente`
- `domain/port/in` → `GestionarCatalogoUseCase`, `BuscarProductosUseCase`
- `domain/port/out` → `ProductoRepositoryPort`, `IngredienteRepositoryPort`, (`OcrPort` para HU5)
- `infrastructure/in/rest` → `CatalogoController` (+ `dto/`)
- `infrastructure/out/persistence` → adaptadores JPA

## Tablas (ver V1__init.sql)
`ingrediente`, `producto`, `producto_ingrediente`
