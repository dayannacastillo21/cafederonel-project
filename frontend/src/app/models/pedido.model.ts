import { Producto } from './producto.model';

export type EstadoPedido = 'pendiente' | 'en_proceso' | 'completado' | 'cancelado';

export type DetallePedido = {
  id: number;
  cantidad: number;
  precio: number;
  subtotal: number;
  pedidoId: number;
  producto: Producto;
};

export type Pedido = {
  id: number;
  cliente: string;
  estado: EstadoPedido;
  total: number;
  fechaCreacion?: string;
  detalles: DetallePedido[];
};

export type PedidoUpdatePayload = {
  cliente: string;
  detalles: { productoId: number; cantidad: number }[];
};

export type PedidoAdminUpdatePayload = PedidoUpdatePayload & {
  adminPin: string;
};

export type PedidoAdminPinPayload = {
  adminPin: string;
};
