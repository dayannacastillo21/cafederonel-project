-- =============================================================================
-- Reordenar IDs de productos: 1, 2, 3... sin huecos
-- Orden: categoria, nombre (menú legible en pgAdmin)
-- Actualiza FK en detalle_pedido y ventas; reinicia la secuencia identity.
--
-- Uso: psql -h localhost -U postgres -d inventario_backenddatabase -f database/scripts/reordenar_ids_productos.sql
-- =============================================================================

BEGIN;

CREATE TEMP TABLE mapa_producto_ids (
    old_id INTEGER PRIMARY KEY,
    new_id INTEGER NOT NULL UNIQUE
);

INSERT INTO mapa_producto_ids (old_id, new_id)
SELECT id, ROW_NUMBER() OVER (ORDER BY categoria, nombre, id)::INTEGER
FROM productos;

SELECT old_id, new_id, nombre
FROM mapa_producto_ids m
JOIN productos p ON p.id = m.old_id
ORDER BY new_id;

ALTER TABLE detalle_pedido DROP CONSTRAINT IF EXISTS fk_detalle_pedido_producto;
ALTER TABLE ventas DROP CONSTRAINT IF EXISTS fk_ventas_producto;

-- Fase A: IDs temporales negativos (evita choque 7→2 cuando ya existe id=2)
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

-- Fase B: IDs finales positivos 1, 2, 3...
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

SELECT id, nombre, precio, categoria
FROM productos
ORDER BY id;

COMMIT;
