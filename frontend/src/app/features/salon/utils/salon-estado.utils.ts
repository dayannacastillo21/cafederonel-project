import { EstadoMesaSalon } from '../../../models/salon.model';

export const ESTADO_LABELS: Record<EstadoMesaSalon, string> = {
  disponible: 'Disponible',
  reservada: 'Reservada',
  ocupada: 'Ocupada',
  cuenta: 'Pide cuenta',
  bloqueada: 'Bloqueada',
};

export function estadoLabel(estado: EstadoMesaSalon): string {
  return ESTADO_LABELS[estado] ?? estado;
}

export function estadoCssClass(estado: EstadoMesaSalon): string {
  return estado;
}
