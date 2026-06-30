import { DecimalPipe } from '@angular/common';
import { Component, input, output, signal, ViewEncapsulation } from '@angular/core';

import { EstadoMesaSalon, SalonMesa } from '../../../../models/salon.model';
import { estadoLabel } from '../../utils/salon-estado.utils';
import {
  CHAIR_OFFSETS,
  computeSpot,
  CORNER_TREES,
  ESTADO_COLORS,
  FLOOR_VIEWBOX,
  getRowDividers,
  getSalonZone,
  GRID_LAYOUT,
  MesaSpot,
} from './salon-floor.constants';

@Component({
  selector: 'app-salon-floor-map',
  imports: [DecimalPipe],
  templateUrl: './salon-floor-map.component.html',
  styleUrl: './salon-floor-map.component.scss',
  encapsulation: ViewEncapsulation.None,
})
export class SalonFloorMapComponent {
  readonly mesas = input.required<SalonMesa[]>();
  readonly selectedNumero = input<number | null>(null);
  readonly filterEstado = input<EstadoMesaSalon | 'todos'>('todos');

  readonly mesaSelected = output<SalonMesa>();

  protected readonly viewW = FLOOR_VIEWBOX.w;
  protected readonly viewH = FLOOR_VIEWBOX.h;
  protected readonly zoom = signal(1);
  protected readonly barStools = [556, 606, 656];
  protected readonly decorTrees = CORNER_TREES;
  protected readonly salonZone = getSalonZone();
  protected readonly rowDividers = getRowDividers();
  protected readonly plankV = Array.from({ length: 24 }, (_, i) => i);
  protected readonly plankH = Array.from({ length: 14 }, (_, i) => i);

  protected zoomIn(): void {
    this.zoom.update((v) => Math.min(2, +(v + 0.12).toFixed(2)));
  }

  protected zoomOut(): void {
    this.zoom.update((v) => Math.max(0.72, +(v - 0.12).toFixed(2)));
  }

  protected resetZoom(): void {
    this.zoom.set(1);
  }

  protected spot(mesa: SalonMesa): MesaSpot {
    return computeSpot(mesa);
  }

  protected mesaCenter(mesa: SalonMesa): { cx: number; cy: number } {
    const s = this.spot(mesa);
    return { cx: s.x + s.w / 2, cy: s.y + s.h / 2 };
  }

  protected hitRadius(mesa: SalonMesa): number {
    const s = this.spot(mesa);
    return Math.max(s.w, s.h) / 2 + 42;
  }

  protected statusCard(mesa: SalonMesa): { x: number; y: number; w: number; h: number } {
    const s = this.spot(mesa);
    const cx = s.x + s.w / 2;
    const w = 92;
    return { x: cx - w / 2, y: s.y - GRID_LAYOUT.cardAbove, w, h: 48 };
  }

  protected chairPositions(mesa: SalonMesa): { x: number; y: number }[] {
    const s = this.spot(mesa);
    const count = Math.min(mesa.capacidad, s.forma === 'communal' ? 6 : 4);
    const offsets = CHAIR_OFFSETS[count] ?? CHAIR_OFFSETS[2];
    const cx = s.x + s.w / 2;
    const cy = s.y + s.h / 2;
    const rx = s.w / 2 + 16;
    const ry = s.h / 2 + 14;
    return offsets
      .filter(({ dy }) => dy >= 0)
      .map(({ dx, dy }) => ({ x: cx + dx * rx, y: cy + dy * ry }));
  }

  protected tableRx(mesa: SalonMesa): number {
    const s = this.spot(mesa);
    if (s.forma === 'round') return s.w / 2;
    if (s.forma === 'communal') return 10;
    return s.forma === 'rect' ? 8 : 10;
  }

  protected estadoColors(estado: EstadoMesaSalon) {
    return ESTADO_COLORS[estado] ?? ESTADO_COLORS.disponible;
  }

  protected tableFill(mesa: SalonMesa): string {
    if (mesa.estado === 'ocupada') return '#c96a58';
    if (mesa.estado === 'bloqueada') return '#a8a29e';
    if (this.isSelected(mesa)) return '#ecfdf5';
    return 'url(#woodTable)';
  }

  protected isDimmed(mesa: SalonMesa): boolean {
    const f = this.filterEstado();
    return f !== 'todos' && mesa.estado !== f;
  }

  protected isSelected(mesa: SalonMesa): boolean {
    return this.selectedNumero() === mesa.numero;
  }

  protected mesaOpacity(mesa: SalonMesa): number {
    return this.isDimmed(mesa) ? 0.35 : 1;
  }

  protected estadoLabel(estado: EstadoMesaSalon): string {
    return estadoLabel(estado);
  }

  protected estadoShort(estado: EstadoMesaSalon): string {
    const map: Record<EstadoMesaSalon, string> = {
      disponible: 'Libre',
      reservada: 'Reserva',
      ocupada: 'Ocupada',
      cuenta: 'Cuenta',
      bloqueada: 'Bloq.',
    };
    return map[estado] ?? estado;
  }

  protected mesaAriaLabel(mesa: SalonMesa): string {
    return [`Mesa ${mesa.numero}`, this.estadoLabel(mesa.estado), `${mesa.capacidad} personas`].join(', ');
  }

  protected formatMoney(value?: number): string {
    return new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN', maximumFractionDigits: 0 }).format(
      value ?? 0,
    );
  }

  protected onSelect(mesa: SalonMesa, event?: Event): void {
    event?.stopPropagation();
    if (mesa.estado === 'bloqueada') return;
    this.mesaSelected.emit(mesa);
  }
}
