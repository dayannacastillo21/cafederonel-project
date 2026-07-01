import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { CafederonelApiService } from '../../../../services/cafederonel-api.service';
import { ResumenFinanciero } from '../../../../models/reporte.model';
import { PageHeader } from '../../../../shared/components/page-header/page-header';

@Component({
  selector: 'app-reportes-page',
  imports: [PageHeader],
  templateUrl: './reportes-page.html',
  styleUrl: './reportes-page.scss',
})
export class ReportesPage {
  private readonly api = inject(CafederonelApiService);

  protected readonly loading = signal(true);
  protected readonly errorMessage = signal('');
  protected readonly resumen = signal<ResumenFinanciero | null>(null);

  constructor() {
    this.api
      .resumenFinanciero()
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: (resumen) => {
          this.resumen.set(resumen);
          this.loading.set(false);
        },
        error: () => {
          this.errorMessage.set('No se pudo cargar el resumen financiero.');
          this.loading.set(false);
        },
      });
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
}
