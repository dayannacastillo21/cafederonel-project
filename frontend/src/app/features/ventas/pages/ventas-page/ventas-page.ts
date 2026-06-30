import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { forkJoin } from 'rxjs';

import { PageHeader } from '../../../../shared/components/page-header/page-header';
import { CafederonelApiService } from '../../../../services/cafederonel-api.service';
import { Usuario } from '../../../../models/usuario.model';
import { Venta } from '../../../../models/venta.model';

type VentaRow = {
  id: number;
  usuario: string;
  producto: string;
  cantidad: number;
  total: number;
};

@Component({
  selector: 'app-ventas-page',
  imports: [PageHeader],
  templateUrl: './ventas-page.html',
  styleUrl: './ventas-page.scss',
})
export class VentasPage {
  private readonly api = inject(CafederonelApiService);

  protected readonly columns = ['Usuario', 'Producto', 'Cantidad', 'Total'];
  protected readonly rows = signal<VentaRow[]>([]);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal('');

  constructor() {
    forkJoin({
      ventas: this.api.ventas(),
      usuarios: this.api.usuarios(),
    })
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: ({ ventas, usuarios }) => {
          this.rows.set(this.toRows(ventas, usuarios));
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

  private toRows(ventas: Venta[], usuarios: Usuario[]): VentaRow[] {
    const usuariosPorId = new Map(usuarios.map((usuario) => [usuario.id, usuario.nombre]));

    return ventas.map((venta) => ({
      id: venta.id,
      usuario: usuariosPorId.get(venta.usuarioId) ?? `Usuario ${venta.usuarioId}`,
      producto: venta.producto.nombre,
      cantidad: venta.cantidad,
      total: venta.total,
    }));
  }
}
