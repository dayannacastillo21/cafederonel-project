-- Ejecutar conectado a PostgreSQL como superusuario (psql, pgAdmin, etc.)
-- No forma parte de Flyway: créala una sola vez antes de v0..v7

CREATE DATABASE cafedronel
    WITH ENCODING 'UTF8'
    TEMPLATE template0;

-- Luego conéctate a cafedronel:
-- \c cafedronel
