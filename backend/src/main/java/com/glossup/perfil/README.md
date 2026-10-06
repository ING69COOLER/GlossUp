# Módulo: perfil

Captura y mantiene el perfil dermatológico del cliente (pH, tipo de piel, tipo de cabello,
afecciones, alergias). Es la entrada que alimenta al motor de compatibilidad.

**Proceso de negocio:** Gestión del perfil dermatológico.

## Historias de usuario (Jira — Sprint 2)
- **SCRUM-28 · HU1** — Registro Inicial de Perfil Dermatológico (3 pts)
- **SCRUM-29 · HU2** — Gestión Dinámica de Alergias y Condiciones (Agregar/Quitar) (3 pts)

**RNF relacionados:** RNF-02 (completitud del análisis), RNF-08/09/10 (usabilidad y validación de datos).

## Estructura hexagonal
- `domain/model` → `PerfilDermatologico`, `Afeccion`, `Alergia`
- `domain/port/in` → `GestionarPerfilUseCase`
- `domain/port/out` → `PerfilRepositoryPort`
- `application/service` → lógica de normalización y validación
- `infrastructure/in/rest` → `PerfilController` (+ `dto/`)
- `infrastructure/out/persistence` → adaptador JPA

## Tablas (ver V1__init.sql)
`perfil_dermatologico`, `perfil_afeccion`, `perfil_alergia`
