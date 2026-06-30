export type EstadoMesaSalon = 'disponible' | 'ocupada' | 'cuenta' | 'reservada' | 'bloqueada';

export type SalonMesa = {
  numero: number;
  capacidad: number;
  posicionX: number;
  posicionY: number;
  zona: string;
  forma: string;
  estado: EstadoMesaSalon;
  pedidoId?: number;
  pedidoCliente?: string;
  pedidoEstado?: string;
  pedidoTotal?: number;
  pedidoResumen?: string;
  pedidoFecha?: string;
  actualizadoEn?: string;
};

export type SalonResumen = {
  total: number;
  disponibles: number;
  ocupadas: number;
  cuenta: number;
  reservadas: number;
  bloqueadas: number;
};
