# Desplegar en Render (PostgreSQL + Spring Boot)

Render **no pide** un archivo `.env` en GitHub. Lo que pide son **variables de entorno** en su panel. Tu proyecto ya está preparado: `application.properties` lee esas variables con `${NOMBRE}`.

## Cómo se conecta (sin carpeta mágica)

```
Render (variables de entorno)
        ↓
application.properties  →  spring.datasource.url / user / password
        ↓
PostgreSQL (base en Render)
```

Archivo que define la conexión (en el repo):

`src/main/resources/application.properties`

## Subir tu base LOCAL a Render (pg_dump)

Render **no tiene botón “subir .sql”** en el formulario de New Postgres. El flujo es:

1. Crear la instancia vacía en Render (botón **Create Database**).
2. Exportar tu PostgreSQL local con `pg_dump`.
3. Importar en Render con `psql` usando la **External Database URL**.

### Formulario “New Postgres” (como tu captura)

| Campo | Qué poner |
|-------|-----------|
| **Name** | `inventario-backend-db` (nombre del servicio en Render; puede ser distinto al de tu PC) |
| **Database** (opcional) | `inventario_backenddatabase` (igual que local, si quieres) |
| **User** (opcional) | déjalo vacío → Render genera uno |
| **Region** | **Frankfurt** si el Web Service también estará ahí |

Luego **Create Database** y espera estado **Available**.

### Exportar desde tu PC (PowerShell)

```powershell
$env:PGPASSWORD = "3684213jJ"
pg_dump -h localhost -U postgres -d inventario_backenddatabase --no-owner --no-acl -f "C:\SpringProjectsnew\inventario_backend\backup_local.sql"
```

### Importar en Render

En el panel de la BD → **Info** → copia **External Database URL** (forma `postgresql://usuario:password@host:5432/nombre_bd`).

```powershell
psql "postgresql://USUARIO:PASSWORD@HOST:5432/NOMBRE_BD" -f "C:\SpringProjectsnew\inventario_backend\backup_local.sql"
```

(Pega la URL completa de Render entre comillas; si la contraseña tiene caracteres raros, codifícala en la URL o usa variables de pgAdmin.)

### Alternativa sin copiar datos

Si solo quieres **tablas vacías + demo Flyway** (admin, productos de V7), **no hagas dump**: despliega el backend y Flyway crea todo al arrancar. Perderías los productos que creaste a mano en local salvo que los vuelvas a insertar o uses el dump.

---

## Paso 1 — Base de datos en Render

1. [dashboard.render.com](https://dashboard.render.com) → **New +** → **PostgreSQL**.
2. Nombre ej. `inventario-db`, plan Free.
3. Cuando esté lista, en **Info** copia:
   - **Internal Database URL** (para el backend en Render)
   - Host, Port, Database, User, Password

URL JDBC para Spring (formato obligatorio):

```text
jdbc:postgresql://HOST:PUERTO/NOMBRE_BD
```

Ejemplo (valores ficticios):

```text
jdbc:postgresql://dpg-xxxxx-a/inventario_backenddatabase
```

Usuario y contraseña: los que muestra Render en **Credentials**.

## Paso 2 — Web Service (backend)

1. **New +** → **Web Service** → conecta el repo `inventario_backend`.
2. **Runtime:** Docker (usa el `Dockerfile` del repo) o Java según prefieras.
3. **Health Check Path:** `/api/estado`

## Paso 3 — Variables de entorno (esto reemplaza al .env)

En el Web Service → **Environment** → **Add Environment Variable**:

| Variable | Obligatoria | Ejemplo / notas |
|----------|-------------|-----------------|
| `DB_HOST` | Sí | Host **interno** ej. `dpg-d8bobn3eo5us73dl9e3g-a` (no el usuario) |
| `DB_PORT` | No | `5432` |
| `DB_NAME` | Sí | `inventario_backenddatabase` |
| `DB_USER` | Sí | `inventario_backenddatabase_user` |
| `DB_PASSWORD` | Sí | Password de la BD Render |
| `DB_SSLMODE` | No | `require` (en Render interno suele ir bien) |

**Error típico:** poner `DB_URL=inventario_backenddatabase_user` → el deploy falla con  
`Driver claims to not accept jdbcUrl, inventario_backenddatabase_user`.  
**Solución:** borra esa `DB_URL` incorrecta o usa JDBC completo que empiece con `jdbc:postgresql://`.
| `JWT_SECRET` | Sí | Mínimo 32 caracteres, aleatorio y distinto al de desarrollo |
| `CORS_ALLOWED_ORIGINS` | Recomendado | URL de tu frontend, ej. `https://mi-app.onrender.com` |
| `PORT` | No | Render la define solo (no hace falta ponerla) |
| `JPA_DDL_AUTO` | No | `validate` (recomendado) |

**No subas** `DB_PASSWORD` ni `JWT_SECRET` al código en GitHub.

### Atajo: vincular BD al servicio

En el Web Service → **Environment** → **Add from Database** → elige tu PostgreSQL. Render crea variables como host, user, password.

Luego agrega **a mano** `DB_URL` en formato JDBC:

```text
jdbc:postgresql://<host-interno>:<puerto>/<nombre-bd>
```

(El `connectionString` de Render suele ser `postgresql://...`; Spring necesita el prefijo `jdbc:`.)

## Paso 4 — Flyway

Al arrancar, Flyway ejecuta `src/main/resources/db/migration/` (V0–V7) y crea tablas + datos demo.

Usuario admin demo (V7):

- Email: `admin@cafedronel.com`
- Password: `password`

## Paso 5 — Probar

```text
GET https://TU-SERVICIO.onrender.com/api/estado
```

Debe responder `ok: true`.

Login:

```text
POST https://TU-SERVICIO.onrender.com/api/auth/sesiones
Content-Type: application/json

{"email":"admin@cafedronel.com","password":"password"}
```

## Blueprint (opcional)

El repo incluye `render.yaml`. En Render: **New Blueprint** → repo → aplica el archivo y revisa las variables generadas.

## Local vs Render

| | Local | Render |
|---|--------|--------|
| Config | `application.properties` + opcional `application-local.properties` | Variables en panel Environment |
| Archivo `.env` | Opcional (no lo usa Spring por defecto) | **No hace falta** |
| Puerto | 8081 por defecto | `PORT` automático de Render |

## Errores frecuentes

| Error | Causa |
|-------|--------|
| `password authentication failed` | `DB_USER` / `DB_PASSWORD` incorrectos |
| `Connection refused` | `DB_URL` con host externo en vez de **Internal** (o al revés) |
| `Could not resolve placeholder` | Falta `JWT_SECRET` o `DB_PASSWORD` en Render |
| Flyway checksum | No cambiar migraciones ya aplicadas; usar `flyway repair` solo en local |

Plantilla de nombres de variables: [.env.example](../.env.example) (solo referencia, no subir secretos).
