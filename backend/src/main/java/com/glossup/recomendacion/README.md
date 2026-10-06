# Módulo: recomendacion

Recomendación con IA, carrito de compras, checkout y orquestación con sistemas externos
(pasarela de pagos, API de envíos). Reúne la lógica de venta del producto validado.

**Proceso de negocio:** Gestión de ventas y checkout + recomendación inteligente.

## Historias de usuario (Jira — Sprint 3)
- **SCRUM-36 · HU9** — Consolidación de Carrito y Reserva de Inventario (5 pts)
- **SCRUM-37 · HU10** — Procesamiento de Pago Seguro mediante Pasarela Externa (5 pts)
- **SCRUM-38 · HU11** — Generación de Comprobante de Compra y Soporte Digital (3 pts)
- **SCRUM-39 · HU14** — Notificación Asíncrona a la API de Envíos (Fire and Forget) (3 pts)

**RNF relacionados:** RNF-13 (no almacenar datos de tarjeta), RNF-19 (timeouts/reintentos), RNF-22 (tolerancia a fallos externos), RNF-25 (checkout transaccional ACID).

## Estructura hexagonal
- `domain/model` → `Carrito`, `Pedido`, `PedidoItem`, `Recomendacion`
- `domain/port/in` → `GestionarCarritoUseCase`, `RealizarCheckoutUseCase`, `RecomendarUseCase`
- `domain/port/out` → `PedidoRepositoryPort`, `PasarelaPagosPort`, `EnvioPort`, `IARecomendacionPort`
- `application/service` → crear pedido, procesar pago, generar comprobante
- `infrastructure/in/rest` → `CheckoutController`, `RecomendacionController` (+ `dto/`)
- `infrastructure/out/persistence` → adaptadores JPA
- `infrastructure/out/client` → adaptadores a sistemas externos:
  - `IARecomendacionClient` — motor de IA
  - `PasarelaPagosClient` — pagos
  - `EnvioApiClient` — API de envíos (fulfillment)

## Tablas (ver V1__init.sql)
`pedido`, `pedido_item`
