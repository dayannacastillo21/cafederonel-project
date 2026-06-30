-- Normaliza unidades de medida mal cargadas o ambiguas en insumos de empaque.

UPDATE inventario
SET unidad = 'unid.'
WHERE unidad IS NULL
   OR btrim(unidad) = ''
   OR LOWER(btrim(unidad)) IN (
        'no identificados',
        'no identificados.',
        'no identificado',
        'sin unidad',
        'sin definir',
        'n/a',
        'na',
        'individuos',
        'individuos.',
        'individuo',
        'unidad',
        'unidades',
        'und',
        'und.',
        'pieza',
        'piezas'
    );

UPDATE inventario
SET unidad = 'unid.'
WHERE LOWER(nombre_insumo) LIKE '%vaso%'
   OR LOWER(nombre_insumo) LIKE '%servilleta%'
   OR LOWER(nombre_insumo) LIKE '%tapa%'
   OR LOWER(nombre_insumo) LIKE '%bolsa%'
   OR LOWER(nombre_insumo) LIKE '%cuchara%'
   OR LOWER(nombre_insumo) LIKE '%popote%'
   OR LOWER(categoria) = 'empaques';

UPDATE inventario
SET categoria = 'Empaques'
WHERE categoria = 'General'
  AND (
        LOWER(nombre_insumo) LIKE '%vaso%'
     OR LOWER(nombre_insumo) LIKE '%servilleta%'
     OR LOWER(nombre_insumo) LIKE '%tapa%'
     OR LOWER(nombre_insumo) LIKE '%bolsa%'
     OR LOWER(nombre_insumo) LIKE '%empaque%'
  );

COMMENT ON COLUMN inventario.unidad IS 'Unidad de medida del stock: unid., kg, litros, paquetes, etc.';
