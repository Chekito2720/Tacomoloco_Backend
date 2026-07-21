-- V2__sample_data.sql - Sample data for auth server (matches real BD schema)
-- Password for all users is: test123 (BCrypt $2a$10$Gh2wVFNWDJICRktsPzO4Mu...)
INSERT INTO users (nombre, password_hash, correo, rol, activo) VALUES
('Administrador', '$2a$10$Gh2wVFNWDJICRktsPzO4Mu03TAj0dXev6t/L13j1iVXScSauSN4E2', 'admin@tacomoloco.local', 'ADMINISTRADOR', true),
('Juan Ramirez', '$2a$10$Gh2wVFNWDJICRktsPzO4Mu03TAj0dXev6t/L13j1iVXScSauSN4E2', 'juan@mail.com', 'GERENTE', true),
('Ana Garcia', '$2a$10$Gh2wVFNWDJICRktsPzO4Mu03TAj0dXev6t/L13j1iVXScSauSN4E2', 'ana@mail.com', 'CLIENTE', true),
('Carlos Ruiz', '$2a$10$Gh2wVFNWDJICRktsPzO4Mu03TAj0dXev6t/L13j1iVXScSauSN4E2', 'carlos@mail.com', 'CLIENTE', true),
('Maria Garcia', '$2a$10$Gh2wVFNWDJICRktsPzO4Mu03TAj0dXev6t/L13j1iVXScSauSN4E2', 'maria@mail.com', 'CLIENTE', true),
('Sergio Perez', '$2a$10$Gh2wVFNWDJICRktsPzO4Mu03TAj0dXev6t/L13j1iVXScSauSN4E2', 'sergio@mail.com', 'CLIENTE', true),
('Sofia Torres', '$2a$10$Gh2wVFNWDJICRktsPzO4Mu03TAj0dXev6t/L13j1iVXScSauSN4E2', 'sofia@mail.com', 'CLIENTE', true),
('Luis Mendoza', '$2a$10$Gh2wVFNWDJICRktsPzO4Mu03TAj0dXev6t/L13j1iVXScSauSN4E2', 'luis@mail.com', 'CLIENTE', false),
('Karla Flores', '$2a$10$Gh2wVFNWDJICRktsPzO4Mu03TAj0dXev6t/L13j1iVXScSauSN4E2', 'karla@mail.com', 'CLIENTE', true),
('Admin Principal', '$2a$10$rDkPvvAFV6GgJjZxQqV9CeK8VxYvZ8JzLQw9vRz3Xq7YzW1E2F3G4H', 'admin@tacomoloco.com', 'ADMIN', true),
('Gerente Sucursal', '$2a$10$rDkPvvAFV6GgJjZxQqV9CeK8VxYvZ8JzLQw9vRz3Xq7YzW1E2F3G4H', 'gerente@tacomoloco.com', 'GERENTE', true),
('Pedro Sanchez', '$2a$10$rDkPvvAFV6GgJjZxQqV9CeK8VxYvZ8JzLQw9vRz3Xq7YzW1E2F3G4H', 'pedro@mail.com', 'CLIENTE', false)
ON CONFLICT (correo) DO NOTHING;