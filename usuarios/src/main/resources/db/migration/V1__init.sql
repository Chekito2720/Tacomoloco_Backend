CREATE TABLE usuario (
    id             BIGSERIAL PRIMARY KEY,
    nombre         VARCHAR(255) NOT NULL,
    correo         VARCHAR(255) NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    rol            VARCHAR(20) NOT NULL,
    activo         BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE perfil (
    id         BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE REFERENCES usuario(id),
    telefono   VARCHAR(20),
    direccion  TEXT,
    foto_url   VARCHAR(500)
);

CREATE INDEX idx_usuario_correo ON usuario(correo);
