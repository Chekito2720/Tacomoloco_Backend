CREATE TABLE ingrediente (
    id                 BIGSERIAL PRIMARY KEY,
    nombre             VARCHAR(255) NOT NULL,
    unidad_medida      VARCHAR(50) NOT NULL,
    cantidad_disponible DOUBLE PRECISION NOT NULL,
    estado             VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
    costo_unitario     DECIMAL(10,2) NOT NULL
);

CREATE INDEX idx_ingrediente_estado ON ingrediente(estado);
