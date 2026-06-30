-- V9: Normaliza credenciales demo para pruebas de Angular + JWT.
-- Clave para todas estas cuentas: password

UPDATE usuarios
SET nombre = 'Administrador General',
    password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    rol = 'ADMIN',
    activo = TRUE
WHERE email = 'admin@cafedronel.com';

UPDATE usuarios
SET nombre = 'Caja Principal',
    password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    rol = 'USER',
    activo = TRUE
WHERE email = 'caja@cafedronel.com';

UPDATE usuarios
SET nombre = 'Supervisor Turno',
    password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    rol = 'USER',
    activo = TRUE
WHERE email = 'supervisor@cafedronel.com';

UPDATE usuarios
SET nombre = 'Cuenta Demo',
    password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    rol = 'USER',
    activo = FALSE
WHERE email = 'demo@cafedronel.com';
