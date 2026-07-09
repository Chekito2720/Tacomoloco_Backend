CREATE TABLE pedido (
    id            BIGSERIAL PRIMARY KEY,
    cliente_id    BIGINT NOT NULL,
    estado        VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    total         DECIMAL(10,2) NOT NULL DEFAULT 0,
    notas_cliente TEXT
);

CREATE TABLE detalle_pedido (
    id              BIGSERIAL PRIMARY KEY,
    pedido_id       BIGINT NOT NULL REFERENCES pedido(id),
    producto_id     BIGINT NOT NULL,
    cantidad        INTEGER NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal        DECIMAL(10,2) NOT NULL
);

CREATE TABLE personalizacion_ingrediente (
    id                BIGSERIAL PRIMARY KEY,
    detalle_pedido_id BIGINT NOT NULL REFERENCES detalle_pedido(id),
    ingrediente_id    BIGINT NOT NULL,
    tipo              VARCHAR(10) NOT NULL,
    costo_extra       DECIMAL(10,2) NOT NULL DEFAULT 0
);

CREATE TABLE pago (
    id        BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL REFERENCES pedido(id),
    metodo    VARCHAR(50) NOT NULL,
    monto     DECIMAL(10,2) NOT NULL,
    estado    VARCHAR(30) NOT NULL,
    fecha     TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE notificacion (
    id          BIGSERIAL PRIMARY KEY,
    pedido_id   BIGINT NOT NULL REFERENCES pedido(id),
    cliente_id  BIGINT NOT NULL,
    mensaje     TEXT NOT NULL,
    fecha_envio TIMESTAMP NOT NULL DEFAULT NOW(),
    leido       BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_pedido_cliente ON pedido(cliente_id);
CREATE INDEX idx_pedido_estado ON pedido(estado);
CREATE INDEX idx_detalle_pedido_pedido ON detalle_pedido(pedido_id);
CREATE INDEX idx_personalizacion_detalle ON personalizacion_ingrediente(detalle_pedido_id);
CREATE INDEX idx_pago_pedido ON pago(pedido_id);
CREATE INDEX idx_notificacion_pedido ON notificacion(pedido_id);
CREATE INDEX idx_notificacion_cliente_no_leido ON notificacion(cliente_id, leido);
