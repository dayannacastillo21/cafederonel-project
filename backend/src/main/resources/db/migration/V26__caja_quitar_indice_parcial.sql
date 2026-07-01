-- V26: Quitar indice parcial PostgreSQL; la unicidad se valida en CajaService (compatible con H2).
DROP INDEX IF EXISTS ux_caja_usuario_abierta;
