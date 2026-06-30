# Cafederonel Project

Sistema Web de Inventario Cafedronel organizado en backend y frontend.

## Estructura

```text
cafederonel-project
|-- backend      API REST con Spring Boot
|-- frontend     Aplicacion web Angular
`-- cafederonel-project.code-workspace
```

## Nombres de entrega

- Proyecto: Sistema Web de Inventario Cafedronel
- Backend: Cafedronel Backend API
- Frontend: Cafedronel Frontend Angular

## Comandos

Backend:

```powershell
cd backend
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

Frontend:

```powershell
cd frontend
npm start
npm run build
```

## URLs locales

```text
Backend:  http://localhost:8081
Frontend: http://localhost:4200
```

## Login demo

```text
Correo: admin@cafedronel.com
Clave:  password
```

## Roles demo

```text
ADMIN       admin@cafedronel.com       password
CAJERO      caja@cafedronel.com        password
INVENTARIO  inventario@cafedronel.com  password
CONTADOR    contador@cafedronel.com    password
```

## Datos desde base de datos

El frontend consume datos reales desde Spring Boot y PostgreSQL. Los scripts
principales estan en:

```text
backend/src/main/resources/db/migration/V8__datos_empresariales_cafederonel.sql
backend/src/main/resources/db/migration/V9__normalizar_credenciales_demo.sql
backend/src/main/resources/db/migration/V10__roles_empresariales_cafederonel.sql
```

Documento de fase:

```text
frontend/docs/FASE3_DATOS_DESDE_BASE_DE_DATOS.md
```
