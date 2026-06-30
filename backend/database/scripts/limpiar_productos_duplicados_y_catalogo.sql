-- =============================================================================
-- Limpiar productos duplicados (mismo nombre, distinto id) e insertar catálogo
-- Base: inventario_backenddatabase | Tabla: productos
--
-- Uso en pgAdmin: abrir este archivo y ejecutar todo (F5).
-- Uso en consola:
--   psql -h localhost -U postgres -d inventario_backenddatabase -f database/scripts/limpiar_productos_duplicados_y_catalogo.sql
-- =============================================================================

BEGIN;

-- -----------------------------------------------------------------------------
-- 1) Vista previa: duplicados por nombre
-- -----------------------------------------------------------------------------
SELECT nombre, COUNT(*) AS cantidad, MIN(id) AS id_a_conservar, ARRAY_AGG(id ORDER BY id) AS todos_los_ids
FROM productos
GROUP BY nombre
HAVING COUNT(*) > 1
ORDER BY nombre;

-- -----------------------------------------------------------------------------
-- 2) Por cada nombre duplicado, conservar el id MENOR y borrar el resto
--    Antes: reasignar FK en pedidos/ventas (por si Postman ya creó líneas)
-- -----------------------------------------------------------------------------
WITH por_nombre AS (
    SELECT nombre, MIN(id) AS id_conservar
    FROM productos
    GROUP BY nombre
    HAVING COUNT(*) > 1
),
a_borrar AS (
    SELECT p.id AS id_borrar, pn.id_conservar
    FROM productos p
    INNER JOIN por_nombre pn ON p.nombre = pn.nombre AND p.id <> pn.id_conservar
)
UPDATE detalle_pedido dp
SET producto_id = ab.id_conservar
FROM a_borrar ab
WHERE dp.producto_id = ab.id_borrar;

WITH por_nombre AS (
    SELECT nombre, MIN(id) AS id_conservar
    FROM productos
    GROUP BY nombre
    HAVING COUNT(*) > 1
),
a_borrar AS (
    SELECT p.id AS id_borrar, pn.id_conservar
    FROM productos p
    INNER JOIN por_nombre pn ON p.nombre = pn.nombre AND p.id <> pn.id_conservar
)
UPDATE ventas v
SET producto_id = ab.id_conservar
FROM a_borrar ab
WHERE v.producto_id = ab.id_borrar;

WITH por_nombre AS (
    SELECT nombre, MIN(id) AS id_conservar
    FROM productos
    GROUP BY nombre
    HAVING COUNT(*) > 1
)
DELETE FROM productos p
USING por_nombre pn
WHERE p.nombre = pn.nombre
  AND p.id <> pn.id_conservar;

-- -----------------------------------------------------------------------------
-- 3) (Opcional) Quitar productos de prueba Postman "Mocaccino Demo APF02*"
--    Descomenta el bloque siguiente si solo quieres el menú real de cafetería.
-- -----------------------------------------------------------------------------
/*
DELETE FROM productos
WHERE nombre LIKE 'Mocaccino Demo APF02%';
*/

-- -----------------------------------------------------------------------------
-- 4) Insertar productos del catálogo (sin duplicar por nombre)
-- -----------------------------------------------------------------------------
INSERT INTO productos (nombre, precio, categoria, descripcion, activo)
SELECT 'Café Americano', 8.00, 'bebidas', 'Café negro tradicional', TRUE
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Café Americano');

INSERT INTO productos (nombre, precio, categoria, descripcion, activo)
SELECT 'Cappuccino', 12.00, 'bebidas', 'Espresso con leche espumada', TRUE
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Cappuccino');

INSERT INTO productos (nombre, precio, categoria, descripcion, activo)
SELECT 'Latte', 11.50, 'bebidas', 'Espresso con leche vaporizada', TRUE
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Latte');

INSERT INTO productos (nombre, precio, categoria, descripcion, activo)
SELECT 'Mocaccino', 14.50, 'bebidas', 'Chocolate, espresso y leche', TRUE
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Mocaccino');

INSERT INTO productos (nombre, precio, categoria, descripcion, activo)
SELECT 'Té verde', 7.00, 'bebidas', 'Infusión de té verde', TRUE
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Té verde');

INSERT INTO productos (nombre, precio, categoria, descripcion, activo)
SELECT 'Croissant', 6.50, 'comida', 'Panadería francesa', TRUE
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Croissant');

INSERT INTO productos (nombre, precio, categoria, descripcion, activo)
SELECT 'Sandwich jamón y queso', 9.50, 'comida', 'Sandwich caliente', TRUE
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Sandwich jamón y queso');

INSERT INTO productos (nombre, precio, categoria, descripcion, activo)
SELECT 'Cheesecake', 14.00, 'postres', 'Tarta de queso', TRUE
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Cheesecake');

INSERT INTO productos (nombre, precio, categoria, descripcion, activo)
SELECT 'Brownie', 8.50, 'postres', 'Brownie de chocolate', TRUE
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Brownie');

-- -----------------------------------------------------------------------------
-- 5) Reordenar IDs: 1, 2, 3... (sin huecos; orden por categoría y nombre)
-- -----------------------------------------------------------------------------
CREATE TEMP TABLE mapa_producto_ids (
    old_id INTEGER PRIMARY KEY,
    new_id INTEGER NOT NULL UNIQUE
);

INSERT INTO mapa_producto_ids (old_id, new_id)
SELECT id, ROW_NUMBER() OVER (ORDER BY categoria, nombre, id)::INTEGER
FROM productos;

ALTER TABLE detalle_pedido DROP CONSTRAINT IF EXISTS fk_detalle_pedido_producto;
ALTER TABLE ventas DROP CONSTRAINT IF EXISTS fk_ventas_producto;

UPDATE detalle_pedido dp
SET producto_id = -m.new_id
FROM mapa_producto_ids m
WHERE dp.producto_id = m.old_id;

UPDATE ventas v
SET producto_id = -m.new_id
FROM mapa_producto_ids m
WHERE v.producto_id = m.old_id;

UPDATE productos p
SET id = -m.new_id
FROM mapa_producto_ids m
WHERE p.id = m.old_id;

UPDATE productos SET id = -id WHERE id < 0;
UPDATE detalle_pedido SET producto_id = -producto_id WHERE producto_id < 0;
UPDATE ventas SET producto_id = -producto_id WHERE producto_id < 0;

ALTER TABLE detalle_pedido
    ADD CONSTRAINT fk_detalle_pedido_producto
        FOREIGN KEY (producto_id) REFERENCES productos (id);

ALTER TABLE ventas
    ADD CONSTRAINT fk_ventas_producto
        FOREIGN KEY (producto_id) REFERENCES productos (id);

SELECT setval(
    pg_get_serial_sequence('productos', 'id'),
    COALESCE((SELECT MAX(id) FROM productos), 1),
    (SELECT MAX(id) IS NOT NULL FROM productos)
);

-- -----------------------------------------------------------------------------
-- 6) Verificación final
-- -----------------------------------------------------------------------------
SELECT COUNT(*) AS total_productos FROM productos;

SELECT id, nombre, precio, categoria, activo, fecha_creacion
FROM productos
ORDER BY id;

COMMIT;
