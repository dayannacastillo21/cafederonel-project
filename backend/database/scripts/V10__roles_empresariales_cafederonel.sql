-- V10: Roles empresariales finales.
-- Roles oficiales: ADMIN, CAJERO, INVENTARIO, CONTADOR.

ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS ck_usuarios_rol;

ALTER TABLE usuarios
    ALTER COLUMN rol SET DEFAULT 'CAJERO';

UPDATE usuarios
SET rol = 'CAJERO'
WHERE rol = 'USER';

UPDATE usuarios
SET nombre = 'Administrador General',
    rol = 'ADMIN',
    activo = TRUE
WHERE email = 'admin@cafedronel.com';

UPDATE usuarios
SET nombre = 'Caja Principal',
    rol = 'CAJERO',
    activo = TRUE
WHERE email = 'caja@cafedronel.com';

UPDATE usuarios
SET nombre = 'Encargado de Inventario',
    rol = 'INVENTARIO',
    activo = TRUE
WHERE email IN ('supervisor@cafedronel.com', 'inventario@cafedronel.com');

UPDATE usuarios
SET email = 'inventario@cafedronel.com'
WHERE email = 'supervisor@cafedronel.com'
  AND NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'inventario@cafedronel.com');

INSERT INTO usuarios (nombre, email, password, rol, activo)
SELECT 'Contador General', 'contador@cafedronel.com',
       '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'CONTADOR', TRUE
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'contador@cafedronel.com');

UPDATE usuarios
SET nombre = 'Cuenta Demo Suspendida',
    rol = 'CAJERO',
    activo = FALSE
WHERE email = 'demo@cafedronel.com';

ALTER TABLE usuarios
    ADD CONSTRAINT ck_usuarios_rol
    CHECK (rol IN ('ADMIN', 'CAJERO', 'INVENTARIO', 'CONTADOR'));
