# Scripts SQL de mantenimiento (PostgreSQL)

## Script demo empresarial

`V8__datos_empresariales_cafederonel.sql` carga usuarios, proveedores, productos,
inventario, pedidos, detalles y ventas para que Angular muestre datos desde la
base de datos.

`V9__normalizar_credenciales_demo.sql` deja las cuentas demo listas para login.

`V10__roles_empresariales_cafederonel.sql` cambia el sistema a roles finales:
`ADMIN`, `CAJERO`, `INVENTARIO` y `CONTADOR`.

Credenciales demo:

```text
ADMIN       admin@cafedronel.com       password
CAJERO      caja@cafedronel.com        password
INVENTARIO  inventario@cafedronel.com  password
CONTADOR    contador@cafedronel.com    password
```

Scripts opcionales para **después** de pruebas con Postman o pgAdmin. No reemplazan a Flyway (`src/main/resources/db/migration/`).

## Cuándo usarlos

| Situación | Script |
|-----------|--------|
| Mismo producto repetido (mismo `nombre`, distinto `id`) | `limpiar_productos_duplicados_y_catalogo.sql` |
| IDs con huecos (2, 5, 7, 11…) y quieres 1, 2, 3… | `reordenar_ids_productos.sql` |
| Solo reordenar IDs sin limpiar duplicados | `reordenar_ids_productos.sql` |
| Borrar un producto por `id` (y sus pedidos/ventas ligados) | `eliminar_producto_por_id.sql` |
| Fechas de creación incorrectas en Postman / pgAdmin | `corregir_fechas_creacion_productos.sql` |

## `limpiar_productos_duplicados_y_catalogo.sql`

1. Muestra duplicados por `nombre`.
2. Conserva el **id más bajo** por nombre y elimina el resto.
3. Reasigna `detalle_pedido` y `ventas` si hubiera referencias.
4. Inserta productos del menú (Cappuccino, Latte, Croissant, etc.) solo si no existen.
5. Reordena IDs a 1, 2, 3… (orden: `categoria`, `nombre`).
6. Reinicia la secuencia `productos_id_seq` para que el próximo alta por API sea `MAX(id)+1`.

**Opcional:** descomenta el bloque `DELETE ... Mocaccino Demo APF02%` para quitar productos de prueba de Postman.

## `corregir_fechas_creacion_productos.sql`

Alinea `productos.fecha_creacion` con **hora de Perú** (`America/Lima`):

1. `DEFAULT` en la columna para nuevos inserts desde SQL.
2. **Trigger** `BEFORE INSERT` si la fecha viene nula.
3. `UPDATE` de todas las filas existentes a la hora actual de Lima.

Después de ejecutarlo, los **POST nuevos por Postman** deben mostrar fecha coherente si el backend usa `Producto` con `fecha_creacion` gestionada por la base (ver entidad Java).

## `eliminar_producto_por_id.sql`

Borra **un** producto y antes elimina todo lo que lo usa:

1. `detalle_pedido` donde `producto_id = X`
2. `ventas` donde `producto_id = X`
3. `productos` donde `id = X`

Edita en el script: `id_producto INTEGER := 1;` (cambia `1` por el id que quieras).

**Ejemplo:** id `1` = Café Americano. Tras borrar, los demás ids **no** se renumeran solos; si quieres 1, 2, 3… sin huecos, ejecuta después `reordenar_ids_productos.sql`.

**API:** también puedes `DELETE /api/productos/{id}` con JWT (misma restricción: si hay ventas/pedidos, fallará hasta borrar referencias o usar este script).

## `reordenar_ids_productos.sql`

Solo renumeración:

- Orden final: `categoria`, `nombre`, `id` antiguo.
- Actualiza FK en `detalle_pedido` y `ventas`.
- Usa IDs temporales negativos para evitar choques de clave primaria.
- Deja la secuencia lista para nuevos inserts.

## Ejecución

**pgAdmin:** abrir el archivo → ejecutar (F5).

**PowerShell** (ajusta usuario y base):

```powershell
$env:PGPASSWORD="tu_password"
psql -h localhost -U postgres -d inventario_backenddatabase -f database/scripts/limpiar_productos_duplicados_y_catalogo.sql
psql -h localhost -U postgres -d inventario_backenddatabase -f database/scripts/reordenar_ids_productos.sql
```

## Consulta recomendada en pgAdmin

Para ver el catálogo del 1 al N sin confusión por orden de categoría:

```sql
SELECT id, nombre, precio, categoria
FROM productos
ORDER BY id;
```

## Nota Postman

Tras reordenar, las URLs `PUT /api/productos/{id}` deben usar los **ids nuevos**. Si vuelves a crear muchos POST con el mismo nombre, ejecuta de nuevo el script de limpieza.
