-- V17: Fechas de vencimiento y lotes completos en inventario.

UPDATE inventario
SET lote = 'L-GEN-' || LPAD(CAST(id AS VARCHAR), 4, '0')
WHERE lote IS NULL OR btrim(lote) = '';

UPDATE inventario
SET fecha_vencimiento = CASE
    WHEN LOWER(categoria) = 'cafe' OR LOWER(nombre_insumo) LIKE '%cafe%' OR LOWER(nombre_insumo) LIKE '%café%' THEN DATE '2026-12-31'
    WHEN LOWER(categoria) = 'lacteos' OR LOWER(nombre_insumo) LIKE '%leche%' OR LOWER(nombre_insumo) LIKE '%crema%' OR LOWER(nombre_insumo) LIKE '%mantequilla%' OR LOWER(nombre_insumo) LIKE '%queso%' THEN DATE '2026-08-15'
    WHEN LOWER(categoria) = 'frutas' OR LOWER(nombre_insumo) LIKE '%fresa%' OR LOWER(nombre_insumo) LIKE '%naranja%' OR LOWER(nombre_insumo) LIKE '%maracuya%' OR LOWER(nombre_insumo) LIKE '%pulpa%' THEN DATE '2026-10-10'
    WHEN LOWER(categoria) = 'cocina' OR LOWER(nombre_insumo) LIKE '%pollo%' OR LOWER(nombre_insumo) LIKE '%pavo%' THEN DATE '2026-08-02'
    WHEN LOWER(categoria) = 'panaderia' OR LOWER(nombre_insumo) LIKE '%pan%' OR LOWER(nombre_insumo) LIKE '%croissant%' THEN DATE '2026-07-14'
    WHEN LOWER(categoria) = 'reposteria' OR LOWER(nombre_insumo) LIKE '%harina%' OR LOWER(nombre_insumo) LIKE '%chocolate%' THEN DATE '2027-02-28'
    WHEN LOWER(categoria) = 'empaques' OR LOWER(nombre_insumo) LIKE '%vaso%' OR LOWER(nombre_insumo) LIKE '%servilleta%' OR LOWER(nombre_insumo) LIKE '%bolsa%' OR LOWER(nombre_insumo) LIKE '%tapa%' THEN DATE '2028-01-01'
    WHEN LOWER(nombre_insumo) LIKE '%jarabe%' OR LOWER(nombre_insumo) LIKE '%azucar%' OR LOWER(nombre_insumo) LIKE '%azúcar%' THEN DATE '2027-06-30'
    ELSE DATE '2026-12-31'
END
WHERE fecha_vencimiento IS NULL;

INSERT INTO lotes_inventario (inventario_id, codigo_lote, cantidad, fecha_vencimiento, costo_unitario, activo)
SELECT
    i.id,
    COALESCE(NULLIF(btrim(i.lote), ''), 'L-GEN-' || LPAD(CAST(i.id AS VARCHAR), 4, '0')),
    i.cantidad,
    i.fecha_vencimiento,
    i.precio_unitario,
    COALESCE(i.activo, TRUE)
FROM inventario i
WHERE NOT EXISTS (
    SELECT 1
    FROM lotes_inventario l
    WHERE l.inventario_id = i.id
      AND l.codigo_lote = COALESCE(NULLIF(btrim(i.lote), ''), 'L-GEN-' || LPAD(CAST(i.id AS VARCHAR), 4, '0'))
);

UPDATE lotes_inventario l
SET cantidad = i.cantidad,
    fecha_vencimiento = i.fecha_vencimiento,
    costo_unitario = i.precio_unitario,
    activo = COALESCE(i.activo, TRUE)
FROM inventario i
WHERE l.inventario_id = i.id
  AND l.codigo_lote = COALESCE(NULLIF(btrim(i.lote), ''), 'L-GEN-' || LPAD(CAST(i.id AS VARCHAR), 4, '0'));

CREATE INDEX IF NOT EXISTS idx_inventario_fecha_vencimiento ON inventario (fecha_vencimiento);

COMMENT ON COLUMN inventario.fecha_vencimiento IS 'Fecha limite de consumo del lote actual del insumo';
COMMENT ON COLUMN inventario.cantidad IS 'Stock disponible actual del insumo en su unidad de medida';
