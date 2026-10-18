-- HU3 (catálogo del vendedor): URL de la imagen del producto.
-- Se guarda el enlace (no el archivo) para carga rápida vía host/CDN externo y DB ligera.
ALTER TABLE producto ADD COLUMN imagen_url VARCHAR(500);
