-- V13: Normaliza tipos numericos de productos para validacion JPA/PostgreSQL.
-- Si V11 se ejecuto manualmente con NUMERIC, estas columnas quedan alineadas con la entidad Java.

ALTER TABLE productos
    ALTER COLUMN costo TYPE DOUBLE PRECISION USING costo::DOUBLE PRECISION;

ALTER TABLE productos
    ALTER COLUMN margen_porcentaje TYPE DOUBLE PRECISION USING margen_porcentaje::DOUBLE PRECISION;
