-- V4__sample_data.sql - Sample data for pedidos service
-- Clean existing data first (idempotent migration)
DELETE FROM historial_estado_pedido;
DELETE FROM notificacion;
DELETE FROM pago;
DELETE FROM personalizacion_ingrediente;
DELETE FROM detalle_pedido;
DELETE FROM pedido;

-- Reset sequences
ALTER SEQUENCE pedido_id_seq RESTART WITH 1;
ALTER SEQUENCE detalle_pedido_id_seq RESTART WITH 1;
ALTER SEQUENCE pago_id_seq RESTART WITH 1;
ALTER SEQUENCE notificacion_id_seq RESTART WITH 1;
ALTER SEQUENCE historial_estado_pedido_id_seq RESTART WITH 1;

INSERT INTO pedido (cliente_id, estado, total, notas_cliente) VALUES
(3, 'ENTREGADO', 124.00, 'Sin cebolla en los tacos'),
(3, 'ENTREGADO', 85.00, 'Extra salsa verde'),
(4, 'LISTO', 210.00, 'Para llevar'),
(4, 'EN_PREPARACION', 96.00, ''),
(5, 'PENDIENTE', 156.00, 'Entregar en recepción'),
(6, 'CANCELADO', 78.00, 'Cliente canceló'),
(3, 'ENTREGADO', 203.00, 'Todo bien cocido'),
(4, 'ENTREGADO', 134.00, '');

INSERT INTO detalle_pedido (pedido_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 3, 28.00, 84.00),
(1, 2, 2, 25.00, 50.00),
(2, 3, 2, 30.00, 60.00),
(2, 12, 1, 18.00, 18.00),
(3, 1, 2, 28.00, 56.00),
(3, 3, 2, 30.00, 60.00),
(3, 9, 1, 45.00, 45.00),
(3, 11, 1, 55.00, 55.00),
(4, 4, 3, 22.00, 66.00),
(4, 13, 2, 12.00, 24.00),
(5, 5, 2, 26.00, 52.00),
(5, 6, 2, 35.00, 70.00),
(5, 14, 2, 22.00, 44.00),
(6, 2, 3, 25.00, 75.00),
(7, 1, 4, 28.00, 112.00),
(7, 7, 2, 32.00, 64.00),
(7, 15, 2, 20.00, 40.00),
(8, 8, 2, 38.00, 76.00),
(8, 10, 1, 42.00, 42.00),
(8, 16, 1, 35.00, 35.00);

INSERT INTO pago (pedido_id, monto, estado, metodo, fecha) VALUES
(1, 124.00, 'COMPLETADO', 'EFECTIVO', '2024-06-15 14:30:00'),
(2, 85.00, 'COMPLETADO', 'TARJETA', '2024-06-16 19:45:00'),
(3, 210.00, 'COMPLETADO', 'TRANSFERENCIA', '2024-06-17 13:20:00'),
(4, 96.00, 'PENDIENTE', 'EFECTIVO', '2024-06-17 12:00:00'),
(5, 156.00, 'PENDIENTE', 'TARJETA', '2024-06-17 12:00:00'),
(6, 78.00, 'CANCELADO', 'EFECTIVO', '2024-06-17 12:00:00'),
(7, 203.00, 'COMPLETADO', 'EFECTIVO', '2024-06-18 20:15:00'),
(8, 134.00, 'COMPLETADO', 'TARJETA', '2024-06-19 14:00:00');

INSERT INTO notificacion (pedido_id, cliente_id, mensaje, leido) VALUES
(1, 3, 'Tu pedido #1 está listo para recoger', true),
(1, 3, 'Tu pedido #1 ha sido entregado', true),
(2, 3, 'Tu pedido #2 ha sido entregado', true),
(3, 4, 'Tu pedido #3 está siendo preparado', false),
(4, 4, 'Hemos recibido tu pedido #4', true),
(5, 5, 'Hemos recibido tu pedido #5', true),
(6, 6, 'Tu pedido #6 ha sido cancelado', true),
(7, 3, 'Tu pedido #7 ha sido entregado', true),
(8, 4, 'Tu pedido #8 ha sido entregado', true);

INSERT INTO historial_estado_pedido (pedido_id, estado_anterior, estado_nuevo, usuario_id) VALUES
(1, 'PENDIENTE', 'EN_PREPARACION', 2),
(1, 'EN_PREPARACION', 'LISTO', 2),
(1, 'LISTO', 'ENTREGADO', 2),
(2, 'PENDIENTE', 'EN_PREPARACION', 2),
(2, 'EN_PREPARACION', 'LISTO', 2),
(2, 'LISTO', 'ENTREGADO', 2),
(3, 'PENDIENTE', 'EN_PREPARACION', 2),
(3, 'EN_PREPARACION', 'LISTO', 2),
(4, 'PENDIENTE', 'EN_PREPARACION', 2),
(5, 'PENDIENTE', 'EN_PREPARACION', NULL),
(6, 'PENDIENTE', 'CANCELADO', 3),
(7, 'PENDIENTE', 'EN_PREPARACION', 2),
(7, 'EN_PREPARACION', 'LISTO', 2),
(7, 'LISTO', 'ENTREGADO', 2),
(8, 'PENDIENTE', 'EN_PREPARACION', 2),
(8, 'EN_PREPARACION', 'LISTO', 2),
(8, 'LISTO', 'ENTREGADO', 2);