import { Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { AuthService } from '../../../../core/auth/auth.service';
import { PageHeader } from '../../../../shared/components/page-header/page-header';
import { CafederonelApiService } from '../../../../services/cafederonel-api.service';
import { Venta } from '../../../../models/venta.model';

type VentaRow = {
  id: number;
  cajero: string;
  producto: string;
  cantidad: number;
  total: number;
  estado: Venta['estado'];
  metodoPago: string;
  fechaVenta: string;
};

@Component({
  selector: 'app-ventas-page',
  imports: [PageHeader],
  templateUrl: './ventas-page.html',
  styleUrl: './ventas-page.scss',
})
export class VentasPage {
  private readonly api = inject(CafederonelApiService);
  private readonly auth = inject(AuthService);

  protected readonly columns = ['Venta', 'Cajero', 'Producto', 'Metodo', 'Estado', 'Cantidad', 'Total', 'Fecha'];
  protected readonly rows = signal<VentaRow[]>([]);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal('');
  protected readonly headerEyebrow = computed(() => {
    const role = this.auth.session()?.role;
    return role === 'CONTADOR' ? 'Contabilidad' : 'Consulta';
  });
  protected readonly totalCobrado = computed(() =>
    this.rows()
      .filter((row) => row.estado === 'completado')
      .reduce((sum, row) => sum + row.total, 0),
  );
  protected readonly completedCount = computed(() => this.rows().filter((row) => row.estado === 'completado').length);

  constructor() {
    this.api
      .ventas()
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: (ventas) => {
          this.rows.set(this.toRows(ventas));
          this.loading.set(false);
        },
        error: () => {
          this.errorMessage.set('No se pudieron cargar ventas desde la base de datos.');
          this.loading.set(false);
        },
      });
  }

  protected formatMoney(value: number): string {
    return new Intl.NumberFormat('es-PE', {
      style: 'currency',
      currency: 'PEN',
      minimumFractionDigits: 2,
    }).format(value);
  }

  protected formatDate(value?: string): string {
    if (!value) {
      return 'Sin fecha';
    }
    const parsed = new Date(value.replace(' ', 'T'));
    return Number.isNaN(parsed.getTime())
      ? value
      : new Intl.DateTimeFormat('es-PE', {
          day: '2-digit',
          month: '2-digit',
          year: 'numeric',
          hour: '2-digit',
          minute: '2-digit',
        }).format(parsed);
  }

  protected paymentLabel(value?: string): string {
    if (!value) {
      return 'Sin metodo';
    }
    return value
      .split(/[\s_-]+/)
      .filter(Boolean)
      .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
      .join(' ');
  }

  private toRows(ventas: Venta[]): VentaRow[] {
    return ventas.map((venta) => ({
      id: venta.id,
      cajero: venta.usuarioNombre?.trim() || `Usuario ${venta.usuarioId}`,
      producto: venta.producto.nombre,
      cantidad: venta.cantidad,
      total: venta.total,
      estado: venta.estado,
      metodoPago: venta.metodoPago ?? 'sin metodo',
      fechaVenta: venta.fechaVenta,
    }));
  }
}
