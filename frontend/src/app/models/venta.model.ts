import { Producto } from './producto.model';

export type Venta = {
  id: number;
  usuarioId: number;
  usuarioNombre?: string;
  fechaVenta: string;
  total: number;
  estado: 'pendiente' | 'completado' | 'cancelado' | 'reembolsado';
  metodoPago?: string;
  cantidad: number;
  precioUnitario: number;
  producto: Producto;
};
