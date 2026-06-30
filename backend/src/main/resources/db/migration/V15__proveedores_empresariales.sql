-- V15: Campos empresariales del directorio de proveedores.

ALTER TABLE proveedores ADD COLUMN IF NOT EXISTS codigo_proveedor VARCHAR(50);
ALTER TABLE proveedores ADD COLUMN IF NOT EXISTS categoria VARCHAR(80) NOT NULL DEFAULT 'General';
ALTER TABLE proveedores ADD COLUMN IF NOT EXISTS ruc VARCHAR(20);
ALTER TABLE proveedores ADD COLUMN IF NOT EXISTS contacto VARCHAR(120);
ALTER TABLE proveedores ADD COLUMN IF NOT EXISTS ciudad VARCHAR(80);
ALTER TABLE proveedores ADD COLUMN IF NOT EXISTS sitio_web VARCHAR(255);
ALTER TABLE proveedores ADD COLUMN IF NOT EXISTS notas VARCHAR(500);
ALTER TABLE proveedores ADD COLUMN IF NOT EXISTS fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

UPDATE proveedores
SET codigo_proveedor = 'CAF-PRV-' || LPAD(CAST(id AS VARCHAR), 3, '0')
WHERE codigo_proveedor IS NULL OR btrim(codigo_proveedor) = '';

UPDATE proveedores
SET categoria = CASE
    WHEN email = 'ventas@cafeperu.com' OR email = 'ventas@andescoffee.pe' THEN 'Cafe'
    WHEN email = 'contacto@lacteosnorte.com' THEN 'Lacteos'
    WHEN email = 'compras@distribuidoralima.pe' THEN 'Distribucion'
    WHEN email = 'contacto@ecopack.pe' THEN 'Empaques'
    WHEN email = 'pedidos@dulcenorte.pe' THEN 'Reposteria'
    ELSE COALESCE(NULLIF(btrim(categoria), ''), 'General')
END;

UPDATE proveedores
SET contacto = CASE email
    WHEN 'ventas@cafeperu.com' THEN 'Karla Mendoza'
    WHEN 'contacto@lacteosnorte.com' THEN 'Roberto Silva'
    WHEN 'ventas@andescoffee.pe' THEN 'Miguel Torres'
    WHEN 'compras@distribuidoralima.pe' THEN 'Lucia Vargas'
    WHEN 'contacto@ecopack.pe' THEN 'Andrea Ruiz'
    WHEN 'pedidos@dulcenorte.pe' THEN 'Pedro Salas'
    ELSE 'Area comercial'
END
WHERE contacto IS NULL OR btrim(contacto) = '';

UPDATE proveedores
SET ciudad = CASE
    WHEN direccion ILIKE '%lima%' OR direccion ILIKE '%surquillo%' THEN 'Lima'
    WHEN direccion ILIKE '%cusco%' THEN 'Cusco'
    WHEN direccion ILIKE '%chiclayo%' OR direccion ILIKE '%lambayeque%' THEN 'Chiclayo'
    ELSE 'Lima'
END
WHERE ciudad IS NULL OR btrim(ciudad) = '';

UPDATE proveedores
SET ruc = CASE email
    WHEN 'ventas@cafeperu.com' THEN '20123456781'
    WHEN 'contacto@lacteosnorte.com' THEN '20567890123'
    WHEN 'ventas@andescoffee.pe' THEN '20112233445'
    WHEN 'compras@distribuidoralima.pe' THEN '20445566778'
    WHEN 'contacto@ecopack.pe' THEN '20667788990'
    WHEN 'pedidos@dulcenorte.pe' THEN '20334455667'
    ELSE ruc
END
WHERE ruc IS NULL OR btrim(ruc) = '';

UPDATE proveedores
SET sitio_web = CASE email
    WHEN 'ventas@cafeperu.com' THEN 'https://cafeperu.com'
    WHEN 'contacto@lacteosnorte.com' THEN 'https://lacteosnorte.com'
    WHEN 'ventas@andescoffee.pe' THEN 'https://andescoffee.pe'
    WHEN 'compras@distribuidoralima.pe' THEN 'https://distribuidoralima.pe'
    WHEN 'contacto@ecopack.pe' THEN 'https://ecopack.pe'
    WHEN 'pedidos@dulcenorte.pe' THEN 'https://dulcenorte.pe'
    ELSE sitio_web
END
WHERE sitio_web IS NULL OR btrim(sitio_web) = '';

UPDATE proveedores
SET notas = CASE email
    WHEN 'ventas@cafeperu.com' THEN 'Proveedor principal de cafe verde y tostado.'
    WHEN 'contacto@lacteosnorte.com' THEN 'Entrega de lacteos tres veces por semana.'
    WHEN 'ventas@andescoffee.pe' THEN 'Cafe de origen Cusco y blends especiales.'
    WHEN 'compras@distribuidoralima.pe' THEN 'Distribucion general de insumos secos.'
    WHEN 'contacto@ecopack.pe' THEN 'Vasos, tapas y empaques biodegradables.'
    WHEN 'pedidos@dulcenorte.pe' THEN 'Insumos de reposteria y postres listos.'
    ELSE notas
END
WHERE notas IS NULL OR btrim(notas) = '';

ALTER TABLE proveedores ALTER COLUMN codigo_proveedor SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_proveedores_codigo ON proveedores (codigo_proveedor);
CREATE UNIQUE INDEX IF NOT EXISTS ux_proveedores_ruc ON proveedores (ruc);
CREATE INDEX IF NOT EXISTS idx_proveedores_categoria ON proveedores (categoria);
CREATE INDEX IF NOT EXISTS idx_proveedores_ciudad ON proveedores (ciudad);

COMMENT ON COLUMN proveedores.categoria IS 'Rubro del proveedor: Cafe, Lacteos, Empaques, etc.';
COMMENT ON COLUMN proveedores.ruc IS 'RUC peruano del proveedor';
COMMENT ON COLUMN proveedores.contacto IS 'Persona de contacto comercial';
