-- V3__sample_data.sql - Sample data for carrito service
INSERT INTO carrito (cliente_id) VALUES
(3),
(4),
(5),
(6);

INSERT INTO grupo_pedido (carrito_id, nombre, nota, orden) VALUES
(1, 'Mi pedido', 'Tacos para la cena', 1),
(1, 'Pedido de María', 'Para mi hermana', 2),
(2, 'Almuerzo', 'Tacos para el almuerzo', 1);