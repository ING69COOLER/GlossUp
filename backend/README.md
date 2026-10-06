# GlossUP · Backend

Backend del ecommerce de cosmética **GlossUP**, con motor de compatibilidad dermatológica.
Implementado como **monolito modular con arquitectura hexagonal** (ADR-01).

## Stack
- **Java 21** + **Spring Boot 3.3**
- **PostgreSQL 16** (Spring Data JPA / Hibernate) — ADR-02, ADR-05, ADR-06
- **Flyway** para migraciones de esquema
- **Spring Security + JWT** (JJWT) — autenticación stateless
- **springdoc-openapi** (Swagger UI) — documentación de API
- **JUnit 5 + Testcontainers + JaCoCo** — pruebas y cobertura
- **Docker / docker-compose** — ADR-08

## Estructura (hexagonal por módulo)
```
backend/
├── pom.xml                 # un único módulo Maven
├── Dockerfile              # imagen de la app (multi-stage)
├── docker-compose.yml      # app + PostgreSQL
└── src/
    ├── main/
    │   ├── resources/
    │   │   ├── application.yml
    │   │   └── db/migration/        # Flyway (V1__init.sql)
    │   └── java/com/glossup/
    │       ├── GlossupApplication.java   # punto de entrada único
    │       ├── shared/                   # transversal: config, security (JWT), exception, web
    │       ├── usuarios/                 # MÓDULO 1 · identidad y roles
    │       ├── perfil/                   # MÓDULO 2 · perfil dermatológico
    │       ├── catalogo/                 # MÓDULO 3 · catálogo + ficha INCI
    │       ├── compatibilidad/           # MÓDULO 4 · motor de reglas
    │       └── recomendacion/            # MÓDULO 5 · IA + carrito + compra
    └── test/
```
Cada módulo sigue el patrón: `domain/` (model, port/in, port/out) · `application/service` ·
`infrastructure/` (in/rest, out/persistence, out/client). Ver el `README.md` dentro de cada
módulo para el mapeo con las Historias de Usuario de Jira.

## Requisitos
- JDK 21 (ya instalado)
- Docker (para PostgreSQL y/o despliegue)
- No necesitas instalar Maven: usa el **wrapper** (`./mvnw`).

## Puesta en marcha

### Opción A — todo con Docker
```bash
cd backend
docker compose up --build
```
App en http://localhost:8080 · Swagger en http://localhost:8080/swagger-ui.html

### Opción B — Postgres en Docker + app local
```bash
cd backend
docker compose up -d db          # solo la base de datos
./mvnw spring-boot:run           # la app en tu máquina
```

## Comandos útiles
```bash
./mvnw clean verify     # compila, ejecuta pruebas y genera reporte JaCoCo
./mvnw test             # solo pruebas
./mvnw spring-boot:run  # arranca la app (requiere PostgreSQL disponible)
```
Reporte de cobertura: `target/site/jacoco/index.html`.

## Variables de entorno
| Variable | Por defecto | Descripción |
|----------|-------------|-------------|
| `GLOSSUP_DB_URL` | `jdbc:postgresql://localhost:5432/glossup` | URL de la base de datos |
| `GLOSSUP_DB_USER` / `GLOSSUP_DB_PASSWORD` | `glossup` / `glossup` | Credenciales BD |
| `GLOSSUP_JWT_SECRET` | *(dev)* | Secreto de firma JWT (≥ 32 bytes en producción) |
| `GLOSSUP_JWT_EXPIRATION_MS` | `3600000` | Vigencia del token (ms) |
| `GLOSSUP_CORS_ORIGINS` | orígenes locales | Orígenes permitidos para la PWA |
| `GLOSSUP_PORT` | `8080` | Puerto HTTP |
