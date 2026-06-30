-- V22: Recetas completas del catalogo para calcular stock vendible
-- "Sin receta" = producto sin insumos vinculados; aqui se completan todas las lineas faltantes.

INSERT INTO inventario (
    nombre_insumo, codigo_insumo, categoria, ubicacion, lote,
    fecha_vencimiento, cantidad, unidad, stock_minimo, precio_unitario, proveedor_id, almacen_id, activo
)
SELECT
    v.nombre_insumo, v.codigo_insumo, v.categoria, 'Almacen central', 'L-V22-2606',
    DATE '2027-06-30', v.cantidad, v.unidad, v.stock_minimo, v.precio, p.id, 1, TRUE
FROM (
    VALUES
        ('Te verde en saquitos', 'CAF-INS-TE-001', 'Bebidas', 800, 'unid.', 200, 0.35),
        ('Agua embotellada 500ml', 'CAF-INS-AGU-500', 'Bebidas', 600, 'unid.', 120, 1.20),
        ('Jamon laminado', 'CAF-INS-JAM-001', 'Cocina', 45, 'kg', 15, 16.50),
        ('Huevos frescos', 'CAF-INS-HUE-001', 'Cocina', 360, 'unid.', 120, 0.45),
        ('Avena integral', 'CAF-INS-AVE-001', 'Desayunos', 80, 'kg', 25, 6.80),
        ('Yogurt natural', 'CAF-INS-YOG-001', 'Lacteos', 120, 'unid.', 40, 3.50),
        ('Papas prefritas', 'CAF-INS-PAP-001', 'Snacks', 55, 'kg', 18, 8.20),
        ('Frutos secos mix', 'CAF-INS-FRU-001', 'Snacks', 40, 'kg', 12, 14.00)
) AS v(nombre_insumo, codigo_insumo, categoria, cantidad, unidad, stock_minimo, precio)
JOIN proveedores p ON p.email = 'compras@distribuidoralima.pe'
WHERE NOT EXISTS (SELECT 1 FROM inventario i WHERE i.codigo_insumo = v.codigo_insumo);

INSERT INTO producto_receta (producto_id, inventario_id, cantidad_insumo, unidad, activo)
SELECT p.id, i.id, v.cantidad, v.unidad, TRUE
FROM (
    VALUES
        -- Bebidas calientes
        ('CAF-BHC-003', 'Cafe especial', 0.014, 'kg'),
        ('CAF-BHC-003', 'Leche fresca', 0.060, 'litros'),
        ('CAF-BHC-003', 'Vasos biodegradables', 1.000, 'unid.'),
        ('CAF-BHC-004', 'Cafe especial', 0.020, 'kg'),
        ('CAF-BHC-004', 'Vasos biodegradables', 1.000, 'unid.'),
        ('CAF-BHC-005', 'Cafe especial', 0.018, 'kg'),
        ('CAF-BHC-005', 'Leche fresca', 0.280, 'litros'),
        ('CAF-BHC-005', 'Vasos biodegradables', 1.000, 'unid.'),
        ('CAF-BHC-008', 'Cafe especial', 0.022, 'kg'),
        ('CAF-BHC-008', 'Leche fresca', 0.250, 'litros'),
        ('CAF-BHC-009', 'Chocolate bitter', 0.035, 'kg'),
        ('CAF-BHC-009', 'Leche fresca', 0.300, 'litros'),
        ('CAF-BHC-010', 'Te verde en saquitos', 1.000, 'unid.'),
        ('CAF-BHC-010', 'Azucar rubia', 0.010, 'kg'),
        ('CAF-BHC-011', 'Te verde en saquitos', 1.000, 'unid.'),
        ('CAF-BHC-012', 'Cafe especial', 0.018, 'kg'),
        ('CAF-BHC-012', 'Leche fresca', 0.250, 'litros'),
        ('CAF-BHC-012', 'Jarabe de vainilla', 0.030, 'botellas'),
        -- Bebidas frias
        ('CAF-BFR-004', 'Naranja fresca', 0.180, 'kg'),
        ('CAF-BFR-004', 'Azucar rubia', 0.020, 'kg'),
        ('CAF-BFR-005', 'Naranja fresca', 0.250, 'kg'),
        ('CAF-BFR-006', 'Cafe especial', 0.018, 'kg'),
        ('CAF-BFR-006', 'Leche fresca', 0.280, 'litros'),
        ('CAF-BFR-006', 'Vasos biodegradables', 1.000, 'unid.'),
        ('CAF-BFR-007', 'Fresa congelada', 0.150, 'kg'),
        ('CAF-BFR-007', 'Yogurt natural', 0.500, 'unid.'),
        ('CAF-BFR-008', 'Agua embotellada 500ml', 1.000, 'unid.'),
        -- Postres
        ('CAF-POS-002', 'Harina pastelera', 0.080, 'kg'),
        ('CAF-POS-002', 'Queso crema', 0.050, 'kg'),
        ('CAF-POS-003', 'Cafe especial', 0.015, 'kg'),
        ('CAF-POS-003', 'Queso crema', 0.100, 'kg'),
        ('CAF-POS-003', 'Harina pastelera', 0.050, 'kg'),
        ('CAF-POS-004', 'Harina pastelera', 0.100, 'kg'),
        ('CAF-POS-004', 'Mantequilla', 0.030, 'kg'),
        ('CAF-POS-005', 'Harina pastelera', 0.050, 'kg'),
        ('CAF-POS-005', 'Chocolate bitter', 0.020, 'kg'),
        ('CAF-POS-006', 'Harina pastelera', 0.120, 'kg'),
        ('CAF-POS-006', 'Leche fresca', 0.200, 'litros'),
        ('CAF-POS-006', 'Queso crema', 0.050, 'kg'),
        ('CAF-POS-007', 'Harina pastelera', 0.080, 'kg'),
        ('CAF-POS-007', 'Azucar rubia', 0.040, 'kg'),
        ('CAF-POS-008', 'Harina pastelera', 0.050, 'kg'),
        ('CAF-POS-008', 'Mantequilla', 0.020, 'kg'),
        ('CAF-POS-009', 'Harina pastelera', 0.090, 'kg'),
        ('CAF-POS-009', 'Chocolate bitter', 0.025, 'kg'),
        ('CAF-POS-010', 'Harina pastelera', 0.100, 'kg'),
        ('CAF-POS-010', 'Leche fresca', 0.150, 'litros'),
        ('CAF-POS-010', 'Mantequilla', 0.030, 'kg'),
        -- Sandwiches
        ('CAF-SAN-002', 'Pan molde integral', 2.000, 'unid.'),
        ('CAF-SAN-002', 'Queso mozzarella', 0.060, 'kg'),
        ('CAF-SAN-005', 'Pan molde integral', 1.000, 'unid.'),
        ('CAF-SAN-005', 'Pollo fileteado', 0.100, 'kg'),
        ('CAF-SAN-006', 'Pan molde integral', 2.000, 'unid.'),
        ('CAF-SAN-006', 'Pollo fileteado', 0.070, 'kg'),
        ('CAF-SAN-007', 'Pan molde integral', 2.000, 'unid.'),
        ('CAF-SAN-007', 'Pollo fileteado', 0.120, 'kg'),
        ('CAF-SAN-007', 'Queso mozzarella', 0.040, 'kg'),
        ('CAF-SAN-008', 'Pan molde integral', 3.000, 'unid.'),
        ('CAF-SAN-008', 'Pollo fileteado', 0.100, 'kg'),
        ('CAF-SAN-008', 'Pavo laminado', 0.050, 'kg'),
        ('CAF-SAN-008', 'Huevos frescos', 1.000, 'unid.'),
        -- Panaderia
        ('CAF-PAN-001', 'Croissant base', 1.000, 'unid.'),
        ('CAF-PAN-001', 'Mantequilla', 0.015, 'kg'),
        ('CAF-PAN-002', 'Harina pastelera', 0.080, 'kg'),
        ('CAF-PAN-002', 'Pollo fileteado', 0.070, 'kg'),
        ('CAF-PAN-003', 'Harina pastelera', 0.080, 'kg'),
        ('CAF-PAN-003', 'Pollo fileteado', 0.065, 'kg'),
        ('CAF-PAN-004', 'Harina pastelera', 0.060, 'kg'),
        ('CAF-PAN-004', 'Huevos frescos', 0.500, 'unid.'),
        ('CAF-PAN-005', 'Harina pastelera', 0.120, 'kg'),
        ('CAF-PAN-006', 'Harina pastelera', 0.150, 'kg'),
        ('CAF-PAN-007', 'Pan molde integral', 2.000, 'unid.'),
        ('CAF-PAN-007', 'Mantequilla', 0.020, 'kg'),
        ('CAF-PAN-008', 'Harina pastelera', 0.100, 'kg'),
        ('CAF-PAN-008', 'Mantequilla', 0.025, 'kg'),
        ('CAF-PAN-008', 'Azucar rubia', 0.030, 'kg'),
        -- Desayunos
        ('CAF-DES-001', 'Pan molde integral', 2.000, 'unid.'),
        ('CAF-DES-001', 'Huevos frescos', 2.000, 'unid.'),
        ('CAF-DES-001', 'Cafe especial', 0.018, 'kg'),
        ('CAF-DES-001', 'Naranja fresca', 0.200, 'kg'),
        ('CAF-DES-002', 'Pan molde integral', 2.000, 'unid.'),
        ('CAF-DES-002', 'Cafe especial', 0.018, 'kg'),
        ('CAF-DES-002', 'Queso mozzarella', 0.050, 'kg'),
        ('CAF-DES-003', 'Yogurt natural', 1.000, 'unid.'),
        ('CAF-DES-003', 'Avena integral', 0.080, 'kg'),
        ('CAF-DES-003', 'Fresa congelada', 0.080, 'kg'),
        ('CAF-DES-004', 'Huevos frescos', 3.000, 'unid.'),
        ('CAF-DES-004', 'Queso mozzarella', 0.050, 'kg'),
        ('CAF-DES-004', 'Pan molde integral', 2.000, 'unid.'),
        ('CAF-DES-005', 'Pan molde integral', 3.000, 'unid.'),
        ('CAF-DES-005', 'Huevos frescos', 2.000, 'unid.'),
        ('CAF-DES-005', 'Mantequilla', 0.030, 'kg'),
        ('CAF-DES-006', 'Avena integral', 0.100, 'kg'),
        ('CAF-DES-006', 'Leche fresca', 0.200, 'litros'),
        ('CAF-DES-006', 'Fresa congelada', 0.060, 'kg'),
        -- Ensaladas
        ('CAF-ENS-001', 'Pollo fileteado', 0.080, 'kg'),
        ('CAF-ENS-001', 'Queso mozzarella', 0.030, 'kg'),
        ('CAF-ENS-002', 'Avena integral', 0.080, 'kg'),
        ('CAF-ENS-002', 'Fresa congelada', 0.050, 'kg'),
        ('CAF-ENS-003', 'Queso mozzarella', 0.060, 'kg'),
        ('CAF-ENS-003', 'Naranja fresca', 0.050, 'kg'),
        ('CAF-ENS-004', 'Pollo fileteado', 0.120, 'kg'),
        ('CAF-ENS-004', 'Avena integral', 0.060, 'kg'),
        -- Snacks
        ('CAF-SNK-001', 'Papas prefritas', 0.080, 'kg'),
        ('CAF-SNK-002', 'Frutos secos mix', 0.060, 'kg'),
        ('CAF-SNK-003', 'Avena integral', 0.040, 'kg'),
        ('CAF-SNK-003', 'Chocolate bitter', 0.020, 'kg'),
        ('CAF-SNK-004', 'Queso mozzarella', 0.080, 'kg'),
        ('CAF-SNK-004', 'Harina pastelera', 0.050, 'kg'),
        -- Productos demo legacy (CAF-PROD)
        ('CAF-PROD-1', 'Cafe especial', 0.018, 'kg'),
        ('CAF-PROD-1', 'Vasos biodegradables', 1.000, 'unid.'),
        ('CAF-PROD-2', 'Cafe especial', 0.018, 'kg'),
        ('CAF-PROD-2', 'Leche fresca', 0.280, 'litros'),
        ('CAF-PROD-3', 'Cafe especial', 0.018, 'kg'),
        ('CAF-PROD-3', 'Leche fresca', 0.300, 'litros'),
        ('CAF-PROD-4', 'Cafe especial', 0.018, 'kg'),
        ('CAF-PROD-4', 'Chocolate bitter', 0.030, 'kg'),
        ('CAF-PROD-5', 'Cafe especial', 0.018, 'kg'),
        ('CAF-PROD-5', 'Chocolate bitter', 0.030, 'kg'),
        ('CAF-PROD-6', 'Cafe especial', 0.018, 'kg'),
        ('CAF-PROD-6', 'Chocolate bitter', 0.030, 'kg'),
        ('CAF-PROD-7', 'Te verde en saquitos', 1.000, 'unid.'),
        ('CAF-PROD-8', 'Croissant base', 1.000, 'unid.'),
        ('CAF-PROD-9', 'Pan molde integral', 2.000, 'unid.'),
        ('CAF-PROD-9', 'Jamon laminado', 0.060, 'kg'),
        ('CAF-PROD-9', 'Queso mozzarella', 0.040, 'kg'),
        ('CAF-PROD-10', 'Harina pastelera', 0.080, 'kg'),
        ('CAF-PROD-10', 'Chocolate bitter', 0.040, 'kg'),
        ('CAF-PROD-11', 'Queso crema', 0.100, 'kg'),
        ('CAF-PROD-11', 'Harina pastelera', 0.060, 'kg'),
        ('CAF-PROD-14', 'Harina pastelera', 0.120, 'kg'),
        ('CAF-PROD-14', 'Chocolate bitter', 0.050, 'kg'),
        ('CAF-PROD-15', 'Pan molde integral', 2.000, 'unid.'),
        ('CAF-PROD-15', 'Queso mozzarella', 0.050, 'kg'),
        ('CAF-PROD-17', 'Harina pastelera', 0.090, 'kg'),
        ('CAF-PROD-17', 'Chocolate bitter', 0.045, 'kg')
) AS v(sku, insumo_nombre, cantidad, unidad)
JOIN productos p ON p.sku = v.sku
JOIN inventario i ON i.nombre_insumo = v.insumo_nombre
WHERE NOT EXISTS (
    SELECT 1 FROM producto_receta pr
    WHERE pr.producto_id = p.id AND pr.inventario_id = i.id
);

-- Completar vasos/servilletas en bebidas calientes que ya tenian receta parcial
INSERT INTO producto_receta (producto_id, inventario_id, cantidad_insumo, unidad, activo)
SELECT p.id, i.id, 1.000, 'unid.', TRUE
FROM productos p
JOIN inventario i ON i.nombre_insumo = 'Vasos biodegradables'
WHERE p.sku IN ('CAF-BHC-001', 'CAF-BHC-002', 'CAF-BHC-006', 'CAF-BHC-007')
  AND NOT EXISTS (
      SELECT 1 FROM producto_receta pr
      WHERE pr.producto_id = p.id AND pr.inventario_id = i.id
  );

COMMENT ON TABLE producto_receta IS 'Receta por producto: insumos consumidos por unidad vendida';
