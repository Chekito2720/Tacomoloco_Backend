-- V2__sample_data.sql - Sample data for auth server
-- Uses ON CONFLICT DO NOTHING to avoid duplicate key errors on re-runs
INSERT INTO users (username, password, email, role, enabled) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'admin@tacomoloco.com', 'ADMIN', true),
('gerente', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'gerente@tacomoloco.com', 'GERENTE', true),
('cliente1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'cliente1@email.com', 'CLIENTE', true),
('cliente2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'cliente2@email.com', 'CLIENTE', true)
ON CONFLICT (username) DO NOTHING;

-- Password for all users is: password123