-- =============================================================================
-- Eliminar UN producto por id y todo lo que lo referencia
-- Tablas: detalle_pedido, ventas → productos
--
-- Edita solo la variable id_producto (línea ~15) y ejecuta en pgAdmin (F5).
--
-- psql:
--   psql -h localhost -U postgres -d inventario_backenddatabase -f database/scripts/eliminar_producto_por_id.sql
-- =============================================================================

BEGIN;

-- ===== CAMBIAR SOLO ESTE NÚMERO =====
DO $eliminar$
DECLARE
    id_producto     INTEGER := 1;
    nombre_producto VARCHAR(150);
    n_detalle       INTEGER;
    n_ventas        INTEGER;
BEGIN
    SELECT nombre INTO nombre_producto
    FROM productos
    WHERE id = id_producto;

    IF nombre_producto IS NULL THEN
        RAISE EXCEPTION 'No existe ningún producto con id = %', id_producto;
    END IF;

    RAISE NOTICE 'Producto a eliminar: id=% nombre=%', id_producto, nombre_producto;

    -- Vista previa (solo informativa; se borra en los DELETE siguientes)
    RAISE NOTICE 'Líneas en detalle_pedido: %',
        (SELECT COUNT(*) FROM detalle_pedido WHERE producto_id = id_producto);
    RAISE NOTICE 'Filas en ventas: %',
        (SELECT COUNT(*) FROM ventas WHERE producto_id = id_producto);

    DELETE FROM detalle_pedido WHERE producto_id = id_producto;
    GET DIAGNOSTICS n_detalle = ROW_COUNT;

    DELETE FROM ventas WHERE producto_id = id_producto;
    GET DIAGNOSTICS n_ventas = ROW_COUNT;

    DELETE FROM productos WHERE id = id_producto;

    RAISE NOTICE 'Listo. Eliminadas % líneas de pedido, % ventas y el producto id=%.',
        n_detalle, n_ventas, id_producto;
END $eliminar$;

-- Catálogo restante (los ids pueden tener huecos; opcional: reordenar_ids_productos.sql)
SELECT id, nombre, precio, categoria
FROM productos
ORDER BY id;

COMMIT;
