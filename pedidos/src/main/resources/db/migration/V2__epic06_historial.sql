CREATE TABLE historial_estado_pedido (
    id              BIGSERIAL PRIMARY KEY,
    pedido_id       BIGINT NOT NULL REFERENCES pedido(id),
    estado_anterior VARCHAR(20),
    estado_nuevo    VARCHAR(20) NOT NULL,
    fecha_cambio    TIMESTAMP NOT NULL DEFAULT NOW(),
    usuario_id      BIGINT
);

CREATE INDEX idx_historial_pedido ON historial_estado_pedido(pedido_id);
CREATE INDEX idx_historial_fecha ON historial_estado_pedido(fecha_cambio);