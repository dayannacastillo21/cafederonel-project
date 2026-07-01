import { CajaSesion } from './caja.model';

export type MetodoPagoResumen = {
  metodoPago: string;
  cantidad: number;
  total: number;
};

export type CobroResumen = {
  ventaId: number;
  producto: string;
  usuarioId: number;
  usuarioNombre?: string;
  cantidad: number;
  metodoPago: string;
  estado: string;
  total: number;
  fechaVenta?: string;
};

export type ResumenFinanciero = {
  totalCobrado: number;
  totalRegistrado: number;
  ticketPromedio: number;
  ventasRegistradas: number;
  ventasCompletadas: number;
  pedidosRegistrados: number;
  pedidosPendientes: number;
  pedidosCompletados: number;
  pedidosCancelados: number;
  cajasAbiertas: number;
  cajasCerradas: number;
  totalVentasCaja: number;
  totalEfectivoCaja: number;
  cobrosPorMetodo: MetodoPagoResumen[];
  sesionesCaja: CajaSesion[];
  ultimosCobros: CobroResumen[];
};
