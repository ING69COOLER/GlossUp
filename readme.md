# GlossUP · E-commerce Cosmético con Motor Dermatológico

GlossUP es una plataforma de comercio electrónico especializada en productos cosméticos y dermatológicos, diseñada para conectar vendedores con clientes que poseen perfiles dermatológicos específicos (tipo de piel, afecciones, alergias y pH).

El sistema cuenta con un **motor de compatibilidad** que:
1. Evalúa productos contra el perfil dermatológico del cliente (alertando sobre alérgenos o ingredientes contraindicados).
2. Evalúa incompatibilidades e interacciones cruzadas entre ingredientes de múltiples productos dentro del carrito de compras antes del checkout.

Este documento detalla la especificación técnica, decisiones de arquitectura y el diseño exhaustivo de la historia de usuario para el backend: **HU-3 (SCRUM-30): Registro de Productos al Catálogo con Ficha INCI e Hipervínculos de Imágenes**.

---

## 1. Arquitectura del Sistema

La solución está construida bajo una **Arquitectura Hexagonal (Puertos y Adaptadores)** combinada con **Diseño Guiado por el Dominio (DDD)** en un **monolito modular**.

```
                   ┌────────────────────────────────────────────────────────┐
                   │                     INFRAESTRUCTURA                    │
                   │                                                        │
                   │   [REST API / Web In]        [Persistencia Out]        │
                   │   ProductoController         ProductoPersistenceAdapter│
                   │   DTOs (Request/Response)    ProductoJpaRepository     │
                   │   JwtAuthenticationFilter    Entidades JPA             │
                   │            │                          ▲                │
                   └────────────┼──────────────────────────┼────────────────┘
                                │                          │
                   ┌────────────┼──────────────────────────┼────────────────┐
                   │            ▼                          │                │
                   │       [Puerto In]               [Puerto Out]           │
                   │   RegistrarProductoUseCase   ProductoRepositoryPort    │
                   │            │                          ▲                │
                   │            ▼                          │                │
                   │      ┌──────────────────────────────────────┐          │
                   │      │         APLICACIÓN / SERVICE         │          │
                   │      │      RegistrarProductoService        │          │
                   │      └──────────────────┬───────────────────┘          │
                   │                         │                              │
                   │                         ▼                              │
                   │      ┌──────────────────────────────────────┐          │
                   │      │            DOMINIO (DDD)             │          │
                   │      │  • Agregado: Producto                │          │
                   │      │  • Entidad: Ingrediente              │          │
                   │      │  • Value Objects: Inci, Dinero,      │          │
                   │      │    ComposicionProducto,              │          │
                   │      │    ImagenProducto                    │          │
                   │      │  • Reglas de Negocio Puras           │          │
                   │      └──────────────────────────────────────┘          │
                   │                                                        │
                   └────────────────────────────────────────────────────────┘
```

### Principios Fundamentales:
- **Aislamiento del Dominio**: El modelo de dominio (`domain/model`) no tiene dependencias de Spring Boot, Hibernate, JPA ni librerías externas. Es Java puro.
- **Inversión de Dependencias (Puertos y Adaptadores)**: La aplicación y el dominio definen interfaces (`port/in` y `port/out`). La infraestructura implementa dichos puertos (`infrastructure/out/persistence` e `infrastructure/in/rest`).
- **Separación entre Dominio y Persistencia**: Las entidades JPA (`ProductoEntity`, `ImagenProductoEntity`, `IngredienteEntity`) existen exclusivamente en infraestructura. Nunca se devuelven al cliente ni se exponen al dominio. Se utilizan mapeadores explícitos dentro de los adaptadores de persistencia.

---

## 2. Historia de Usuario: Registro de Producto al Catálogo (HU-3 / SCRUM-30)

> **Historia de Usuario:**
> *"Como **vendedor**, deseo registrar nuevos productos en el catálogo proporcionando sus datos generales, ficha técnica de especificaciones (composición INCI con concentraciones) y una lista de hipervínculos de imágenes del producto, para que la plataforma valide su formulación y quede disponible de forma segura."*

### Requisitos y Criterios de Aceptación:
1. **Envío Atómico y Completo**:
   - El vendedor registra el producto en un **único request**. No se registran datos parciales ni se agrega la ficha en pasos separados.
   - El cuerpo del request contiene datos generales, lista de URLs de imágenes y ficha INCI completa.
2. **Ficha INCI Obligatoria (RNF-04)**:
   - Todo producto registrado **debe tener al menos un ingrediente INCI**.
   - Si no se envían ingredientes, la petición es rechazada de inmediato tanto por la capa web (`400 Bad Request`) como por la capa de dominio (`ReglaNegocioException`).
   - Al poseer su ficha INCI reglamentaria desde el inicio, el producto queda en estado `activo = true` (publicado).
3. **Lista de Hipervínculos de Imágenes**:
   - Cada producto puede tener una lista ordenada de URLs que apuntan a sus imágenes alojadas (CDN, S3, etc.).
   - No se permiten URLs en blanco o nulas.
4. **Seguridad y Control de Acceso (RNF-11 / RNF-14)**:
   - Endpoint protegido contra accesos anónimos. Requiere token Bearer JWT válido.
   - Restringido a usuarios con rol `VENDEDOR` (`@PreAuthorize("hasRole('VENDEDOR')")`).
   - El identificador del vendedor (`vendedorId`) se resuelve a través de la identidad autenticada en el token JWT, garantizando que un vendedor no registre productos en nombre de otro.

---

## 3. Modelo de Dominio (DDD)

### 3.1. Agregado: `Producto`
- **Identidad**: `id` (Long, asignado en persistencia).
- **Propietario**: `vendedorId` (Long, extraído del contexto JWT).
- **Atributos**: `nombre`, `marca`, `categoria`, `precio` (`Dinero`), `stock` (int), `ph` (Double), `registroInvima` (String), `activo` (boolean), `creadoEn` (OffsetDateTime).
- **Colecciones de Agregado**:
  - `List<ImagenProducto> imagenes`: Lista de hipervínculos de imágenes asociadas.
  - `List<ComposicionProducto> composicion`: Ficha de ingredientes INCI.
- **Invariantes y Reglas de Negocio en el Dominio**:
  - `nombre` es obligatorio y no puede estar en blanco.
  - `precio` (`Dinero`) no puede ser negativo y maneja 2 cifras decimales (`RoundingMode.HALF_UP`).
  - `stock` no puede ser negativo.
  - La composición debe contener **al menos un ingrediente** al registrarse. Si la lista es nula o vacía, lanza `ReglaNegocioException`.
  - No se permiten ingredientes duplicados dentro de la misma ficha de un producto.
  - El producto nace `activo = true` si su ficha INCI contiene $\ge 1$ ingrediente.

### 3.2. Objetos de Valor (Value Objects)
- **`ImagenProducto` (Record)**:
  - Encapsula la URL del hipervínculo.
  - Invariante: URL no nula, no vacía, normalizada con `.trim()`.
- **`Inci` (Record)**:
  - Nomenclatura Internacional de Ingredientes Cosméticos.
  - Invariante: No nulo, normalizado a minúsculas y sin espacios para evitar duplicación ontológica.
- **`ComposicionProducto` (Record)**:
  - Pareja formada por `Ingrediente` y su `concentracion` en porcentaje (Double opcional).
  - Invariante: Concentración entre `0.0` y `100.0` %.
- **`Dinero` (Record)**:
  - Encapsula `BigDecimal` para operaciones monetarias seguras sin errores de punto flotante.

### 3.3. Entidad de Dominio: `Ingrediente`
- Representa el concepto ontológico global en la plataforma.
- Atributos: `id`, `inci` (`Inci`), `descripcion` (String opcional).
- Permite reutilizar el mismo ingrediente entre múltiples productos del catálogo para que el motor de compatibilidad cruce las reglas.

---

## 4. Estructura de Paquetes en `com.glossup.catalogo`

```
com.glossup.catalogo/
├── domain/
│   ├── model/
│   │   ├── Producto.java                       # Raíz del Agregado
│   │   ├── ImagenProducto.java                 # Value Object: hipervínculo de imagen
│   │   ├── ComposicionProducto.java            # Value Object: ingrediente + concentración
│   │   ├── Ingrediente.java                    # Entidad de dominio (ontología)
│   │   └── Inci.java                           # Value Object: nomenclatura INCI normalizada
│   └── port/
│       ├── in/
│       │   ├── RegistrarProductoUseCase.java   # Puerto de Entrada
│       │   └── RegistrarProductoCommand.java   # Parámetros del caso de uso
│       └── out/
│           ├── ProductoRepositoryPort.java     # Puerto de Salida para Producto
│           └── IngredienteRepositoryPort.java  # Puerto de Salida para Ingrediente
├── application/
│   └── service/
│       └── RegistrarProductoService.java       # Implementación transaccional del caso de uso
└── infrastructure/
    ├── in/
    │   └── rest/
    │       ├── ProductoController.java         # Controlador REST (@PreAuthorize)
    │       └── dto/
    │           ├── RegistrarProductoRequest.java # DTO con validaciones Jakarta Bean Validation
    │           ├── IngredienteRequest.java     # DTO de ingrediente en la petición
    │           ├── ProductoResponse.java       # DTO de respuesta estructurada
    │           └── IngredienteResponse.java    # DTO del ingrediente en la respuesta
    └── out/
        └── persistence/
            ├── entity/
            │   ├── ProductoEntity.java         # Mapeo JPA: tabla producto
            │   ├── ImagenProductoEntity.java   # Mapeo JPA: tabla producto_imagen
            │   ├── ProductoIngredienteEntity.java # Mapeo JPA: tabla producto_ingrediente
            │   ├── ProductoIngredienteId.java  # Clave compuesta (@Embeddable)
            │   └── IngredienteEntity.java      # Mapeo JPA: tabla ingrediente
            ├── repository/
            │   ├── ProductoJpaRepository.java  # Spring Data JPA
            │   └── IngredienteJpaRepository.java # Spring Data JPA
            └── adapter/
                ├── ProductoPersistenceAdapter.java    # Adaptador de ProductoRepositoryPort
                └── IngredientePersistenceAdapter.java # Adaptador de IngredienteRepositoryPort
```

---

## 5. Base de Datos y Persistencia

### 5.1. Migración Flyway: `V2__producto_imagenes.sql`
Complementa el esquema baseline (`V1__init.sql`) agregando la tabla para el soporte de listas de imágenes:

```sql
CREATE TABLE producto_imagen (
    id          BIGSERIAL PRIMARY KEY,
    producto_id BIGINT       NOT NULL REFERENCES producto(id) ON DELETE CASCADE,
    url         VARCHAR(500) NOT NULL,
    orden       INT          NOT NULL DEFAULT 0
);

CREATE INDEX idx_producto_imagen_producto ON producto_imagen(producto_id);
```

### 5.2. Tablas Involucradas:
- `producto`: Registra los atributos base, estado (`activo`), `vendedor_id`, `registro_invima`, etc.
- `producto_imagen`: Almacena la lista de URLs asociadas al producto de forma 1 a N.
- `ingrediente`: Diccionario ontológico global (`nombre_inci` único y normalizado).
- `producto_ingrediente`: Tabla asociativa que vincula `producto_id`, `ingrediente_id` y su `concentracion` porcentual.

---

## 6. Contrato de la API REST

### 6.1. Endpoint Principal: Registro de Producto
- **Método**: `POST`
- **Ruta**: `/api/catalogo/productos`
- **Encabezados**:
  - `Content-Type: application/json`
  - `Authorization: Bearer <TOKEN_JWT_ROL_VENDEDOR>`

#### Payload de Entrada (`RegistrarProductoRequest`):
```json
{
  "nombre": "Serum Iluminador Vitamina C 15% + Ácido Hialurónico",
  "marca": "GlossLab",
  "categoria": "Serum Facial",
  "precio": 85000.00,
  "stock": 50,
  "ph": 3.5,
  "registroInvima": "NSOC19482-23CO",
  "imagenes": [
    "https://cdn.glossup.co/productos/serum-vitc-frontal.webp",
    "https://cdn.glossup.co/productos/serum-vitc-textura.webp",
    "https://cdn.glossup.co/productos/serum-vitc-empaque.webp"
  ],
  "ingredientes": [
    {
      "inci": "Ascorbic Acid",
      "descripcion": "Vitamina C pura antioxidante",
      "concentracion": 15.0
    },
    {
      "inci": "Hyaluronic Acid",
      "descripcion": "Agente hidratante de bajo peso molecular",
      "concentracion": 2.0
    },
    {
      "inci": "Aqua",
      "descripcion": "Vehículo purificado",
      "concentracion": null
    }
  ]
}
```

#### Respuestas HTTP:

##### `201 Created` — Registro Exitoso:
```json
{
  "id": 101,
  "vendedorId": 12,
  "nombre": "Serum Iluminador Vitamina C 15% + Ácido Hialurónico",
  "marca": "GlossLab",
  "categoria": "Serum Facial",
  "precio": 85000.00,
  "stock": 50,
  "ph": 3.5,
  "registroInvima": "NSOC19482-23CO",
  "activo": true,
  "imagenes": [
    "https://cdn.glossup.co/productos/serum-vitc-frontal.webp",
    "https://cdn.glossup.co/productos/serum-vitc-textura.webp",
    "https://cdn.glossup.co/productos/serum-vitc-empaque.webp"
  ],
  "ingredientes": [
    {
      "inci": "ascorbic acid",
      "descripcion": "Vitamina C pura antioxidante",
      "concentracion": 15.0
    },
    {
      "inci": "hyaluronic acid",
      "descripcion": "Agente hidratante de bajo peso molecular",
      "concentracion": 2.0
    },
    {
      "inci": "aqua",
      "descripcion": "Vehículo purificado",
      "concentracion": null
    }
  ],
  "creadoEn": "2026-10-14T21:15:00Z"
}
```

##### `400 Bad Request` — Fallo en Validaciones de Entrada:
Ocurre cuando la lista de ingredientes está vacía (`@NotEmpty`), falta el nombre, el precio es negativo o las URLs están vacías:
```json
{
  "timestamp": "2026-10-14T21:15:30Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Error de validacion",
  "path": "/api/catalogo/productos",
  "detalles": [
    "ingredientes: La ficha técnica debe contener al menos un ingrediente INCI"
  ]
}
```

##### `401 Unauthorized` — Token Ausente o Inválido:
Ocurre cuando no se envía la cabecera `Authorization` o el token ha expirado.

##### `403 Forbidden` — Rol Insuficiente:
Ocurre cuando el usuario autenticado tiene rol `CLIENTE` en lugar de `VENDEDOR`.

##### `422 Unprocessable Entity` — Violación de Reglas de Negocio en Dominio:
Ocurre si la lógica de dominio detecta una regla inconsistente no atrapada en los DTOs (por ejemplo, ingredientes duplicados con el mismo INCI):
```json
{
  "timestamp": "2026-10-14T21:16:00Z",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "El ingrediente ya está en la ficha: ascorbic acid",
  "path": "/api/catalogo/productos",
  "detalles": []
}
```

---

## 7. Estrategia y Diseño de Pruebas Unitarias

La pirámide de pruebas garantiza la verificación aislada de cada capa sin acoplamientos innecesarios a bases de datos en los tests unitarios.

### 7.1. Pruebas de Dominio (JUnit 5 puro - Sin Spring, Sin Mocks)
**Paquete**: `com.glossup.catalogo.domain.model`
- **`ProductoTest.java`**:
  1. `debeRegistrarProductoConFichaInciEImagenesCorrectamente`: Verifica que el producto nace con `activo = true`, sus imágenes asociadas y sus ingredientes en composición.
  2. `debeLanzarExcepcionAlRegistrarSinIngredientes`: Valida que `Producto.registrar(...)` lance `ReglaNegocioException` si la lista de composición es vacía o nula.
  3. `debeLanzarExcepcionAlRegistrarIngredienteInciDuplicado`: Valida que si se añade el mismo INCI dos veces en la composición, falle con `ReglaNegocioException`.
  4. `debeLanzarExcepcionSiNombreEstaVacio`: Verifica la obligatoriedad del nombre.
  5. `debeLanzarExcepcionSiStockEsNegativo`: Verifica el invariante del inventario.
  6. `debeLanzarExcepcionSiPrecioEsNegativo`: Verifica la invariante del value object `Dinero`.
  7. `debeReducirStockCorrectamente`: Valida el comportamiento de decremento de inventario.
  8. `debeLanzarExcepcionSiStockInsuficiente`: Valida que no se pueda reducir más del stock disponible.
- **`ImagenProductoTest.java`**:
  1. `debeCrearImagenConUrlValida`: URL válida se preserva con trim.
  2. `debeLanzarExcepcionSiUrlEsNula`: Lanza `ReglaNegocioException`.
  3. `debeLanzarExcepcionSiUrlEsBlanco`: Lanza `ReglaNegocioException`.

### 7.2. Pruebas de Aplicación (JUnit 5 + Mockito - Sin Contexto de Spring)
**Paquete**: `com.glossup.catalogo.application.service`
- **`RegistrarProductoServiceTest.java`**:
  1. `debeRegistrarProductoExitosamente`: Simula llamadas a `IngredienteRepositoryPort` y `ProductoRepositoryPort`. Verifica que los métodos del repositorio se llamen con los objetos de dominio correctos y retorne el producto guardado.
  2. `debeReutilizarIngredienteExistenteSiYaExisteEnOntologia`: Si `IngredienteRepositoryPort.buscarPorInci()` encuentra un ingrediente, se asocia dicho ingrediente en lugar de crear uno nuevo.
  3. `debeCrearIngredienteNuevoSiNoExisteEnOntologia`: Si no existe, invoca `IngredienteRepositoryPort.guardarOBuscar()`.
  4. `debeFallarSiComandoNoTieneIngredientes`: Valida que el servicio lance `ReglaNegocioException` inmediatamente sin interactuar con los repositorios.
  5. `debePermitirRegistrarProductoSinImagenes`: Si la lista de URLs está vacía, el producto se registra correctamente con lista de imágenes vacía.

### 7.3. Pruebas de Infraestructura Web (MockMvc + `@WebMvcTest`)
**Paquete**: `com.glossup.catalogo.infrastructure.in.rest`
- **`ProductoControllerTest.java`**:
  1. `debeRetornar401SiNoHayToken`: Petición anónima es rechazada por Spring Security.
  2. `debeRetornar403SiUsuarioNoEsVendedor`: Petición con usuario autenticado con rol `CLIENTE` recibe `403 Forbidden`.
  3. `debeRetornar201ConProductoResponseSiVendedorYRequestValido`: Petición con rol `VENDEDOR` y JSON válido retorna `201 Created` con el cuerpo esperado.
  4. `debeRetornar400SiFichaInciVacia`: Envío de `"ingredientes": []` retorna `400 Bad Request` con mensaje descriptivo de validación.
  5. `debeRetornar400SiFaltaNombreOPrecioNegativo`: Validaciones estándar de Bean Validation.
  6. `debeRetornar422SiServicioLanzaReglaNegocioException`: Comprueba que `GlobalExceptionHandler` capture `ReglaNegocioException` y devuelva el formato estándar `ApiError` con status 422.

---

## 8. Comandos de Verificación y Ejecución

```bash
# Ejecutar únicamente las pruebas unitarias
cd backend
./mvnw test

# Ejecutar pruebas con reporte de cobertura JaCoCo
./mvnw clean verify

# Iniciar la base de datos PostgreSQL localmente
docker compose up -d db

# Iniciar la aplicación backend
./mvnw spring-boot:run
```

- Documentación OpenAPI / Swagger UI: `http://localhost:8080/swagger-ui.html`
- Health check de la aplicación: `http://localhost:8080/actuator/health`
