# Módulo: usuarios

Registro, autenticación (login) y gestión de roles. Es el módulo transversal de identidad
que emite los JWT consumidos por el resto de la aplicación.

**Proceso de negocio:** Transversal (habilita todos los demás).
**RNF relacionados:** RNF-11 (JWT), RNF-14 (control de acceso por rol).

## Estructura hexagonal
- `domain/model` → `Usuario`, `Rol`, `Credencial`
- `domain/port/in` → casos de uso: `RegistrarUsuarioUseCase`, `AutenticarUsuarioUseCase`
- `domain/port/out` → `UsuarioRepositoryPort`
- `application/service` → `RegistrarUsuarioService`, `AutenticarUsuarioService`
- `infrastructure/in/rest` → `UsuarioController` (+ `dto/`)
- `infrastructure/out/persistence` → entidad JPA, `UsuarioJpaRepository`, `UsuarioPersistenceAdapter`

## Tablas (ver V1__init.sql)
`usuario`, `rol`, `usuario_rol`
