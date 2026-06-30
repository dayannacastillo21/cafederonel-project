-- V7: Índices adicionales y datos de demostración
CREATE INDEX IF NOT EXISTS idx_usuarios_activo ON usuarios (activo);
CREATE INDEX IF NOT EXISTS idx_usuarios_rol ON usuarios (rol);

-- Contraseña: password (BCrypt). Cambiar en producción.
INSERT INTO usuarios (nombre, email, password, rol, activo)
SELECT 'Administrador', 'admin@cafedronel.com',
       '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', TRUE
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'admin@cafedronel.com');

INSERT INTO productos (nombre, precio, categoria, descripcion, activo)
SELECT 'Café Americano', 8.00, 'bebidas', 'Café negro tradicional', TRUE
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Café Americano');

INSERT INTO productos (nombre, precio, categoria, descripcion, activo)
SELECT 'Cappuccino', 12.00, 'bebidas', 'Espresso con leche espumada', TRUE
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Cappuccino');

INSERT INTO productos (nombre, precio, categoria, descripcion, activo)
SELECT 'Croissant', 6.50, 'comida', 'Panadería francesa', TRUE
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Croissant');

INSERT INTO productos (nombre, precio, categoria, descripcion, activo)
SELECT 'Cheesecake', 14.00, 'postres', 'Tarta de queso', TRUE
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Cheesecake');

INSERT INTO proveedores (nombre, telefono, direccion, email, activo)
SELECT 'Distribuidora Café Peru', '999111222', 'Av. Industrial 120, Lima', 'ventas@cafeperu.com', TRUE
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE email = 'ventas@cafeperu.com');

INSERT INTO proveedores (nombre, telefono, direccion, email, activo)
SELECT 'Lácteos del Norte', '999333444', 'Calle Los Olivos 45, Lima', 'contacto@lacteosnorte.com', TRUE
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE email = 'contacto@lacteosnorte.com');

INSERT INTO inventario (nombre_insumo, cantidad, unidad, stock_minimo, precio_unitario, proveedor_id)
SELECT 'Granos de café', 50, 'kg', 10, 25.00, p.id
FROM proveedores p
WHERE p.email = 'ventas@cafeperu.com'
  AND NOT EXISTS (SELECT 1 FROM inventario i WHERE i.nombre_insumo = 'Granos de café');

INSERT INTO inventario (nombre_insumo, cantidad, unidad, stock_minimo, precio_unitario, proveedor_id)
SELECT 'Leche entera', 30, 'litros', 5, 4.50, p.id
FROM proveedores p
WHERE p.email = 'contacto@lacteosnorte.com'
  AND NOT EXISTS (SELECT 1 FROM inventario i WHERE i.nombre_insumo = 'Leche entera');

-- Pedidos y ventas se registran desde la API y quedan persistidos por JPA.
-- No se insertan registros demo para evitar duplicar transacciones operativas.
