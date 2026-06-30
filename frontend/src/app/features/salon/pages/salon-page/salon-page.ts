import { Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { interval } from 'rxjs';

import { PageHeader } from '../../../../shared/components/page-header/page-header';
import { CafederonelApiService } from '../../../../services/cafederonel-api.service';
import { EstadoMesaSalon, SalonMesa, SalonResumen } from '../../../../models/salon.model';
import { SalonFloorMapComponent } from '../../components/salon-floor-map/salon-floor-map.component';
import { estadoLabel } from '../../utils/salon-estado.utils';

type EstadoFilter = 'todos' | EstadoMesaSalon;

@Component({
  selector: 'app-salon-page',
  imports: [PageHeader, RouterLink, SalonFloorMapComponent],
  templateUrl: './salon-page.html',
  styleUrl: './salon-page.scss',
})
export class SalonPage {
  private readonly api = inject(CafederonelApiService);

  protected readonly mesas = signal<SalonMesa[]>([]);
  protected readonly selectedMesa = signal<SalonMesa | null>(null);
  protected readonly estadoFilter = signal<EstadoFilter>('todos');
  protected readonly loading = signal(true);
  protected readonly acting = signal(false);
  protected readonly errorMessage = signal('');
  protected readonly actionMessage = signal('');
  protected readonly lastSync = signal<Date | null>(null);

  protected readonly resumen = computed<SalonResumen>(() => {
    const items = this.mesas();
    return {
      total: items.length,
      disponibles: items.filter((m) => m.estado === 'disponible').length,
      ocupadas: items.filter((m) => m.estado === 'ocupada').length,
      cuenta: items.filter((m) => m.estado === 'cuenta').length,
      reservadas: items.filter((m) => m.estado === 'reservada').length,
      bloqueadas: items.filter((m) => m.estado === 'bloqueada').length,
    };
  });

  protected readonly mesasDimmed = computed(() => this.estadoFilter() !== 'todos');

  protected readonly lastSyncLabel = computed(() => {
    const sync = this.lastSync();
    if (!sync) {
      return 'Sincronizando...';
    }
    return new Intl.DateTimeFormat('es-PE', {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
    }).format(sync);
  });

  protected readonly ocupacionPct = computed(() => {
    const { total, disponibles, reservadas } = this.resumen();
    if (!total) {
      return 0;
    }
    return Math.round(((total - disponibles - reservadas) / total) * 100);
  });

  constructor() {
    this.loadMesas();
    interval(12_000)
      .pipe(takeUntilDestroyed())
      .subscribe(() => this.loadMesas(false));
  }

  protected estadoLabel = estadoLabel;

  protected estadoFilterLabel(): string {
    const filter = this.estadoFilter();
    return filter === 'todos' ? 'Todas' : estadoLabel(filter);
  }

  protected filterByEstado(estado: EstadoFilter): void {
    this.estadoFilter.update((current) => (current === estado ? 'todos' : estado));
  }

  protected isFilterActive(estado: EstadoFilter): boolean {
    return this.estadoFilter() === estado;
  }

  protected zonaLabel(zona: string): string {
    const labels: Record<string, string> = {
      ventana: 'Zona ventana',
      salon: 'Salón de la cafetería',
      terraza: 'Terraza',
    };
    return labels[zona] ?? zona;
  }

  protected formatMoney(value?: number): string {
    return new Intl.NumberFormat('es-PE', {
      style: 'currency',
      currency: 'PEN',
      minimumFractionDigits: 2,
    }).format(value ?? 0);
  }

  protected formatDate(value?: string): string {
    if (!value) {
      return 'Sin hora';
    }
    return new Intl.DateTimeFormat('es-PE', {
      hour: '2-digit',
      minute: '2-digit',
    }).format(new Date(value.replace(' ', 'T')));
  }

  protected tiempoEnMesa(value?: string): string {
    if (!value) {
      return '—';
    }
    const start = new Date(value.replace(' ', 'T')).getTime();
    const mins = Math.max(0, Math.floor((Date.now() - start) / 60_000));
    if (mins < 60) {
      return `${mins} min`;
    }
    const h = Math.floor(mins / 60);
    const m = mins % 60;
    return m ? `${h}h ${m}m` : `${h}h`;
  }

  protected onMesaSelected(mesa: SalonMesa): void {
    this.selectedMesa.set(mesa);
    this.actionMessage.set('');
    this.errorMessage.set('');
  }

  protected closePanel(): void {
    this.selectedMesa.set(null);
  }

  protected posLink(): string[] {
    return ['/ventas'];
  }

  protected posQuery(mesa: SalonMesa): { mesa: number } {
    return { mesa: mesa.numero };
  }

  protected canTakeOrder(mesa: SalonMesa): boolean {
    return mesa.estado === 'disponible' || mesa.estado === 'reservada';
  }

  protected marcarCuenta(mesa: SalonMesa): void {
    if (this.acting()) {
      return;
    }
    this.acting.set(true);
    this.api.salonMarcarCuenta(mesa.numero).subscribe({
      next: (actualizada) => {
        this.updateMesa(actualizada);
        this.selectedMesa.set(actualizada);
        this.acting.set(false);
        this.actionMessage.set(`Mesa ${mesa.numero} marcada como pide cuenta.`);
      },
      error: () => {
        this.acting.set(false);
        this.errorMessage.set('No se pudo marcar la cuenta de la mesa.');
      },
    });
  }

  protected liberarMesa(mesa: SalonMesa): void {
    if (this.acting() || mesa.estado === 'bloqueada') {
      return;
    }
    const ok = window.confirm(`¿Liberar mesa ${mesa.numero}? El cliente ya se retiro.`);
    if (!ok) {
      return;
    }

    this.acting.set(true);
    this.api.salonLiberarMesa(mesa.numero).subscribe({
      next: (actualizada) => {
        this.updateMesa(actualizada);
        this.selectedMesa.set(actualizada);
        this.acting.set(false);
        this.actionMessage.set(`Mesa ${mesa.numero} disponible.`);
      },
      error: () => {
        this.acting.set(false);
        this.errorMessage.set('No se pudo liberar la mesa.');
      },
    });
  }

  private loadMesas(showLoading = true): void {
    if (showLoading) {
      this.loading.set(true);
    }
    this.api.salonMesas().subscribe({
      next: (mesas) => {
        this.mesas.set(mesas);
        this.lastSync.set(new Date());
        const selected = this.selectedMesa();
        if (selected) {
          const refreshed = mesas.find((mesa) => mesa.numero === selected.numero) ?? null;
          this.selectedMesa.set(refreshed);
        }
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('No se pudo cargar el mapa de la cafetería.');
        this.loading.set(false);
      },
    });
  }

  private updateMesa(actualizada: SalonMesa): void {
    this.mesas.update((items) => items.map((mesa) => (mesa.numero === actualizada.numero ? actualizada : mesa)));
  }
}
