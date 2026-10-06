# Módulo: compatibilidad

Motor de reglas que evalúa, en tiempo real, la compatibilidad **producto–perfil** y
**producto–producto** para prevenir reacciones adversas. Es el núcleo diferencial de GlossUP.

**Proceso de negocio:** Análisis y validación de compatibilidad.

## Historias de usuario (Jira — Sprint 2)
- **SCRUM-33 · HU6** — Validación Compatibilidad Producto–Perfil (Alergias e Irritantes) (5 pts)
- **SCRUM-34 · HU7** — Validación Compatibilidad Producto–Perfil (Tipo de Piel y Comedogenia) (3 pts)
- **SCRUM-35 · HU8** — Validación Compatibilidad Producto–Producto (Cruces en Carrito) (5 pts)

**RNF relacionados:** RNF-01 (exactitud ≥ 95%, 0 falsos negativos críticos), RNF-05 (alertas con causa explicada), RNF-16 (< 2 s).

## Estructura hexagonal
- `domain/model` → `ReglaIncompatibilidad`, `ReglaPerfil`, `ResultadoCompatibilidad`, `Alerta`
- `domain/port/in` → `EvaluarCompatibilidadUseCase`
- `domain/port/out` → `ReglaRepositoryPort`
- `application/service` → motor de inferencia (cruce perfil↔producto y producto↔producto)
- `infrastructure/in/rest` → `CompatibilidadController` (+ `dto/`)
- `infrastructure/out/persistence` → adaptadores JPA de reglas

## Tablas (ver V1__init.sql)
`regla_incompatibilidad`, `regla_perfil`
