CREATE TABLE producto (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(255) NOT NULL,
    descripcion TEXT,
    precio      DECIMAL(10,2) NOT NULL,
    imagen_url  VARCHAR(500),
    disponible  BOOLEAN NOT NULL DEFAULT TRUE,
    categoria   VARCHAR(100) NOT NULL
);

CREATE TABLE producto_ingrediente (
    id                 BIGSERIAL PRIMARY KEY,
    producto_id        BIGINT NOT NULL REFERENCES producto(id),
    ingrediente_id     BIGINT NOT NULL,
    cantidad_requerida DECIMAL(10,2) NOT NULL
);

CREATE INDEX idx_producto_disponible ON producto(disponible);
CREATE INDEX idx_producto_categoria ON producto(categoria);
CREATE INDEX idx_producto_ingrediente_producto ON producto_ingrediente(producto_id);
CREATE INDEX idx_producto_ingrediente_id ON producto_ingrediente(ingrediente_id);
