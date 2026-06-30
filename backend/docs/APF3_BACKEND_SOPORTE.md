# APF3 - Soporte Backend Para Frontend Angular

## Nombre del proyecto

- Proyecto completo: Sistema Web de Inventario Cafedronel
- Backend: Cafedronel Backend API
- Frontend objetivo: Cafedronel Frontend Angular

## Estado de fase 1

El backend queda preparado para integrarse con Angular en `http://localhost:4200`.
La API mantiene seguridad JWT, roles, validaciones con DTO, manejo uniforme de errores,
persistencia con JPA/Flyway y pruebas automatizadas.

## Configuracion local segura

El backend lee variables de entorno para base de datos, JWT y CORS.
No se deben guardar passwords reales ni secretos JWT reales en Git.

Variables principales:

```text
PORT=8081
DB_HOST=localhost
DB_PORT=5432
DB_NAME=inventario_backenddatabase
DB_USER=postgres
DB_PASSWORD=<password-local>
DB_SSLMODE=disable
JWT_SECRET=<clave-jwt-minimo-32-caracteres>
CORS_ALLOWED_ORIGINS=http://localhost:4200,http://127.0.0.1:4200
```

## Contrato base para Angular

URL base local:

```text
http://localhost:8081/api
```

Healthcheck:

```http
GET /api/estado
```

Login:

```http
POST /api/auth/sesiones
Content-Type: application/json
```

```json
{
  "email": "admin@cafedronel.com",
  "password": "password"
}
```

Respuesta esperada:

```json
{
  "userId": 1,
  "userName": "Administrador",
  "email": "admin@cafedronel.com",
  "role": "ADMIN",
  "token": "eyJ..."
}
```

Angular debe enviar el token asi:

```http
Authorization: Bearer <token>
```

## Endpoints que debe consumir Angular

| Modulo | Ruta principal | Uso en frontend |
| --- | --- | --- |
| Auth | `/api/auth/sesiones` | Login y obtencion de JWT |
| Productos | `/api/productos` | Listado, creacion, edicion, eliminacion |
| Inventario | `/api/inventario` | Stock, alertas, deducciones |
| Proveedores | `/api/proveedores` | CRUD de proveedores |
| Pedidos | `/api/pedidos` | Registro y seguimiento de pedidos |
| Ventas | `/api/ventas` | Registro y consulta de ventas |
| Usuarios | `/api/usuarios` | Gestion admin de usuarios |

## Evidencias recomendadas APF3

1. Backend ejecutando en `http://localhost:8081`.
2. `GET /api/estado` con respuesta `ok: true`.
3. Login exitoso y token JWT recibido.
4. Peticion protegida sin token devuelve 401.
5. Peticion protegida con token devuelve datos.
6. Angular en `http://localhost:4200` consumiendo `/api/productos`.
7. Formulario Angular mostrando validaciones del cliente.
8. Error del backend mostrado en pantalla.

## Verificacion tecnica fase 1

Comando:

```powershell
.\mvnw.cmd test
```

La suite debe pasar con pruebas de seguridad, validacion, JWT, CRUD y CORS para Angular.
