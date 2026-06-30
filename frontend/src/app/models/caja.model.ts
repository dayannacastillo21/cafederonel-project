export type CajaSesion = {
  id: number;
  usuarioId: number;
  usuarioNombre: string;
  montoInicial: number;
  totalVentas: number;
  totalEfectivo: number;
  efectivoEnCaja: number;
  cantidadPedidos: number;
  estado: 'abierta' | 'cerrada';
  fechaApertura: string;
  fechaCierre?: string;
  montoCierre?: number;
  observaciones?: string;
};

export type CajaAperturaPayload = {
  montoInicial: number;
};

export type CajaCierrePayload = {
  montoCierre: number;
  observaciones?: string;
};
