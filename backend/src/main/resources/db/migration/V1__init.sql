-- =====================================================================================
-- GlossUP - Esquema inicial (baseline)
-- Modelo relacional alineado con los procesos de negocio y los modulos hexagonales.
-- Cada bloque corresponde a un modulo del backend.
-- =====================================================================================

-- ------------------------------------------------------------------ MODULO: usuarios
CREATE TABLE usuario (
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(180) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nombre        VARCHAR(150) NOT NULL,
    activo        BOOLEAN      NOT NULL DEFAULT TRUE,
    creado_en     TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE rol (
    id     BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE   -- CLIENTE, VENDEDOR, ADMIN
);

CREATE TABLE usuario_rol (
    usuario_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    rol_id     BIGINT NOT NULL REFERENCES rol(id)     ON DELETE CASCADE,
    PRIMARY KEY (usuario_id, rol_id)
);

-- ------------------------------------------------------------------ MODULO: perfil
CREATE TABLE perfil_dermatologico (
    id            BIGSERIAL PRIMARY KEY,
    usuario_id    BIGINT NOT NULL UNIQUE REFERENCES usuario(id) ON DELETE CASCADE,
    ph            NUMERIC(3,1),
    tipo_piel     VARCHAR(20),   -- NORMAL, SECA, GRASA, MIXTA, SENSIBLE
    tipo_cabello  VARCHAR(30),
    actualizado_en TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE perfil_afeccion (
    id         BIGSERIAL PRIMARY KEY,
    perfil_id  BIGINT NOT NULL REFERENCES perfil_dermatologico(id) ON DELETE CASCADE,
    nombre     VARCHAR(120) NOT NULL   -- acne, rosacea, dermatitis, ...
);

CREATE TABLE perfil_alergia (
    id         BIGSERIAL PRIMARY KEY,
    perfil_id  BIGINT NOT NULL REFERENCES perfil_dermatologico(id) ON DELETE CASCADE,
    nombre     VARCHAR(120) NOT NULL   -- nombre INCI del ingrediente al que es alergico
);

-- ------------------------------------------------------------------ MODULO: catalogo
CREATE TABLE ingrediente (
    id          BIGSERIAL PRIMARY KEY,
    nombre_inci VARCHAR(200) NOT NULL UNIQUE,   -- nomenclatura INCI normalizada
    descripcion TEXT
);

CREATE TABLE producto (
    id             BIGSERIAL PRIMARY KEY,
    vendedor_id    BIGINT REFERENCES usuario(id),
    nombre         VARCHAR(200) NOT NULL,
    marca          VARCHAR(150),
    categoria      VARCHAR(120),
    precio         NUMERIC(12,2) NOT NULL DEFAULT 0,
    stock          INT           NOT NULL DEFAULT 0,
    ph             NUMERIC(3,1),
    registro_invima VARCHAR(100),
    activo         BOOLEAN       NOT NULL DEFAULT TRUE,
    creado_en      TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE TABLE producto_ingrediente (
    producto_id    BIGINT NOT NULL REFERENCES producto(id)    ON DELETE CASCADE,
    ingrediente_id BIGINT NOT NULL REFERENCES ingrediente(id) ON DELETE CASCADE,
    concentracion  NUMERIC(5,2),
    PRIMARY KEY (producto_id, ingrediente_id)
);

-- ------------------------------------------------------------------ MODULO: compatibilidad
-- Reglas producto-producto: dos ingredientes que no deben combinarse.
CREATE TABLE regla_incompatibilidad (
    id              BIGSERIAL PRIMARY KEY,
    ingrediente_a   BIGINT NOT NULL REFERENCES ingrediente(id),
    ingrediente_b   BIGINT NOT NULL REFERENCES ingrediente(id),
    severidad       VARCHAR(20) NOT NULL DEFAULT 'MEDIA',  -- BAJA, MEDIA, ALTA
    motivo          TEXT,
    CONSTRAINT uq_par_ingredientes UNIQUE (ingrediente_a, ingrediente_b)
);

-- Reglas ingrediente-perfil: ingrediente desaconsejado para una condicion/tipo de piel.
CREATE TABLE regla_perfil (
    id             BIGSERIAL PRIMARY KEY,
    ingrediente_id BIGINT NOT NULL REFERENCES ingrediente(id),
    condicion      VARCHAR(120) NOT NULL,   -- tipo de piel o afeccion a la que aplica
    severidad      VARCHAR(20)  NOT NULL DEFAULT 'MEDIA',
    motivo         TEXT
);

-- ------------------------------------------------------------------ MODULO: recomendacion / ventas
CREATE TABLE pedido (
    id          BIGSERIAL PRIMARY KEY,
    usuario_id  BIGINT NOT NULL REFERENCES usuario(id),
    estado      VARCHAR(30) NOT NULL DEFAULT 'CREADO',  -- CREADO, PAGADO, ENVIADO, CANCELADO
    total       NUMERIC(12,2) NOT NULL DEFAULT 0,
    creado_en   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE pedido_item (
    id          BIGSERIAL PRIMARY KEY,
    pedido_id   BIGINT NOT NULL REFERENCES pedido(id) ON DELETE CASCADE,
    producto_id BIGINT NOT NULL REFERENCES producto(id),
    cantidad    INT    NOT NULL DEFAULT 1,
    precio_unit NUMERIC(12,2) NOT NULL
);

-- Roles base
INSERT INTO rol (nombre) VALUES ('CLIENTE'), ('VENDEDOR'), ('ADMIN');

-- Indices utiles para busqueda de catalogo (HU12/HU13)
CREATE INDEX idx_producto_nombre   ON producto (lower(nombre));
CREATE INDEX idx_producto_categoria ON producto (categoria);
CREATE INDEX idx_ingrediente_inci  ON ingrediente (lower(nombre_inci));
