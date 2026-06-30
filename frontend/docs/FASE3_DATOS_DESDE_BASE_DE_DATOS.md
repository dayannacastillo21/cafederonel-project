# Fase 3 - Datos desde base de datos

## Objetivo

El frontend Angular ya no usa filas fijas escritas dentro de los componentes.
Las pantallas consumen datos reales del backend Spring Boot, y el backend lee
esos datos desde PostgreSQL.

## Flujo implementado

```text
Angular
  -> AuthService obtiene JWT en /api/auth/sesiones
  -> AuthInterceptor envia Authorization: Bearer <token>
  -> CafederonelApiService consume endpoints protegidos
  -> Spring Boot consulta PostgreSQL con JPA
```

## Scripts SQL

Datos empresariales:

```text
backend/src/main/resources/db/migration/V8__datos_empresariales_cafederonel.sql
backend/database/scripts/V8__datos_empresariales_cafederonel.sql
```

Credenciales demo normalizadas:

```text
backend/src/main/resources/db/migration/V9__normalizar_credenciales_demo.sql
backend/database/scripts/V9__normalizar_credenciales_demo.sql
```

Roles empresariales:

```text
backend/src/main/resources/db/migration/V10__roles_empresariales_cafederonel.sql
backend/database/scripts/V10__roles_empresariales_cafederonel.sql
```

## Login demo

```text
Correo: admin@cafedronel.com
Clave:  password
Rol:    ADMIN
```

## Roles empresariales

| Rol | Responsable | Enfoque |
| --- | --- | --- |
| `ADMIN` | Administrador | Control total del sistema |
| `CAJERO` | Caja / Ventas | Registro operativo de ventas y pedidos |
| `INVENTARIO` | Encargado de inventario | Control de productos, stock y proveedores |
| `CONTADOR` | Contabilidad | Revision financiera, ventas y reportes |

## Usuarios demo por rol

```text
ADMIN
Correo: admin@cafedronel.com
Clave:  password

CAJERO
Correo: caja@cafedronel.com
Clave:  password

INVENTARIO
Correo: inventario@cafedronel.com
Clave:  password

CONTADOR
Correo: contador@cafedronel.com
Clave:  password
```

## Endpoints conectados

| Pantalla | Endpoint |
| --- | --- |
| Dashboard | `/api/productos`, `/api/inventario`, `/api/pedidos`, `/api/ventas`, `/api/usuarios` |
| Productos | `/api/productos` |
| Inventario | `/api/inventario` |
| Proveedores | `/api/proveedores` |
| Pedidos | `/api/pedidos` |
| Ventas | `/api/ventas` |
| Usuarios | `/api/usuarios` |

## Archivos Angular clave

```text
src/app/core/auth/auth.service.ts
src/app/core/auth/auth.interceptor.ts
src/app/core/auth/auth.guard.ts
src/app/services/cafederonel-api.service.ts
src/app/core/api/api.config.ts
```

## Verificacion

```powershell
cd C:\SpringProjectsnew\cafederonel-project\backend
.\mvnw.cmd test

cd C:\SpringProjectsnew\cafederonel-project\frontend
npm run build
npm test -- --watch=false
```
