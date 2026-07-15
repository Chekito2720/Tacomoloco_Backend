ALTER TABLE detalle_pedido ADD COLUMN nombre_producto VARCHAR(255);
ALTER TABLE detalle_pedido ADD COLUMN grupo_nombre VARCHAR(255);
ALTER TABLE detalle_pedido ADD COLUMN grupo_nota TEXT;

ALTER TABLE personalizacion_ingrediente ADD COLUMN nombre_ingrediente VARCHAR(255);