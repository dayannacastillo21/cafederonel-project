-- Eliminar pedidos de demostracion insertados en V8 (Mesa 04, Delivery 128, Mesa 09, Mostrador).
-- detalle_pedido se elimina en cascada por FK ON DELETE CASCADE.

DELETE FROM pedidos
WHERE cliente IN ('Mesa 04', 'Delivery 128', 'Mesa 09', 'Mostrador')
  AND fecha_creacion IN (
      TIMESTAMP '2026-06-26 09:15:00',
      TIMESTAMP '2026-06-26 09:22:00',
      TIMESTAMP '2026-06-26 09:35:00',
      TIMESTAMP '2026-06-26 09:48:00'
  );
