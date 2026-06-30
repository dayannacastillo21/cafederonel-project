-- V16: Codigo por empresa, RUC de 21 digitos y categorias del catalogo formal.

ALTER TABLE proveedores ALTER COLUMN ruc TYPE VARCHAR(21);

UPDATE proveedores
SET codigo_proveedor = CASE email
    WHEN 'ventas@cafeperu.com' THEN 'PRV-DIST-CAFE-PERU'
    WHEN 'contacto@lacteosnorte.com' THEN 'PRV-LACT-NORTE'
    WHEN 'ventas@andescoffee.pe' THEN 'PRV-ANDES-COFFEE'
    WHEN 'compras@distribuidoralima.pe' THEN 'PRV-DIST-LIMA'
    WHEN 'contacto@ecopack.pe' THEN 'PRV-ECOPACK-PE'
    WHEN 'pedidos@dulcenorte.pe' THEN 'PRV-DULCE-NORTE'
    ELSE codigo_proveedor
END,
    categoria = CASE email
    WHEN 'ventas@cafeperu.com' THEN 'Bebidas calientes'
    WHEN 'contacto@lacteosnorte.com' THEN 'Bebidas frias'
    WHEN 'ventas@andescoffee.pe' THEN 'Bebidas calientes'
    WHEN 'compras@distribuidoralima.pe' THEN 'Sandwiches'
    WHEN 'contacto@ecopack.pe' THEN 'Insumos retail'
    WHEN 'pedidos@dulcenorte.pe' THEN 'Postres'
    ELSE categoria
END,
    ruc = CASE email
    WHEN 'ventas@cafeperu.com' THEN '201234567890123450001'
    WHEN 'contacto@lacteosnorte.com' THEN '205678901234567890002'
    WHEN 'ventas@andescoffee.pe' THEN '201122334455667788003'
    WHEN 'compras@distribuidoralima.pe' THEN '204455667788990011004'
    WHEN 'contacto@ecopack.pe' THEN '206677889900112233005'
    WHEN 'pedidos@dulcenorte.pe' THEN '203344556677889900006'
    ELSE ruc
END,
    fecha_actualizacion = CURRENT_TIMESTAMP
WHERE email IN (
    'ventas@cafeperu.com',
    'contacto@lacteosnorte.com',
    'ventas@andescoffee.pe',
    'compras@distribuidoralima.pe',
    'contacto@ecopack.pe',
    'pedidos@dulcenorte.pe'
);

COMMENT ON COLUMN proveedores.categoria IS 'Rubro alineado al catalogo categorias_producto';
COMMENT ON COLUMN proveedores.ruc IS 'Identificador tributario del proveedor (21 digitos)';
