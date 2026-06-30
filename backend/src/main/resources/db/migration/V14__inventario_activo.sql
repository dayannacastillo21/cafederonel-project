-- V14: Campo activo en inventario para desactivar insumos sin eliminar el historial.

ALTER TABLE inventario
    ADD COLUMN IF NOT EXISTS activo BOOLEAN NOT NULL DEFAULT TRUE;

UPDATE inventario
SET activo = TRUE
WHERE activo IS NULL;

CREATE INDEX IF NOT EXISTS idx_inventario_activo ON inventario (activo);

COMMENT ON COLUMN inventario.activo IS 'Indica si el insumo esta activo en almacen';
