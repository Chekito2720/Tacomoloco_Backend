-- V2__sample_data.sql - Sample data for usuarios service
-- Passwords are BCrypt encoded: admin123, gerente123, cliente123
INSERT INTO usuario (nombre, correo, password_hash, rol, activo) VALUES
('Admin Principal', 'admin@tacomoloco.com', '$2a$10$rDkPvvAFV6GgJjZxQqV9CeK8VxYvZ8JzLQw9vRz3Xq7YzW1E2F3G4H', 'ADMIN', true),
('Gerente Sucursal', 'gerente@tacomoloco.com', '$2a$10$rDkPvvAFV6GgJjZxQqV9CeK8VxYvZ8JzLQw9vRz3Xq7YzW1E2F3G4H', 'GERENTE', true),
('Juan Pérez', 'juan@mail.com', '$2a$10$rDkPvvAFV6GgJjZxQqV9CeK8VxYvZ8JzLQw9vRz3Xq7YzW1E2F3G4H', 'CLIENTE', true),
('María González', 'maria@mail.com', '$2a$10$rDkPvvAFV6GgJjZxQqV9CeK8VxYvZ8JzLQw9vRz3Xq7YzW1E2F3G4H', 'CLIENTE', true),
('Carlos López', 'carlos@mail.com', '$2a$10$rDkPvvAFV6GgJjZxQqV9CeK8VxYvZ8JzLQw9vRz3Xq7YzW1E2F3G4H', 'CLIENTE', true),
('Ana Martín', 'ana@mail.com', '$2a$10$rDkPvvAFV6GgJjZxQqV9CeK8VxYvZ8JzLQw9vRz3Xq7YzW1E2F3G4H', 'CLIENTE', true),
('Pedro Sánchez', 'pedro@mail.com', '$2a$10$rDkPvvAFV6GgJjZxQqV9CeK8VxYvZ8JzLQw9vRz3Xq7YzW1E2F3G4H', 'CLIENTE', false)
ON CONFLICT (correo) DO NOTHING;

INSERT INTO perfil (usuario_id, telefono, direccion, foto_url)
SELECT u.id, '+52 55 1111 1111', 'Av. Insurgentes Sur 123, Col. Roma Norte, CDMX', 'assets/admin-avatar.jpg'
FROM usuario u WHERE u.correo = 'admin@tacomoloco.com'
AND NOT EXISTS (SELECT 1 FROM perfil p WHERE p.usuario_id = u.id);

INSERT INTO perfil (usuario_id, telefono, direccion, foto_url)
SELECT u.id, '+52 55 2222 2222', 'Av. Reforma 456, Col. Juárez, CDMX', 'assets/gerente-avatar.jpg'
FROM usuario u WHERE u.correo = 'gerente@tacomoloco.com'
AND NOT EXISTS (SELECT 1 FROM perfil p WHERE p.usuario_id = u.id);

INSERT INTO perfil (usuario_id, telefono, direccion, foto_url)
SELECT u.id, '+52 55 3333 3333', 'Calle 5 de Mayo 789, Col. Centro, CDMX', 'assets/cliente-avatar.jpg'
FROM usuario u WHERE u.correo = 'juan@mail.com'
AND NOT EXISTS (SELECT 1 FROM perfil p WHERE p.usuario_id = u.id);

INSERT INTO perfil (usuario_id, telefono, direccion, foto_url)
SELECT u.id, '+52 55 4444 4444', 'Av. Universidad 321, Col. Coyoacán, CDMX', 'assets/cliente-avatar.jpg'
FROM usuario u WHERE u.correo = 'maria@mail.com'
AND NOT EXISTS (SELECT 1 FROM perfil p WHERE p.usuario_id = u.id);

INSERT INTO perfil (usuario_id, telefono, direccion, foto_url)
SELECT u.id, '+52 55 5555 5555', 'Calzada de Tlalpan 654, Col. Doctores, CDMX', 'assets/cliente-avatar.jpg'
FROM usuario u WHERE u.correo = 'carlos@mail.com'
AND NOT EXISTS (SELECT 1 FROM perfil p WHERE p.usuario_id = u.id);

INSERT INTO perfil (usuario_id, telefono, direccion, foto_url)
SELECT u.id, '+52 55 6666 6666', 'Eje Central 987, Col. Morelos, CDMX', 'assets/cliente-avatar.jpg'
FROM usuario u WHERE u.correo = 'ana@mail.com'
AND NOT EXISTS (SELECT 1 FROM perfil p WHERE p.usuario_id = u.id);

INSERT INTO perfil (usuario_id, telefono, direccion, foto_url)
SELECT u.id, '+52 55 7777 7777', 'Av. Patriotismo 147, Col. Escandón, CDMX', 'assets/cliente-avatar.jpg'
FROM usuario u WHERE u.correo = 'pedro@mail.com'
AND NOT EXISTS (SELECT 1 FROM perfil p WHERE p.usuario_id = u.id);