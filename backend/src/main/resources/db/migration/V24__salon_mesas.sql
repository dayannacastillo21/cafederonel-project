-- Plano del salon: estado de mesas (disponible / ocupada / cuenta)

CREATE TABLE IF NOT EXISTS salon_mesas (
    numero          INTEGER PRIMARY KEY,
    capacidad       INTEGER NOT NULL DEFAULT 4,
    posicion_x      INTEGER NOT NULL DEFAULT 0,
    posicion_y      INTEGER NOT NULL DEFAULT 0,
    zona            VARCHAR(30) NOT NULL DEFAULT 'salon',
    forma           VARCHAR(20) NOT NULL DEFAULT 'cuadrada',
    estado          VARCHAR(20) NOT NULL DEFAULT 'disponible',
    pedido_id       INTEGER,
    actualizado_en  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_salon_mesas_pedido
        FOREIGN KEY (pedido_id) REFERENCES pedidos (id)
        ON UPDATE CASCADE
        ON DELETE SET NULL,
    CONSTRAINT ck_salon_mesas_estado CHECK (estado IN ('disponible', 'ocupada', 'cuenta')),
    CONSTRAINT ck_salon_mesas_numero CHECK (numero BETWEEN 1 AND 10),
    CONSTRAINT ck_salon_mesas_capacidad CHECK (capacidad > 0)
);

CREATE INDEX IF NOT EXISTS idx_salon_mesas_estado ON salon_mesas (estado);
CREATE INDEX IF NOT EXISTS idx_salon_mesas_pedido ON salon_mesas (pedido_id);

INSERT INTO salon_mesas (numero, capacidad, posicion_x, posicion_y, zona, forma)
SELECT 1, 2, 1, 1, 'ventana', 'redonda'
WHERE NOT EXISTS (SELECT 1 FROM salon_mesas WHERE numero = 1);

INSERT INTO salon_mesas (numero, capacidad, posicion_x, posicion_y, zona, forma)
SELECT 2, 2, 2, 1, 'ventana', 'redonda'
WHERE NOT EXISTS (SELECT 1 FROM salon_mesas WHERE numero = 2);

INSERT INTO salon_mesas (numero, capacidad, posicion_x, posicion_y, zona, forma)
SELECT 3, 4, 3, 1, 'salon', 'cuadrada'
WHERE NOT EXISTS (SELECT 1 FROM salon_mesas WHERE numero = 3);

INSERT INTO salon_mesas (numero, capacidad, posicion_x, posicion_y, zona, forma)
SELECT 4, 4, 4, 1, 'salon', 'cuadrada'
WHERE NOT EXISTS (SELECT 1 FROM salon_mesas WHERE numero = 4);

INSERT INTO salon_mesas (numero, capacidad, posicion_x, posicion_y, zona, forma)
SELECT 5, 6, 2, 2, 'salon', 'rectangular'
WHERE NOT EXISTS (SELECT 1 FROM salon_mesas WHERE numero = 5);

INSERT INTO salon_mesas (numero, capacidad, posicion_x, posicion_y, zona, forma)
SELECT 6, 4, 3, 2, 'salon', 'cuadrada'
WHERE NOT EXISTS (SELECT 1 FROM salon_mesas WHERE numero = 6);

INSERT INTO salon_mesas (numero, capacidad, posicion_x, posicion_y, zona, forma)
SELECT 7, 4, 4, 2, 'salon', 'cuadrada'
WHERE NOT EXISTS (SELECT 1 FROM salon_mesas WHERE numero = 7);

INSERT INTO salon_mesas (numero, capacidad, posicion_x, posicion_y, zona, forma)
SELECT 8, 2, 1, 3, 'terraza', 'redonda'
WHERE NOT EXISTS (SELECT 1 FROM salon_mesas WHERE numero = 8);

INSERT INTO salon_mesas (numero, capacidad, posicion_x, posicion_y, zona, forma)
SELECT 9, 2, 2, 3, 'terraza', 'redonda'
WHERE NOT EXISTS (SELECT 1 FROM salon_mesas WHERE numero = 9);

INSERT INTO salon_mesas (numero, capacidad, posicion_x, posicion_y, zona, forma)
SELECT 10, 4, 3, 3, 'terraza', 'cuadrada'
WHERE NOT EXISTS (SELECT 1 FROM salon_mesas WHERE numero = 10);

COMMENT ON TABLE salon_mesas IS 'Estado visual de mesas del restaurante para el plano del salon';
