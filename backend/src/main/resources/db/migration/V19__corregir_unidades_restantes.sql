-- Corrige unidades mal rotuladas que aún digan "no identificados" o "individuos".

UPDATE inventario
SET unidad = 'unid.'
WHERE unidad IS NULL
   OR btrim(unidad) = ''
   OR LOWER(btrim(unidad)) LIKE 'no identificad%'
   OR LOWER(btrim(unidad)) LIKE '%individuo%'
   OR LOWER(btrim(unidad)) IN (
        'sin unidad',
        'sin definir',
        'n/a',
        'na',
        'unidad',
        'unidades',
        'und',
        'und.',
        'pieza',
        'piezas',
        'u',
        'uds',
        'uds.'
    );

UPDATE inventario
SET unidad = 'unid.'
WHERE LOWER(nombre_insumo) LIKE '%servilleta%'
   OR LOWER(nombre_insumo) LIKE '%vaso%'
   OR LOWER(nombre_insumo) LIKE '%tapa%'
   OR LOWER(categoria) = 'empaques';
