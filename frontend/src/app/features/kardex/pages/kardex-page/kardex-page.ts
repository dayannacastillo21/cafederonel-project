import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { catchError, finalize, of, timeout } from 'rxjs';

import { AuthService } from '../../../../core/auth/auth.service';
import { canExportInventarioReports } from '../../../../core/auth/export-access.util';
import { downloadStyledExcel, formatExcelGeneratedAt, timestampForFilename } from '../../../../core/utils/excel-export.util';
import { MovimientoInventario } from '../../../../models/inventario.model';
import { CafederonelApiService } from '../../../../services/cafederonel-api.service';
import { CatalogCacheService } from '../../../../services/catalog-cache.service';
import { PageHeader } from '../../../../shared/components/page-header/page-header';

@Component({
  selector: 'app-kardex-page',
  imports: [PageHeader],
  templateUrl: './kardex-page.html',
  styleUrl: './kardex-page.scss',
})
export class KardexPage implements OnInit {
  private readonly api = inject(CafederonelApiService);
  private readonly cache = inject(CatalogCacheService);
  private readonly auth = inject(AuthService);
  private loadVersion = 0;

  protected readonly canExportExcel = computed(() =>
    canExportInventarioReports(this.auth.session()?.role),
  );

  protected readonly columns = ['Fecha', 'Insumo', 'Almacen', 'Tipo', 'Cantidad', 'Antes', 'Nuevo', 'Referencia'];
  protected readonly movimientos = signal<MovimientoInventario[]>([]);
  protected readonly inventario = this.cache.inventario;
  protected readonly almacenesCatalogo = this.cache.almacenes;
  protected readonly searchTerm = signal('');
  protected readonly selectedType = signal('todos');
  protected readonly selectedWarehouse = signal('todos');
  protected readonly movimientosLoading = signal(true);
  protected readonly errorMessage = signal('');
  protected readonly actionMessage = signal('');

  protected readonly loading = computed(
    () => this.movimientosLoading() && this.movimientos().length === 0,
  );

  protected readonly filteredMovements = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();
    const type = this.selectedType();
    const warehouse = this.selectedWarehouse();

    return this.movimientos().filter((movement) => {
      const matchTerm =
        !term ||
        [
          movement.insumo,
          movement.tipo,
          movement.motivo,
          movement.referencia,
          movement.almacen,
        ]
          .filter(Boolean)
          .some((value) => String(value).toLowerCase().includes(term));

      const matchType = type === 'todos' || movement.tipo === type;
      const matchWarehouse = warehouse === 'todos' || String(movement.almacenId) === warehouse;
      return matchTerm && matchType && matchWarehouse;
    });
  });

  protected readonly totalEntradas = computed(() =>
    this.filteredMovements()
      .filter((movement) => movement.tipo === 'entrada')
      .reduce((sum, movement) => sum + movement.cantidad, 0),
  );

  protected readonly totalSalidas = computed(() =>
    this.filteredMovements()
      .filter((movement) => movement.tipo === 'salida' || movement.tipo === 'merma')
      .reduce((sum, movement) => sum + movement.cantidad, 0),
  );

  protected readonly stockValorizado = computed(() =>
    this.inventario().reduce((sum, item) => sum + item.cantidad * item.precioUnitario, 0),
  );

  protected readonly warehouseLabel = computed(() =>
    this.almacenesCatalogo()
      .map((almacen) => almacen.nombre)
      .join(' · ') || 'Almacenes registrados',
  );

  ngOnInit(): void {
    this.cache.ensureInventario();
    this.loadMovimientos();
  }

  private loadMovimientos(): void {
    const loadVersion = ++this.loadVersion;

    this.movimientosLoading.set(true);
    this.errorMessage.set('');

    this.api
      .ultimosMovimientosInventario()
      .pipe(
        timeout(15000),
        catchError(() => {
          if (loadVersion === this.loadVersion) {
            this.errorMessage.set('No se pudo cargar el Kardex desde el backend.');
          }
          return of([] as MovimientoInventario[]);
        }),
        finalize(() => {
          if (loadVersion === this.loadVersion) {
            this.movimientosLoading.set(false);
          }
        }),
      )
      .subscribe((movimientos) => {
        if (loadVersion !== this.loadVersion) {
          return;
        }

        this.movimientos.set(movimientos);
      });
  }

  protected updateSearch(event: Event): void {
    this.searchTerm.set((event.target as HTMLInputElement).value);
  }

  protected updateType(event: Event): void {
    this.selectedType.set((event.target as HTMLSelectElement).value);
  }

  protected updateWarehouse(event: Event): void {
    this.selectedWarehouse.set((event.target as HTMLSelectElement).value);
  }

  protected clearFilters(): void {
    this.searchTerm.set('');
    this.selectedType.set('todos');
    this.selectedWarehouse.set('todos');
  }

  protected formatDate(value: string): string {
    if (!value) {
      return '—';
    }

    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? value : date.toLocaleString('es-PE');
  }

  protected formatMoney(value: number): string {
    return new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN' }).format(value);
  }

  protected exportKardexExcel(): void {
    if (!this.canExportExcel()) {
      return;
    }

    void this.runKardexExcelExport();
  }

  protected tipoLabel(tipo: MovimientoInventario['tipo']): string {
    const labels: Record<MovimientoInventario['tipo'], string> = {
      entrada: 'Entrada',
      salida: 'Salida',
      ajuste: 'Ajuste',
      merma: 'Merma',
    };
    return labels[tipo] ?? tipo;
  }

  private async runKardexExcelExport(): Promise<void> {
    const data = this.filteredMovements();

    try {
      await downloadStyledExcel({
        filename: `kardex_cafedronel_${timestampForFilename()}.xlsx`,
        sheetName: 'Kardex',
        title: 'Cafedronel · Kardex de inventario',
        meta: [
          { label: 'Generado', value: formatExcelGeneratedAt() },
          { label: 'Movimientos', value: String(data.length) },
          { label: 'Entradas', value: String(this.totalEntradas()) },
          { label: 'Salidas y mermas', value: String(this.totalSalidas()) },
          { label: 'Filtros', value: this.buildKardexFilterSummary() },
        ],
        headers: [
          'Fecha',
          'Insumo',
          'Motivo',
          'Almacen',
          'Tipo',
          'Cantidad',
          'Stock anterior',
          'Stock nuevo',
          'Costo unitario',
          'Referencia',
          'Usuario',
        ],
        columnWidths: [18, 26, 22, 14, 12, 10, 12, 12, 14, 18, 16],
        moneyColumnIndexes: [9],
        integerColumnIndexes: [6, 7, 8],
        rows: data.map((movement) => [
          this.formatDate(movement.fechaMovimiento),
          movement.insumo,
          movement.motivo || 'Sin motivo',
          movement.almacen || '—',
          this.tipoLabel(movement.tipo),
          movement.cantidad,
          movement.stockAnterior,
          movement.stockNuevo,
          Number(movement.costoUnitario ?? 0),
          movement.referencia || 'Sin referencia',
          movement.usuario || '—',
        ]),
        highlight: (row, columnIndex) => {
          if (columnIndex !== 4) {
            return null;
          }
          const tipo = String(row[4] ?? '').toLowerCase();
          if (tipo === 'entrada') return 'ok';
          if (tipo === 'salida' || tipo === 'ajuste') return 'warning';
          if (tipo === 'merma') return 'danger';
          return null;
        },
      });

      this.actionMessage.set(`Excel descargado con ${data.length} movimiento(s) segun los filtros actuales.`);
    } catch {
      this.actionMessage.set('No se pudo generar el archivo Excel.');
    }
  }

  private buildKardexFilterSummary(): string {
    const parts: string[] = [];
    const term = this.searchTerm().trim();
    if (term) {
      parts.push(`Busqueda: "${term}"`);
    }
    if (this.selectedType() !== 'todos') {
      parts.push(`Tipo: ${this.tipoLabel(this.selectedType() as MovimientoInventario['tipo'])}`);
    }
    if (this.selectedWarehouse() !== 'todos') {
      const warehouse = this.almacenesCatalogo().find(
        (item) => String(item.id) === this.selectedWarehouse(),
      );
      parts.push(`Almacen: ${warehouse?.nombre ?? this.selectedWarehouse()}`);
    }
    return parts.length ? parts.join(' | ') : 'Todos (sin filtros)';
  }
}
