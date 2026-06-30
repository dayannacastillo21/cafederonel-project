-- Estados adicionales para mesas: reservada, bloqueada (ademas de disponible, ocupada, cuenta)

ALTER TABLE salon_mesas DROP CONSTRAINT IF EXISTS ck_salon_mesas_estado;

ALTER TABLE salon_mesas
    ADD CONSTRAINT ck_salon_mesas_estado
    CHECK (estado IN ('disponible', 'ocupada', 'cuenta', 'reservada', 'bloqueada'));

COMMENT ON COLUMN salon_mesas.estado IS 'disponible | ocupada | cuenta | reservada | bloqueada';
