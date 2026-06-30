-- =============================================================================
-- Corregir fecha_creacion en productos (hora Perú, sin desfases raros en Postman)
--
-- Problema habitual: timestamp sin zona + app/JSON con otra zona → horas "absurdas".
-- Este script:
--   1) Pone DEFAULT y trigger en INSERT (America/Lima)
--   2) Normaliza todas las filas existentes a la hora actual de Lima
--
-- Ejecutar en pgAdmin (F5) o:
--   psql -h localhost -U postgres -d inventario_backenddatabase -f database/scripts/corregir_fechas_creacion_productos.sql
-- =============================================================================

BEGIN;

-- Hora local Perú en columna timestamp without time zone
ALTER TABLE productos
    ALTER COLUMN fecha_creacion
    SET DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'America/Lima');

CREATE OR REPLACE FUNCTION fn_productos_fecha_creacion()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF TG_OP = 'INSERT' AND NEW.fecha_creacion IS NULL THEN
        NEW.fecha_creacion := (CURRENT_TIMESTAMP AT TIME ZONE 'America/Lima');
    END IF;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_productos_fecha_creacion ON productos;

CREATE TRIGGER trg_productos_fecha_creacion
    BEFORE INSERT ON productos
    FOR EACH ROW
    EXECUTE FUNCTION fn_productos_fecha_creacion();

-- Normalizar datos actuales (demo, Flyway, Postman con horas mezcladas)
UPDATE productos
SET fecha_creacion = (CURRENT_TIMESTAMP AT TIME ZONE 'America/Lima');

SELECT id, nombre, fecha_creacion
FROM productos
ORDER BY id;

COMMIT;
