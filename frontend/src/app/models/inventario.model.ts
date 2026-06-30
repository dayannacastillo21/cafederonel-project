export type InventarioItem = {
  id: number;
  nombreInsumo: string;
  codigoInsumo: string;
  categoria: string;
  ubicacion: string;
  almacenId: number;
  almacen: string;
  lote?: string;
  fechaVencimiento?: string;
  cantidad: number;
  unidad: string;
  stockMinimo: number;
  precioUnitario: number;
  proveedor: string;
  fechaActualizacion?: string;
  activo: boolean;
};

export type InventarioPayload = {
  nombreInsumo: string;
  codigoInsumo?: string;
  categoria?: string;
  ubicacion?: string;
  almacenId?: number;
  lote?: string;
  fechaVencimiento?: string;
  cantidad: number;
  unidad: string;
  stockMinimo: number;
  precioUnitario: number;
  proveedor: string;
  activo?: boolean;
};

export type MovimientoInventario = {
  id: number;
  inventarioId: number;
  insumo: string;
  tipo: 'entrada' | 'salida' | 'ajuste' | 'merma';
  cantidad: number;
  stockAnterior: number;
  stockNuevo: number;
  costoUnitario?: number;
  motivo?: string;
  referencia?: string;
  usuarioId?: number;
  usuario?: string;
  almacenId?: number;
  almacen?: string;
  fechaMovimiento: string;
};

export type MovimientoInventarioPayload = {
  tipo: MovimientoInventario['tipo'];
  cantidad: number;
  costoUnitario?: number;
  motivo?: string;
  referencia?: string;
  usuarioId?: number;
  almacenId?: number;
};
