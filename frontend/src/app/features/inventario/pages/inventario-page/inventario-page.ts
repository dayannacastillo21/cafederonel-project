import { Component, DestroyRef, OnInit, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { PageHeader } from '../../../../shared/components/page-header/page-header';
import { AuthService } from '../../../../core/auth/auth.service';
import { canExportInventarioReports } from '../../../../core/auth/export-access.util';
import { UnidadLabelPipe } from '../../../../core/pipes/unidad-label.pipe';
import { coerceActivo } from '../../../../core/utils/boolean.util';
import { downloadStyledExcel, formatExcelGeneratedAt, timestampForFilename } from '../../../../core/utils/excel-export.util';
import { unidadLabel } from '../../../../core/utils/unidad.util';
import { CafederonelApiService } from '../../../../services/cafederonel-api.service';
import { CatalogCacheService } from '../../../../services/catalog-cache.service';
import { InventarioItem, InventarioPayload, MovimientoInventario } from '../../../../models/inventario.model';
import { Almacen } from '../../../../models/catalogo.model';
import { Proveedor } from '../../../../models/proveedor.model';

type StockStatusFilter = 'todos' | 'Ok' | 'Bajo' | 'Critico';
type InventoryStatusFilter = 'todos' | 'activo' | 'inactivo';
type ExpiryFilter = 'todos' | 'vencido' | '30_dias' | '3_meses' | 'sin_fecha';
type InventoryModalMode = 'kardex' | 'edit' | 'create' | null;

@Component({
  selector: 'app-inventario-page',
  imports: [PageHeader, ReactiveFormsModule, UnidadLabelPipe],
  templateUrl: './inventario-page.html',
  styleUrl: './inventario-page.scss',
})
export class InventarioPage implements OnInit {
  private readonly cache = inject(CatalogCacheService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly fb = inject(FormBuilder);
  private readonly pageSize = 10;

  protected readonly unidadOptions = [
    { value: 'unid.', label: 'Unidades (vasos, servilletas, piezas)' },
    { value: 'paquetes', label: 'Paquetes' },
    { value: 'millares', label: 'Millares (x1000)' },
    { value: 'kg', label: 'Kilogramos' },
    { value: 'litros', label: 'Litros' },
    { value: 'latas', label: 'Latas' },
    { value: 'botellas', label: 'Botellas' },
  ] as const;
  protected readonly movementColumns = ['Fecha', 'Tipo', 'Cantidad', 'Antes', 'Nuevo', 'Motivo'];
  protected readonly rows = computed(() =>
    this.cache.inventario().map((item) => this.normalizeItem(item)),
  );
  protected readonly almacenesCatalogo = this.cache.almacenes;
  protected readonly proveedoresCatalogo = this.cache.proveedores;
  protected readonly categoriasInventarioCatalogo = this.cache.categoriasInventario;
  protected readonly loading = computed(
    () => this.cache.inventarioLoading() && this.cache.inventario().length === 0,
  );
  protected readonly errorMessage = this.cache.inventarioError;
  protected readonly searchTerm = signal('');
  protected readonly selectedCategory = signal('todos');
  protected readonly selectedStockStatus = signal<StockStatusFilter>('todos');
  protected readonly selectedExpiryFilter = signal<ExpiryFilter>('todos');
  protected readonly selectedStatus = signal<InventoryStatusFilter>('todos');
  protected readonly selectedWarehouse = signal('todos');
  protected readonly currentPage = signal(1);
  protected readonly selectedItem = signal<InventarioItem | null>(null);
  protected readonly modalMode = signal<InventoryModalMode>(null);
  protected readonly movimientos = signal<MovimientoInventario[]>([]);
  protected readonly savingItem = signal(false);
  protected readonly actionId = signal<number | null>(null);
  protected readonly movementLoading = signal(false);
  protected readonly movementSubmitting = signal(false);
  protected readonly actionMessage = signal('');
  protected readonly formMessage = signal('');
  protected readonly movementError = signal('');
  private readonly api = inject(CafederonelApiService);
  private readonly auth = inject(AuthService);

  protected readonly canExportExcel = computed(() =>
    canExportInventarioReports(this.auth.session()?.role),
  );

  protected readonly itemForm = this.fb.nonNullable.group({
    nombreInsumo: ['', [Validators.required, Validators.maxLength(150)]],
    codigoInsumo: ['', [Validators.maxLength(50)]],
    categoria: ['', [Validators.required, Validators.maxLength(80)]],
    ubicacion: ['', [Validators.maxLength(120)]],
    almacenId: [0, [Validators.required, Validators.min(1)]],
    lote: ['', [Validators.maxLength(80)]],
    fechaVencimiento: [''],
    cantidad: [0, [Validators.required, Validators.min(0)]],
    unidad: ['unid.', [Validators.required, Validators.maxLength(30)]],
    stockMinimo: [0, [Validators.required, Validators.min(0)]],
    precioUnitario: [0, [Validators.required, Validators.min(0)]],
    proveedor: ['', [Validators.required]],
    activo: [true],
  });

  protected readonly filteredRows = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();
    const category = this.selectedCategory();
    const stockStatus = this.selectedStockStatus();
    const expiryFilter = this.selectedExpiryFilter();
    const status = this.selectedStatus();
    const warehouse = this.selectedWarehouse();

    return this.rows().filter((row) => {
      const matchesTerm = term
        ? [
            row.codigoInsumo,
            row.nombreInsumo,
            row.categoria,
            row.proveedor,
            row.ubicacion,
            row.almacen,
            row.lote,
            row.unidad,
          ]
            .filter(Boolean)
            .some((value) => String(value).toLowerCase().includes(term))
        : true;

      return (
        matchesTerm &&
        (category === 'todos' || row.categoria === category) &&
        (stockStatus === 'todos' || this.estadoStock(row) === stockStatus) &&
        (expiryFilter === 'todos' || this.matchesExpiryFilter(row, expiryFilter)) &&
        (status === 'todos' || (status === 'activo' ? this.isActive(row) : !this.isActive(row))) &&
        (warehouse === 'todos' || String(row.almacenId) === warehouse)
      );
    });
  });

  protected readonly totalPages = computed(() => Math.max(1, Math.ceil(this.filteredRows().length / this.pageSize)));
  protected readonly displayPage = computed(() => Math.min(this.currentPage(), this.totalPages()));
  protected readonly pagedRows = computed(() => {
    const start = (this.displayPage() - 1) * this.pageSize;
    return this.filteredRows().slice(start, start + this.pageSize);
  });
  protected readonly pageStart = computed(() =>
    this.filteredRows().length ? (this.displayPage() - 1) * this.pageSize + 1 : 0,
  );
  protected readonly pageEnd = computed(() => Math.min(this.displayPage() * this.pageSize, this.filteredRows().length));
  protected readonly pageNumbers = computed(() => {
    const total = this.totalPages();
    const current = this.displayPage();
    const start = Math.max(1, Math.min(current - 2, total - 4));
    const end = Math.min(total, start + 4);
    return Array.from({ length: end - start + 1 }, (_, index) => start + index);
  });

  protected readonly categoryOptions = computed(() =>
    Array.from(
      new Set([
        ...this.categoriasInventarioCatalogo(),
        ...this.rows().map((row) => row.categoria).filter(Boolean),
      ]),
    ).sort((a, b) => a.localeCompare(b)),
  );

  protected readonly proveedoresActivos = computed(() =>
    this.proveedoresCatalogo().filter((proveedor) => proveedor.activo),
  );

  protected readonly stockAlerts = computed(() =>
    this.rows()
      .filter((item) => this.isActive(item) && this.estadoStock(item) !== 'Ok')
      .sort((a, b) => this.stockCoverage(a) - this.stockCoverage(b))
      .slice(0, 5),
  );

  protected readonly activeCount = computed(() => this.rows().filter((item) => this.isActive(item)).length);

  protected readonly criticalCount = computed(
    () => this.rows().filter((item) => this.isActive(item) && this.estadoStock(item) === 'Critico').length,
  );

  protected readonly lowStockCount = computed(
    () => this.rows().filter((item) => this.isActive(item) && this.estadoStock(item) !== 'Ok').length,
  );

  protected readonly expiring30Count = computed(
    () =>
      this.rows().filter((item) => {
        const days = this.diasParaVencer(item);
        return this.isActive(item) && days !== null && days >= 0 && days <= 30;
      }).length,
  );

  protected readonly expiring90Count = computed(
    () =>
      this.rows().filter((item) => {
        const days = this.diasParaVencer(item);
        return this.isActive(item) && days !== null && days >= 0 && days <= 90;
      }).length,
  );

  protected readonly expiredCount = computed(
    () =>
      this.rows().filter((item) => {
        const days = this.diasParaVencer(item);
        return this.isActive(item) && days !== null && days < 0;
      }).length,
  );

  protected readonly inventoryValue = computed(() =>
    this.rows()
      .filter((item) => this.isActive(item))
      .reduce((total, item) => total + item.cantidad * (item.precioUnitario ?? 0), 0),
  );

  protected readonly activeFilters = computed(
    () =>
      Boolean(this.searchTerm().trim()) ||
      this.selectedCategory() !== 'todos' ||
      this.selectedStockStatus() !== 'todos' ||
      this.selectedExpiryFilter() !== 'todos' ||
      this.selectedStatus() !== 'todos' ||
      this.selectedWarehouse() !== 'todos',
  );

  protected readonly inventoryCountLabel = computed(() => {
    if (this.loading()) {
      return 'Cargando inventario desde la base...';
    }

    if (this.errorMessage()) {
      return 'Sin datos cargados';
    }

    const total = this.rows().length;
    const visible = this.filteredRows().length;

    if (!total) {
      return '0 insumos registrados';
    }

    if (this.activeFilters()) {
      return `${visible} de ${total} insumos visibles`;
    }

    return `${total} insumos registrados`;
  });

  ngOnInit(): void {
    this.cache.ensureInventario(true);
  }

  protected updateSearch(event: Event): void {
    this.searchTerm.set((event.target as HTMLInputElement).value);
    this.resetPagination();
  }

  protected updateCategoryFilter(event: Event): void {
    this.selectedCategory.set((event.target as HTMLSelectElement).value);
    this.resetPagination();
  }

  protected updateStockFilter(event: Event): void {
    this.selectedStockStatus.set((event.target as HTMLSelectElement).value as StockStatusFilter);
    this.resetPagination();
  }

  protected updateExpiryFilter(event: Event): void {
    this.selectedExpiryFilter.set((event.target as HTMLSelectElement).value as ExpiryFilter);
    this.resetPagination();
  }

  protected applyExpiryFilter(filter: ExpiryFilter): void {
    this.selectedExpiryFilter.set(filter);
    this.resetPagination();
  }

  protected updateStatusFilter(event: Event): void {
    this.selectedStatus.set((event.target as HTMLSelectElement).value as InventoryStatusFilter);
    this.resetPagination();
  }

  protected updateWarehouseFilter(event: Event): void {
    this.selectedWarehouse.set((event.target as HTMLSelectElement).value);
    this.resetPagination();
  }

  protected clearAllFilters(): void {
    this.searchTerm.set('');
    this.selectedCategory.set('todos');
    this.selectedStockStatus.set('todos');
    this.selectedExpiryFilter.set('todos');
    this.selectedStatus.set('todos');
    this.selectedWarehouse.set('todos');
    this.resetPagination();
  }

  protected previousPage(): void {
    this.goToPage(this.displayPage() - 1);
  }

  protected nextPage(): void {
    this.goToPage(this.displayPage() + 1);
  }

  protected goToPage(page: number): void {
    this.currentPage.set(Math.min(Math.max(page, 1), this.totalPages()));
  }

  protected openKardex(item: InventarioItem): void {
    const normalized = this.normalizeItem(item);
    this.selectedItem.set(normalized);
    this.movementError.set('');
    this.modalMode.set('kardex');
    this.loadMovimientos(normalized.id);
  }

  protected openEditor(item?: InventarioItem): void {
    const defaultAlmacen = this.almacenesCatalogo()[0];
    this.selectedItem.set(item ?? null);
    this.formMessage.set('');
    this.itemForm.reset({
      nombreInsumo: item?.nombreInsumo ?? '',
      codigoInsumo: item?.codigoInsumo ?? '',
      categoria: item?.categoria ?? this.categoriasInventarioCatalogo()[0] ?? '',
      ubicacion: item?.ubicacion ?? item?.almacen ?? defaultAlmacen?.nombre ?? '',
      almacenId: item?.almacenId ?? defaultAlmacen?.id ?? 0,
      lote: item?.lote ?? '',
      fechaVencimiento: item?.fechaVencimiento ?? '',
      cantidad: item?.cantidad ?? 0,
      unidad: this.unidadLabel(item?.unidad) || 'unid.',
      stockMinimo: item?.stockMinimo ?? 0,
      precioUnitario: item?.precioUnitario ?? 0,
      proveedor: item?.proveedor ?? this.proveedoresActivos()[0]?.nombre ?? '',
      activo: item?.activo ?? true,
    });
    this.itemForm.markAsPristine();
    this.itemForm.markAsUntouched();
    this.modalMode.set(item ? 'edit' : 'create');
  }

  protected closeModal(): void {
    this.modalMode.set(null);
    this.formMessage.set('');
    this.movementError.set('');
  }

  protected toggleActivo(item: InventarioItem, event: Event): void {
    event.stopPropagation();
    const nextActivo = !this.isActive(item);
    const previous = { ...item };

    this.actionId.set(item.id);
    this.actionMessage.set('');
    this.upsertItem({ ...item, activo: nextActivo }, nextActivo);

    this.api
      .cambiarEstadoInventario(item.id, nextActivo)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (saved) => {
          this.upsertItem(saved, nextActivo);
          if (this.selectedItem()?.id === saved.id) {
            this.selectedItem.set(this.normalizeItem(saved, nextActivo));
          }
          this.actionId.set(null);
        },
        error: (error) => {
          this.upsertItem(previous, this.isActive(previous));
          this.actionMessage.set(this.actionErrorMessage(error));
          this.actionId.set(null);
        },
      });
  }

  protected deleteItem(item: InventarioItem, event: Event): void {
    event.stopPropagation();
    const confirmed = confirm(
      `¿Eliminar "${item.nombreInsumo}"?\n\nSe borrara del inventario junto con su Kardex. Esta accion no se puede deshacer.`,
    );
    if (!confirmed) {
      return;
    }

    this.actionId.set(item.id);
    this.api
      .eliminarInventario(item.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.cache.removeInventario(item.id);
          if (this.selectedItem()?.id === item.id) {
            this.closeModal();
            this.selectedItem.set(null);
          }
          this.actionId.set(null);
        },
        error: () => {
          this.actionMessage.set('No se pudo eliminar el insumo. Puede estar vinculado a una receta.');
          this.actionId.set(null);
        },
      });
  }

  protected isActionLoading(id: number): boolean {
    return this.actionId() === id;
  }

  protected isActive(item: InventarioItem): boolean {
    return coerceActivo(item.activo);
  }

  protected saveItem(): void {
    if (this.itemForm.invalid) {
      this.itemForm.markAllAsTouched();
      this.formMessage.set('Revisa los campos obligatorios antes de guardar.');
      return;
    }

    const selected = this.selectedItem();
    const payload = this.buildInventoryPayload();
    const request =
      this.modalMode() === 'edit' && selected?.id
        ? this.api.actualizarInventario(selected.id, payload)
        : this.api.crearInventario(payload);

    this.savingItem.set(true);
    this.formMessage.set('');

    request.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (saved) => {
        this.upsertItem(saved);
        this.selectedItem.set(saved);
        this.savingItem.set(false);
        this.modalMode.set('kardex');
        this.loadMovimientos(saved.id);
      },
      error: () => {
        this.formMessage.set('No se pudo guardar el insumo. Revisa codigo, proveedor y datos.');
        this.savingItem.set(false);
      },
    });
  }

  protected fieldInvalid(controlName: string): boolean {
    const control = this.itemForm.get(controlName);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  protected registrarMovimiento(
    tipo: string,
    cantidad: string,
    motivo: string,
    referencia: string,
    costoUnitario: string,
  ): void {
    const item = this.selectedItem();
    const parsedCantidad = Number(cantidad);
    const parsedCosto = costoUnitario ? Number(costoUnitario) : undefined;

    if (!item) {
      this.movementError.set('Selecciona un insumo para registrar movimiento.');
      return;
    }

    if (!Number.isFinite(parsedCantidad) || parsedCantidad < 0) {
      this.movementError.set('Ingresa una cantidad valida.');
      return;
    }

    if (parsedCosto !== undefined && (!Number.isFinite(parsedCosto) || parsedCosto < 0)) {
      this.movementError.set('Ingresa un costo unitario valido.');
      return;
    }

    this.movementSubmitting.set(true);
    this.movementError.set('');
    this.api
      .registrarMovimientoInventario(item.id, {
        tipo: tipo as MovimientoInventario['tipo'],
        cantidad: parsedCantidad,
        motivo: motivo || `Movimiento ${tipo}`,
        referencia: referencia || undefined,
        costoUnitario: parsedCosto,
      })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.movementSubmitting.set(false);
          this.cache.refreshInventario(true, (items) => {
            const updated = items.find((row) => row.id === item.id);
            if (updated) {
              this.selectedItem.set(this.normalizeItem(updated));
            }
            this.loadMovimientos(item.id);
          });
        },
        error: () => {
          this.movementError.set('No se pudo registrar el movimiento. Revisa stock y datos.');
          this.movementSubmitting.set(false);
        },
      });
  }

  protected estadoStock(item: InventarioItem): 'Ok' | 'Bajo' | 'Critico' {
    if (item.cantidad <= item.stockMinimo / 2) {
      return 'Critico';
    }

    if (item.cantidad <= item.stockMinimo) {
      return 'Bajo';
    }

    return 'Ok';
  }

  protected stockCoverage(item: InventarioItem): number {
    if (!item.stockMinimo) {
      return 100;
    }

    return Math.round((item.cantidad / item.stockMinimo) * 100);
  }

  protected diasParaVencer(item: InventarioItem): number | null {
    if (!item.fechaVencimiento) {
      return null;
    }

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const expiry = new Date(`${item.fechaVencimiento}T00:00:00`);
    return Math.ceil((expiry.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
  }

  protected matchesExpiryFilter(item: InventarioItem, filter: ExpiryFilter): boolean {
    const days = this.diasParaVencer(item);

    switch (filter) {
      case 'vencido':
        return days !== null && days < 0;
      case '30_dias':
        return days !== null && days >= 0 && days <= 30;
      case '3_meses':
        return days !== null && days >= 0 && days <= 90;
      case 'sin_fecha':
        return days === null;
      default:
        return true;
    }
  }

  protected estadoVencimiento(item: InventarioItem): 'sin_fecha' | 'vencido' | 'por_vencer' | 'ok' {
    const days = this.diasParaVencer(item);
    if (days === null) {
      return 'sin_fecha';
    }

    if (days < 0) {
      return 'vencido';
    }

    if (days <= 90) {
      return 'por_vencer';
    }

    return 'ok';
  }

  protected vencimientoLabel(item: InventarioItem): string {
    const days = this.diasParaVencer(item);
    if (days === null) {
      return 'Sin fecha';
    }
    if (days < 0) {
      return 'Vencido';
    }
    if (days <= 30) {
      return `Por vencer (${days}d)`;
    }
    if (days <= 90) {
      return `3 meses (${days}d)`;
    }
    return 'Vigente';
  }

  protected formatVencimiento(value?: string): string {
    if (!value) {
      return 'Sin fecha';
    }

    return new Intl.DateTimeFormat('es-PE', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
    }).format(new Date(`${value}T00:00:00`));
  }

  protected formatMoney(value?: number): string {
    return new Intl.NumberFormat('es-PE', {
      style: 'currency',
      currency: 'PEN',
      minimumFractionDigits: 2,
    }).format(value ?? 0);
  }

  protected stockValue(item: InventarioItem): number {
    return item.cantidad * (item.precioUnitario ?? 0);
  }

  protected unidadLabel = unidadLabel;

  protected formatDate(value?: string): string {
    if (!value) {
      return 'Sin fecha';
    }

    return new Intl.DateTimeFormat('es-PE', {
      dateStyle: 'short',
      timeStyle: 'short',
    }).format(new Date(value.replace(' ', 'T')));
  }

  private loadMovimientos(inventarioId: number): void {
    this.movementLoading.set(true);
    this.api
      .movimientosInventario(inventarioId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (movimientos) => {
          this.movimientos.set(movimientos);
          this.movementLoading.set(false);
        },
        error: () => {
          this.movimientos.set([]);
          this.movementError.set('No se pudo cargar el Kardex del insumo.');
          this.movementLoading.set(false);
        },
      });
  }

  private buildInventoryPayload(): InventarioPayload {
    const raw = this.itemForm.getRawValue();
    const almacen = this.almacenesCatalogo().find((item) => item.id === Number(raw.almacenId));

    return {
      nombreInsumo: raw.nombreInsumo.trim(),
      codigoInsumo: this.optionalText(raw.codigoInsumo),
      categoria: raw.categoria.trim(),
      ubicacion: raw.ubicacion.trim() || almacen?.nombre || '',
      almacenId: Number(raw.almacenId),
      lote: this.optionalText(raw.lote),
      fechaVencimiento: this.optionalText(raw.fechaVencimiento),
      cantidad: Number(raw.cantidad || 0),
      unidad: this.unidadLabel(raw.unidad),
      stockMinimo: Number(raw.stockMinimo || 0),
      precioUnitario: Number(raw.precioUnitario || 0),
      proveedor: raw.proveedor.trim(),
      activo: raw.activo,
    };
  }

  private toPayload(item: InventarioItem, activo: boolean): InventarioPayload {
    return {
      nombreInsumo: item.nombreInsumo,
      codigoInsumo: item.codigoInsumo,
      categoria: item.categoria,
      ubicacion: item.ubicacion,
      almacenId: item.almacenId,
      lote: item.lote,
      fechaVencimiento: item.fechaVencimiento,
      cantidad: item.cantidad,
      unidad: item.unidad,
      stockMinimo: item.stockMinimo,
      precioUnitario: item.precioUnitario,
      proveedor: item.proveedor,
      activo,
    };
  }

  private upsertItem(saved: InventarioItem, activoFallback?: boolean): void {
    this.cache.patchInventario(this.normalizeItem(saved, activoFallback));
  }

  private actionErrorMessage(error: unknown): string {
    const response = error as { status?: number; error?: { message?: string } };

    if (response.status === 0) {
      return 'El backend no responde. Reinicia Spring Boot en el puerto 8081.';
    }

    return response.error?.message || 'No se pudo cambiar el estado del insumo.';
  }

  protected exportInventarioExcel(): void {
    if (!this.canExportExcel()) {
      return;
    }

    void this.runInventarioExcelExport();
  }

  private async runInventarioExcelExport(): Promise<void> {
    const data = this.filteredRows();

    try {
      await downloadStyledExcel({
        filename: `inventario_cafedronel_${timestampForFilename()}.xlsx`,
        sheetName: 'Inventario',
        title: 'Cafedronel · Reporte de inventario',
        meta: [
          { label: 'Generado', value: formatExcelGeneratedAt() },
          { label: 'Registros', value: String(data.length) },
          { label: 'Filtros', value: this.buildInventarioFilterSummary() },
        ],
        headers: [
          'Codigo',
          'Insumo',
          'Categoria',
          'Proveedor',
          'Almacen',
          'Ubicacion',
          'Lote',
          'Cantidad',
          'Unidad',
          'Stock minimo',
          'Estado stock',
          'Vencimiento',
          'Estado vencimiento',
          'Costo unitario',
          'Valor total',
          'Estado',
        ],
        columnWidths: [12, 28, 16, 18, 14, 14, 12, 10, 10, 12, 14, 14, 18, 14, 14, 12],
        moneyColumnIndexes: [14, 15],
        integerColumnIndexes: [8, 10],
        rows: data.map((item) => [
          item.codigoInsumo || '',
          item.nombreInsumo,
          item.categoria,
          item.proveedor,
          item.almacen || '',
          item.ubicacion || '',
          item.lote || '',
          item.cantidad,
          item.unidad,
          item.stockMinimo,
          this.estadoStock(item),
          this.formatVencimiento(item.fechaVencimiento),
          this.vencimientoLabel(item),
          Number(item.precioUnitario ?? 0),
          Number(this.stockValue(item)),
          this.isActive(item) ? 'Activo' : 'Inactivo',
        ]),
        highlight: (row, columnIndex) => {
          if (columnIndex === 10) {
            const estado = String(row[10] ?? '');
            if (estado === 'Critico') return 'danger';
            if (estado === 'Bajo') return 'warning';
            if (estado === 'Ok') return 'ok';
          }
          if (columnIndex === 12) {
            const vencimiento = String(row[12] ?? '');
            if (vencimiento === 'Vencido') return 'danger';
            if (vencimiento.startsWith('Por vencer') || vencimiento.startsWith('3 meses')) return 'warning';
          }
          if (columnIndex === 15 && row[15] === 'Inactivo') {
            return 'warning';
          }
          return null;
        },
      });

      this.actionMessage.set(`Excel descargado con ${data.length} registro(s) segun los filtros actuales.`);
    } catch {
      this.actionMessage.set('No se pudo generar el archivo Excel.');
    }
  }

  private buildInventarioFilterSummary(): string {
    const parts: string[] = [];
    const term = this.searchTerm().trim();
    if (term) {
      parts.push(`Busqueda: "${term}"`);
    }
    if (this.selectedCategory() !== 'todos') {
      parts.push(`Categoria: ${this.selectedCategory()}`);
    }
    if (this.selectedStockStatus() !== 'todos') {
      parts.push(`Stock: ${this.selectedStockStatus()}`);
    }
    if (this.selectedExpiryFilter() !== 'todos') {
      parts.push(`Vencimiento: ${this.selectedExpiryFilter()}`);
    }
    if (this.selectedStatus() !== 'todos') {
      parts.push(`Estado: ${this.selectedStatus()}`);
    }
    if (this.selectedWarehouse() !== 'todos') {
      const warehouse = this.almacenesCatalogo().find(
        (item) => String(item.id) === this.selectedWarehouse(),
      );
      parts.push(`Almacen: ${warehouse?.nombre ?? this.selectedWarehouse()}`);
    }
    return parts.length ? parts.join(' | ') : 'Todos (sin filtros)';
  }

  private normalizeItem(item: InventarioItem, fallback = true): InventarioItem {
    return {
      ...item,
      activo: coerceActivo(item.activo, fallback),
      unidad: unidadLabel(item.unidad),
    };
  }

  private optionalText(value: string): string | undefined {
    const trimmed = value.trim();
    return trimmed ? trimmed : undefined;
  }

  private resetPagination(): void {
    this.currentPage.set(1);
  }

  private warehouseName(item: InventarioItem): string {
    return item.almacen || item.ubicacion || 'Sin almacen';
  }
}
