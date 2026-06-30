import { Pedido } from './pedido.model';

export type PosCheckoutLine = {
  productoId: number;
  cantidad: number;
};

export type PosCheckoutPayload = {
  cliente?: string;
  metodoPago: string;
  lineas: PosCheckoutLine[];
};

export type PosCheckoutResult = {
  pedidoId: number;
  pedido: Pedido;
  ventaIds: number[];
  total: number;
  insumosDescontados: number;
  productosSinReceta: string[];
};

export type PosCartItem = {
  productoId: number;
  nombre: string;
  categoria: string;
  precio: number;
  cantidad: number;
  imagenUrl?: string;
};
