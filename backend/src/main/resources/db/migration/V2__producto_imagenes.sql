-- =====================================================================================
-- Migración V2: Soporte para lista de hipervínculos de imágenes por producto (HU3)
-- =====================================================================================

CREATE TABLE producto_imagen (
    id          BIGSERIAL PRIMARY KEY,
    producto_id BIGINT       NOT NULL REFERENCES producto(id) ON DELETE CASCADE,
    url         VARCHAR(500) NOT NULL,
    orden       INT          NOT NULL DEFAULT 0
);

CREATE INDEX idx_producto_imagen_producto ON producto_imagen(producto_id);
