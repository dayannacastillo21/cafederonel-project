# Cafedronel Project - Fases empresariales

## Fase 4 - Catalogo, inventario y Kardex

Estado: implementada.

- Migracion `V11__catalogo_kardex_empresarial.sql`.
- Productos con SKU, codigo de barras, costo, margen, imagen y unidad de venta.
- Inventario con codigo de insumo, categoria, ubicacion, lote y vencimiento.
- Tabla `movimientos_inventario` para Kardex.
- Tabla `producto_receta` para preparar descuento automatico de insumos por producto.
- Tabla `lotes_inventario` para control de lotes y vencimientos.
- Endpoints Kardex:
  - `GET /api/inventario/movimientos`
  - `GET /api/inventario/{id}/movimientos`
  - `POST /api/inventario/{id}/movimientos`
- Angular:
  - Productos muestra SKU, codigo de barras, margen, detalle y buscador.
  - Inventario muestra codigo, categoria, ubicacion, Kardex y registro de movimientos.

## Fase 5 - CRUD real de productos e inventario

Pendiente.

- Modal o pagina de nuevo producto.
- Modal o pagina de editar producto.
- Activar/desactivar producto.
- Nuevo insumo.
- Editar insumo.
- Eliminar/desactivar insumo.
- Validaciones visuales en Angular.

## Fase 6 - Ventas empresariales

Pendiente.

- Venta con varios productos.
- Tabla `venta_detalle`.
- Busqueda por SKU/codigo de barras.
- Ticket o comprobante.
- Descuento automatico usando `producto_receta`.
- Anulacion/reembolso con devolucion de stock si aplica.

## Fase 7 - Pedidos completos

Pendiente.

- Formulario de pedido con varias lineas.
- Detalle de pedido visible.
- Cambio de estado desde Angular.
- Filtros por cliente, estado y fecha.

## Fase 8 - Roles, reportes y tablero

Pendiente.

- Rutas protegidas por rol en Angular.
- Botones visibles segun rol.
- Reportes de ventas, inventario y Kardex.
- Dashboard con graficos reales.
- Exportacion CSV/PDF.
