CREATE TABLE item_personalizacion (
    id              BIGSERIAL PRIMARY KEY,
    item_carrito_id BIGINT NOT NULL REFERENCES item_carrito(id) ON DELETE CASCADE,
    ingrediente_id  BIGINT NOT NULL,
    tipo            VARCHAR(10) NOT NULL,
    costo_extra     DECIMAL(10,2) NOT NULL DEFAULT 0,
    nombre_ingrediente VARCHAR(255) NOT NULL
);

CREATE TABLE grupo_pedido (
    id              BIGSERIAL PRIMARY KEY,
    carrito_id      BIGINT NOT NULL REFERENCES carrito(id) ON DELETE CASCADE,
    nombre          VARCHAR(255),
    nota            TEXT,
    orden           INTEGER NOT NULL DEFAULT 0
);

ALTER TABLE item_carrito ADD COLUMN grupo_pedido_id BIGINT REFERENCES grupo_pedido(id) ON DELETE SET NULL;

CREATE INDEX idx_item_personalizacion_item ON item_personalizacion(item_carrito_id);
CREATE INDEX idx_grupo_pedido_carrito ON grupo_pedido(carrito_id);
CREATE INDEX idx_item_carrito_grupo ON item_carrito(grupo_pedido_id);