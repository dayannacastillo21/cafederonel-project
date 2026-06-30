# Backend Cafedronel

API REST desarrollada con Spring Boot para gestionar productos, usuarios, autenticacion, proveedores, inventario, pedidos y ventas de Cafedronel.

Este repositorio corresponde al Avance de Proyecto Final 02 (APF02). La documentacion academica detallada (JPA, CRUD, consultas, transacciones, seguridad, JWT y evidencias).

El avance anterior (APF1) cubria estructura Spring Boot, endpoints REST, capas, pruebas y documentacion base; APF02 profundiza en persistencia JPA, CRUD completo, consultas transaccionales, Spring Security y JWT.

## Documentacion del repositorio

| Documento | Contenido |
|-----------|-----------|
| [docs/APF3_BACKEND_SOPORTE.md](docs/APF3_BACKEND_SOPORTE.md) | Contrato backend para el frontend Angular APF3 |
| [AVANCE_PROYECTO_FINAL_02.md](AVANCE_PROYECTO_FINAL_02.md) | Informe tecnico APF02 (JPA, CRUD, consultas, seguridad, JWT) |
| [CHECKLIST_APF02.md](CHECKLIST_APF02.md) | Lista de verificacion para entrega y capturas |
| [database/DISENO_BASE_DATOS.md](database/DISENO_BASE_DATOS.md) | Modelo relacional y scripts SQL |
| [database/scripts/README.md](database/scripts/README.md) | Limpieza de duplicados y reordenar IDs de productos |
| [docs/postman/Cafedronel-APF02.postman_collection.json](docs/postman/Cafedronel-APF02.postman_collection.json) | Coleccion Postman para pruebas |
| [application-example.properties](src/main/resources/application-example.properties) | Plantilla de variables de entorno (sin secretos reales) |

## Objetivo del avance

Construir y documentar un backend funcional, ejecutable y verificable en Spring Boot, con rutas REST coherentes, persistencia JPA/Flyway, validaciones con DTO, seguridad JWT, manejo uniforme de errores y pruebas automatizadas suficientes para demostrar el comportamiento esperado.

## Tecnologias

| Tecnologia | Uso en el proyecto |
|------------|--------------------|
| Java 17 | Lenguaje base del backend |
| Spring Boot 3.5.x | Framework principal |
| Spring Web | Exposicion de endpoints REST |
| Spring Data JPA | Persistencia por repositorios |
| Flyway | Migraciones versionadas de base de datos |
| PostgreSQL | Base de datos de ejecucion local/despliegue |
| H2 | Base de datos aislada para pruebas |
| Spring Security | Proteccion de rutas y roles |
| JWT con `jjwt` | Tokens de autenticacion |
| Jakarta Validation | Validacion de DTOs |
| JUnit 5, MockMvc, Mockito | Pruebas unitarias e integracion ligera |
| Maven | Build, pruebas y empaquetado |

## Estructura del proyecto

```text
src/main/java/com/example/backend_cafedronel/
|-- config/           # Configuracion transversal, como CORS
|-- controller/       # Endpoints REST
|-- dto/              # DTOs de entrada y respuesta
|-- exception/        # Excepciones y GlobalExceptionHandler
|-- mapper/           # Conversion entre DTOs y modelos
|-- model/            # Entidades JPA y modelos de dominio
|-- repository/       # Repositorios Spring Data JPA
|-- security/         # JWT, filtros y reglas de seguridad
`-- service/          # Logica de negocio transaccional
```

## Arquitectura por capas

El flujo principal es:

```text
Cliente HTTP -> Controller -> DTO/Mapper -> Service -> Repository -> Base de datos
```

- Los controladores reciben solicitudes HTTP y devuelven `ResponseEntity`.
- Los DTOs validan entradas con `@Valid`, `@NotBlank`, `@NotNull`, `@Positive`, `@Email`, etc.
- Los servicios contienen la logica de negocio: calculo de totales, deduccion de stock, validacion de estados y resolucion de recursos.
- Los repositories encapsulan acceso a datos mediante Spring Data JPA.
- `GlobalExceptionHandler` centraliza errores 400, 401, 403, 404, 409 y 500.
- La seguridad usa JWT y roles `USER` / `ADMIN`.

## Persistencia y migraciones

El proyecto usa Flyway como fuente de verdad para la estructura de base de datos. Las migraciones estan en:

```text
src/main/resources/db/migration/
```

Tablas principales:

| Tabla | Modulo |
|-------|--------|
| `usuarios` | Usuarios y autenticacion |
| `productos` | Catalogo de productos |
| `proveedores` | Proveedores |
| `inventario` | Stock de insumos |
| `pedidos` | Pedidos |
| `detalle_pedido` | Lineas de pedido |
| `ventas` | Ventas |

Todos esos modulos cuentan con entidades JPA y repositories.

## Configuracion

El archivo activo `src/main/resources/application.properties` no contiene secretos reales ni defaults sensibles. Los valores criticos se leen desde variables de entorno. El archivo `src/main/resources/application-example.properties` sirve solo como ejemplo local.

| Variable | Descripcion | Requerida | Valor por defecto |
|----------|-------------|-----------|-------------------|
| `PORT` | Puerto HTTP | No | `8081` |
| `DB_URL` | URL JDBC de PostgreSQL | Si | - |
| `DB_USER` | Usuario de base de datos | Si | - |
| `DB_PASSWORD` | Password de base de datos | Si | - |
| `JWT_SECRET` | Clave JWT de minimo 32 bytes | Si | - |
| `JWT_EXPIRATION_MS` | Duracion del token | No | `86400000` |
| `JPA_DDL_AUTO` | Modo Hibernate | No | `validate` |
| `JPA_SHOW_SQL` | Mostrar SQL en logs | No | `false` |
| `CORS_ALLOWED_ORIGINS` | Origenes permitidos separados por coma | No | localhost dev |

## Ejecucion local

En PowerShell:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/inventario_backenddatabase"
$env:DB_USER="postgres"
$env:DB_PASSWORD="<password-local>"
$env:JWT_SECRET="<clave-jwt-local-de-minimo-32-bytes>"
mvn spring-boot:run
```

En Linux/macOS:

```bash
export DB_URL="jdbc:postgresql://localhost:5432/inventario_backenddatabase"
export DB_USER="postgres"
export DB_PASSWORD="<password-local>"
export JWT_SECRET="<clave-jwt-local-de-minimo-32-bytes>"
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8081`, salvo que `PORT` indique otro puerto.

## Pruebas y TDD

Comandos de verificacion:

```bash
mvn test
mvn -DskipTests package
```

Resultado actual verificado:

```text
Tests run: 46, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Las pruebas usan H2 en memoria con `src/test/resources/application.properties`.

Cobertura incluida:

| Area | Evidencia |
|------|-----------|
| Productos | Listado, consulta por id, 404, creacion valida, validacion invalida |
| Usuarios | Registro seguro sin password, validacion invalida, email duplicado |
| Autenticacion | Login valido con token y login invalido 401 |
| Proveedores | Listado, creacion, validacion invalida, 404 |
| Inventario | Creacion, validacion invalida, deduccion de stock, 404 |
| Pedidos | Creacion con total calculado, validacion invalida, 404 |
| Ventas | Creacion con total calculado, validacion invalida, 404 |
| Seguridad | Ruta publica sin token, ruta protegida sin token, permisos por rol |

## Endpoints REST

### Publicos

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| `GET` | `/` | Mensaje base de la API |
| `GET` | `/api/estado` | Healthcheck |
| `POST` | `/api/auth/sesiones` | Login y generacion de JWT |
| `POST` | `/api/usuarios` | Registro publico con rol `USER` |

### Productos

| Metodo | Ruta |
|--------|------|
| `GET` | `/api/productos` |
| `GET` | `/api/productos/{id}` |
| `GET` | `/api/productos/categoria/{categoria}` |
| `GET` | `/api/productos/activos` |
| `GET` | `/api/productos/busqueda/precio-minimo?min=` |
| `POST` | `/api/productos` |
| `PUT` | `/api/productos/{id}` |
| `DELETE` | `/api/productos/{id}` |

### Usuarios

| Metodo | Ruta |
|--------|------|
| `GET` | `/api/usuarios` |
| `GET` | `/api/usuarios/{id}` |
| `POST` | `/api/usuarios` |
| `PUT` | `/api/usuarios/{id}` |
| `DELETE` | `/api/usuarios/{id}` |

### Proveedores

| Metodo | Ruta |
|--------|------|
| `GET` | `/api/proveedores` |
| `GET` | `/api/proveedores/{id}` |
| `POST` | `/api/proveedores` |
| `PUT` | `/api/proveedores/{id}` |
| `DELETE` | `/api/proveedores/{id}` |

### Inventario

| Metodo | Ruta |
|--------|------|
| `GET` | `/api/inventario` |
| `GET` | `/api/inventario/{id}` |
| `GET` | `/api/inventario/alertas/stock-bajo` |
| `POST` | `/api/inventario` |
| `PUT` | `/api/inventario/{id}` |
| `DELETE` | `/api/inventario/{id}` |
| `POST` | `/api/inventario/{id}/deducciones` |

### Pedidos

| Metodo | Ruta |
|--------|------|
| `GET` | `/api/pedidos` |
| `GET` | `/api/pedidos/{id}` |
| `GET` | `/api/pedidos/{id}/detalles` |
| `POST` | `/api/pedidos` |
| `PUT` | `/api/pedidos/{id}` |
| `PUT` | `/api/pedidos/{id}/estado?estado=completado` |
| `DELETE` | `/api/pedidos/{id}` |

### Ventas

| Metodo | Ruta |
|--------|------|
| `GET` | `/api/ventas` |
| `GET` | `/api/ventas/{id}` |
| `GET` | `/api/ventas/usuario/{usuarioId}` |
| `GET` | `/api/ventas/estado/{estado}` |
| `POST` | `/api/ventas` |
| `PUT` | `/api/ventas/{id}` |
| `DELETE` | `/api/ventas/{id}` |

### Seguridad

| Metodo | Ruta | Rol |
|--------|------|-----|
| `GET` | `/api/admin/ping` | `ADMIN` |
| `GET` | `/api/user/ping` | `USER` o `ADMIN` |

## Autenticacion

Solicitud:

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

Respuesta:

```json
{
  "userId": 1,
  "userName": "Administrador",
  "email": "admin@cafedronel.com",
  "role": "ADMIN",
  "token": "eyJ..."
}
```

Uso del token:

```http
Authorization: Bearer <token>
```

## Manejo de errores

Los errores se responden con una estructura uniforme:

```json
{
  "timestamp": "2026-05-22T00:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Datos de entrada no validos",
  "fieldErrors": {
    "nombre": "El nombre es obligatorio"
  }
}
```

## Seguridad

- No se versionan secretos reales.
- `application.properties` no contiene password ni JWT secret por defecto.
- Las contrasenas se almacenan con BCrypt.
- Las respuestas de usuario no exponen `password`.
- Registro publico crea usuarios con rol `USER`.
- Rutas administrativas requieren rol `ADMIN`.
- CORS se configura de forma centralizada.

## Evidencias sugeridas para la entrega

Capturas pendientes recomendadas:

1. `mvn -version` mostrando Java 17 y Maven.
2. `mvn test` con `BUILD SUCCESS` y 34 pruebas.
3. `mvn -DskipTests package` con JAR generado.
4. Arranque con `mvn spring-boot:run`.
5. `GET /api/estado` devolviendo `ok: true`.
6. Login `POST /api/auth/sesiones` devolviendo JWT.
7. Endpoint protegido sin token devolviendo 401.
8. Coleccion Postman en `docs/postman/Cafedronel-APF02.postman_collection.json`.
9. CRUD de producto/proveedor/inventario/pedido/venta en Postman.
10. Estructura del proyecto en el IDE.
11. Migraciones Flyway en `src/main/resources/db/migration`.


## Despliegue

El `Dockerfile` construye el JAR con Maven y ejecuta la aplicacion con Java 17.

**Render:** no hace falta subir un `.env`. Configura las variables en el panel **Environment** del Web Service (Render las inyecta igual que un `.env`). Guia paso a paso: **[docs/DESPLIEGUE_RENDER.md](docs/DESPLIEGUE_RENDER.md)**. Plantilla de nombres: [.env.example](.env.example). Blueprint opcional: [render.yaml](render.yaml).

Variables minimas en Render:

- `DB_URL` (formato `jdbc:postgresql://host:puerto/bd`)
- `DB_USER`
- `DB_PASSWORD`
- `JWT_SECRET`
- `CORS_ALLOWED_ORIGINS`
