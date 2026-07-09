CREATE TABLE reporte_generado (
    id               BIGSERIAL PRIMARY KEY,
    tipo             VARCHAR(20) NOT NULL,
    fecha_generacion TIMESTAMP NOT NULL DEFAULT NOW(),
    parametros       TEXT,
    generado_por     BIGINT NOT NULL
);

CREATE TABLE venta_resumen (
    id               BIGSERIAL PRIMARY KEY,
    fecha            DATE NOT NULL,
    producto_id      BIGINT NOT NULL,
    cantidad_vendida INTEGER NOT NULL,
    total_venta      DECIMAL(10,2) NOT NULL
);

CREATE INDEX idx_reporte_tipo ON reporte_generado(tipo);
CREATE INDEX idx_venta_fecha ON venta_resumen(fecha);
CREATE INDEX idx_venta_producto ON venta_resumen(producto_id);
