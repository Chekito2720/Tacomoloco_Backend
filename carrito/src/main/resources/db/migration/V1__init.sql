CREATE TABLE carrito (
    id              BIGSERIAL PRIMARY KEY,
    cliente_id      BIGINT NOT NULL,
    fecha_creacion  TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT NOW(),
    activo          BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE item_carrito (
    id              BIGSERIAL PRIMARY KEY,
    carrito_id      BIGINT NOT NULL REFERENCES carrito(id) ON DELETE CASCADE,
    producto_id     BIGINT NOT NULL,
    cantidad        INTEGER NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal        DECIMAL(10,2) NOT NULL,
    nombre_producto VARCHAR(255) NOT NULL,
    imagen_url      VARCHAR(500)
);

CREATE INDEX idx_carrito_cliente ON carrito(cliente_id);
CREATE INDEX idx_carrito_activo ON carrito(activo);
CREATE INDEX idx_item_carrito_carrito ON item_carrito(carrito_id);