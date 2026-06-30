-- V8: Datos empresariales para demo completa desde base de datos.
-- Credenciales demo: admin@cafedronel.com / password
-- Este archivo es copia manual del script Flyway ubicado en src/main/resources/db/migration.

INSERT INTO usuarios (nombre, email, password, rol, activo)
SELECT 'Administrador General', 'admin@cafedronel.com',
       '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', TRUE
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'admin@cafedronel.com');

INSERT INTO usuarios (nombre, email, password, rol, activo)
SELECT 'Caja Principal', 'caja@cafedronel.com',
       '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER', TRUE
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'caja@cafedronel.com');

INSERT INTO usuarios (nombre, email, password, rol, activo)
SELECT 'Supervisor Turno', 'supervisor@cafedronel.com',
       '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER', TRUE
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'supervisor@cafedronel.com');

INSERT INTO usuarios (nombre, email, password, rol, activo)
SELECT 'Cuenta Demo', 'demo@cafedronel.com',
       '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER', FALSE
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'demo@cafedronel.com');

INSERT INTO proveedores (nombre, telefono, direccion, email, activo)
SELECT 'Andes Coffee', '987451220', 'Cusco - Peru', 'ventas@andescoffee.pe', TRUE
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE email = 'ventas@andescoffee.pe');

INSERT INTO proveedores (nombre, telefono, direccion, email, activo)
SELECT 'Distribuidora Lima', '966220145', 'Av. Colonial 455, Lima', 'compras@distribuidoralima.pe', TRUE
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE email = 'compras@distribuidoralima.pe');

INSERT INTO proveedores (nombre, telefono, direccion, email, activo)
SELECT 'EcoPack Peru', '955120330', 'Surquillo, Lima', 'contacto@ecopack.pe', TRUE
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE email = 'contacto@ecopack.pe');

INSERT INTO proveedores (nombre, telefono, direccion, email, activo)
SELECT 'Dulce Norte', '944876100', 'Chiclayo, Lambayeque', 'pedidos@dulcenorte.pe', TRUE
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE email = 'pedidos@dulcenorte.pe');

INSERT INTO productos (nombre, precio, categoria, descripcion, activo)
SELECT 'Cafe Americano', 8.50, 'Bebidas', 'Cafe filtrado de origen peruano', TRUE
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Cafe Americano');

INSERT INTO productos (nombre, precio, categoria, descripcion, activo)
SELECT 'Cappuccino Clasico', 11.00, 'Bebidas', 'Espresso con leche vaporizada y espuma fina', TRUE
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Cappuccino Clasico');

INSERT INTO productos (nombre, precio, categoria, descripcion, activo)
SELECT 'Torta de Chocolate', 14.00, 'Postres', 'Porcion artesanal con cacao bitter', TRUE
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Torta de Chocolate');

INSERT INTO productos (nombre, precio, categoria, descripcion, activo)
SELECT 'Sandwich Integral', 16.00, 'Comidas', 'Pan integral con pollo, palta y verduras', TRUE
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Sandwich Integral');

INSERT INTO productos (nombre, precio, categoria, descripcion, activo)
SELECT 'Latte Vainilla', 13.50, 'Bebidas', 'Cafe latte con jarabe de vainilla', TRUE
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Latte Vainilla');

INSERT INTO productos (nombre, precio, categoria, descripcion, activo)
SELECT 'Brownie Artesanal', 9.50, 'Postres', 'Brownie de chocolate con nueces', TRUE
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Brownie Artesanal');

INSERT INTO inventario (nombre_insumo, cantidad, unidad, stock_minimo, precio_unitario, proveedor_id, fecha_actualizacion)
SELECT 'Cafe especial', 6, 'kg', 12, 31.50, p.id, TIMESTAMP '2026-06-26 08:00:00'
FROM proveedores p
WHERE p.email = 'ventas@andescoffee.pe'
  AND NOT EXISTS (SELECT 1 FROM inventario WHERE nombre_insumo = 'Cafe especial');

INSERT INTO inventario (nombre_insumo, cantidad, unidad, stock_minimo, precio_unitario, proveedor_id, fecha_actualizacion)
SELECT 'Leche evaporada', 18, 'latas', 40, 3.90, p.id, TIMESTAMP '2026-06-26 08:05:00'
FROM proveedores p
WHERE p.email = 'compras@distribuidoralima.pe'
  AND NOT EXISTS (SELECT 1 FROM inventario WHERE nombre_insumo = 'Leche evaporada');

INSERT INTO inventario (nombre_insumo, cantidad, unidad, stock_minimo, precio_unitario, proveedor_id, fecha_actualizacion)
SELECT 'Azucar rubia', 72, 'kg', 30, 4.20, p.id, TIMESTAMP '2026-06-26 08:10:00'
FROM proveedores p
WHERE p.email = 'pedidos@dulcenorte.pe'
  AND NOT EXISTS (SELECT 1 FROM inventario WHERE nombre_insumo = 'Azucar rubia');

INSERT INTO inventario (nombre_insumo, cantidad, unidad, stock_minimo, precio_unitario, proveedor_id, fecha_actualizacion)
SELECT 'Vasos biodegradables', 80, 'unid.', 200, 0.32, p.id, TIMESTAMP '2026-06-26 08:15:00'
FROM proveedores p
WHERE p.email = 'contacto@ecopack.pe'
  AND NOT EXISTS (SELECT 1 FROM inventario WHERE nombre_insumo = 'Vasos biodegradables');

INSERT INTO inventario (nombre_insumo, cantidad, unidad, stock_minimo, precio_unitario, proveedor_id, fecha_actualizacion)
SELECT 'Jarabe de vainilla', 14, 'botellas', 10, 18.00, p.id, TIMESTAMP '2026-06-26 08:20:00'
FROM proveedores p
WHERE p.email = 'compras@distribuidoralima.pe'
  AND NOT EXISTS (SELECT 1 FROM inventario WHERE nombre_insumo = 'Jarabe de vainilla');

INSERT INTO pedidos (cliente, estado, total, fecha_creacion)
SELECT 'Mesa 04', 'en_proceso', 64.00, TIMESTAMP '2026-06-26 09:15:00'
WHERE NOT EXISTS (
    SELECT 1 FROM pedidos WHERE cliente = 'Mesa 04' AND fecha_creacion = TIMESTAMP '2026-06-26 09:15:00'
);

INSERT INTO pedidos (cliente, estado, total, fecha_creacion)
SELECT 'Delivery 128', 'pendiente', 42.50, TIMESTAMP '2026-06-26 09:22:00'
WHERE NOT EXISTS (
    SELECT 1 FROM pedidos WHERE cliente = 'Delivery 128' AND fecha_creacion = TIMESTAMP '2026-06-26 09:22:00'
);

INSERT INTO pedidos (cliente, estado, total, fecha_creacion)
SELECT 'Mesa 09', 'completado', 88.00, TIMESTAMP '2026-06-26 09:35:00'
WHERE NOT EXISTS (
    SELECT 1 FROM pedidos WHERE cliente = 'Mesa 09' AND fecha_creacion = TIMESTAMP '2026-06-26 09:35:00'
);

INSERT INTO pedidos (cliente, estado, total, fecha_creacion)
SELECT 'Mostrador', 'completado', 23.00, TIMESTAMP '2026-06-26 09:48:00'
WHERE NOT EXISTS (
    SELECT 1 FROM pedidos WHERE cliente = 'Mostrador' AND fecha_creacion = TIMESTAMP '2026-06-26 09:48:00'
);

INSERT INTO detalle_pedido (pedido_id, producto_id, cantidad, precio, subtotal)
SELECT pe.id, pr.id, 4, CAST(pr.precio AS NUMERIC(10, 2)), CAST(pr.precio * 4 AS NUMERIC(12, 2))
FROM pedidos pe, productos pr
WHERE pe.cliente = 'Mesa 04'
  AND pe.fecha_creacion = TIMESTAMP '2026-06-26 09:15:00'
  AND pr.nombre = 'Cafe Americano'
  AND NOT EXISTS (SELECT 1 FROM detalle_pedido d WHERE d.pedido_id = pe.id AND d.producto_id = pr.id);

INSERT INTO detalle_pedido (pedido_id, producto_id, cantidad, precio, subtotal)
SELECT pe.id, pr.id, 1, CAST(pr.precio AS NUMERIC(10, 2)), CAST(pr.precio AS NUMERIC(12, 2))
FROM pedidos pe, productos pr
WHERE pe.cliente = 'Mesa 04'
  AND pe.fecha_creacion = TIMESTAMP '2026-06-26 09:15:00'
  AND pr.nombre = 'Sandwich Integral'
  AND NOT EXISTS (SELECT 1 FROM detalle_pedido d WHERE d.pedido_id = pe.id AND d.producto_id = pr.id);

INSERT INTO detalle_pedido (pedido_id, producto_id, cantidad, precio, subtotal)
SELECT pe.id, pr.id, 1, CAST(pr.precio AS NUMERIC(10, 2)), CAST(pr.precio AS NUMERIC(12, 2))
FROM pedidos pe, productos pr
WHERE pe.cliente = 'Mesa 04'
  AND pe.fecha_creacion = TIMESTAMP '2026-06-26 09:15:00'
  AND pr.nombre = 'Torta de Chocolate'
  AND NOT EXISTS (SELECT 1 FROM detalle_pedido d WHERE d.pedido_id = pe.id AND d.producto_id = pr.id);

INSERT INTO detalle_pedido (pedido_id, producto_id, cantidad, precio, subtotal)
SELECT pe.id, pr.id, 5, CAST(pr.precio AS NUMERIC(10, 2)), CAST(pr.precio * 5 AS NUMERIC(12, 2))
FROM pedidos pe, productos pr
WHERE pe.cliente = 'Delivery 128'
  AND pe.fecha_creacion = TIMESTAMP '2026-06-26 09:22:00'
  AND pr.nombre = 'Cafe Americano'
  AND NOT EXISTS (SELECT 1 FROM detalle_pedido d WHERE d.pedido_id = pe.id AND d.producto_id = pr.id);

INSERT INTO detalle_pedido (pedido_id, producto_id, cantidad, precio, subtotal)
SELECT pe.id, pr.id, 8, CAST(pr.precio AS NUMERIC(10, 2)), CAST(pr.precio * 8 AS NUMERIC(12, 2))
FROM pedidos pe, productos pr
WHERE pe.cliente = 'Mesa 09'
  AND pe.fecha_creacion = TIMESTAMP '2026-06-26 09:35:00'
  AND pr.nombre = 'Cappuccino Clasico'
  AND NOT EXISTS (SELECT 1 FROM detalle_pedido d WHERE d.pedido_id = pe.id AND d.producto_id = pr.id);

INSERT INTO detalle_pedido (pedido_id, producto_id, cantidad, precio, subtotal)
SELECT pe.id, pr.id, 1, CAST(pr.precio AS NUMERIC(10, 2)), CAST(pr.precio AS NUMERIC(12, 2))
FROM pedidos pe, productos pr
WHERE pe.cliente = 'Mostrador'
  AND pe.fecha_creacion = TIMESTAMP '2026-06-26 09:48:00'
  AND pr.nombre = 'Brownie Artesanal'
  AND NOT EXISTS (SELECT 1 FROM detalle_pedido d WHERE d.pedido_id = pe.id AND d.producto_id = pr.id);

INSERT INTO detalle_pedido (pedido_id, producto_id, cantidad, precio, subtotal)
SELECT pe.id, pr.id, 1, CAST(pr.precio AS NUMERIC(10, 2)), CAST(pr.precio AS NUMERIC(12, 2))
FROM pedidos pe, productos pr
WHERE pe.cliente = 'Mostrador'
  AND pe.fecha_creacion = TIMESTAMP '2026-06-26 09:48:00'
  AND pr.nombre = 'Latte Vainilla'
  AND NOT EXISTS (SELECT 1 FROM detalle_pedido d WHERE d.pedido_id = pe.id AND d.producto_id = pr.id);

INSERT INTO ventas (usuario_id, producto_id, cantidad, precio_unitario, total, estado, metodo_pago, fecha_venta)
SELECT u.id, p.id, 3, 8.50, 25.50, 'completado', 'tarjeta', TIMESTAMP '2026-06-26 10:05:00'
FROM usuarios u, productos p
WHERE u.email = 'admin@cafedronel.com'
  AND p.nombre = 'Cafe Americano'
  AND NOT EXISTS (
      SELECT 1 FROM ventas v
      WHERE v.usuario_id = u.id AND v.producto_id = p.id AND v.fecha_venta = TIMESTAMP '2026-06-26 10:05:00'
  );

INSERT INTO ventas (usuario_id, producto_id, cantidad, precio_unitario, total, estado, metodo_pago, fecha_venta)
SELECT u.id, p.id, 2, 11.00, 22.00, 'completado', 'efectivo', TIMESTAMP '2026-06-26 10:18:00'
FROM usuarios u, productos p
WHERE u.email = 'caja@cafedronel.com'
  AND p.nombre = 'Cappuccino Clasico'
  AND NOT EXISTS (
      SELECT 1 FROM ventas v
      WHERE v.usuario_id = u.id AND v.producto_id = p.id AND v.fecha_venta = TIMESTAMP '2026-06-26 10:18:00'
  );

INSERT INTO ventas (usuario_id, producto_id, cantidad, precio_unitario, total, estado, metodo_pago, fecha_venta)
SELECT u.id, p.id, 4, 14.00, 56.00, 'completado', 'yape', TIMESTAMP '2026-06-26 10:34:00'
FROM usuarios u, productos p
WHERE u.email = 'supervisor@cafedronel.com'
  AND p.nombre = 'Torta de Chocolate'
  AND NOT EXISTS (
      SELECT 1 FROM ventas v
      WHERE v.usuario_id = u.id AND v.producto_id = p.id AND v.fecha_venta = TIMESTAMP '2026-06-26 10:34:00'
  );

INSERT INTO ventas (usuario_id, producto_id, cantidad, precio_unitario, total, estado, metodo_pago, fecha_venta)
SELECT u.id, p.id, 1, 16.00, 16.00, 'pendiente', 'efectivo', TIMESTAMP '2026-06-26 10:45:00'
FROM usuarios u, productos p
WHERE u.email = 'admin@cafedronel.com'
  AND p.nombre = 'Sandwich Integral'
  AND NOT EXISTS (
      SELECT 1 FROM ventas v
      WHERE v.usuario_id = u.id AND v.producto_id = p.id AND v.fecha_venta = TIMESTAMP '2026-06-26 10:45:00'
  );
