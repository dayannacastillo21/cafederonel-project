import { Component, DestroyRef, OnInit, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { PageHeader } from '../../../../shared/components/page-header/page-header';
import { SearchableSelectComponent } from '../../../../shared/components/searchable-select/searchable-select';
import { coerceActivo } from '../../../../core/utils/boolean.util';
import { onProductImageError, productImageUrl } from '../../../../core/utils/product-image.util';
import { CafederonelApiService } from '../../../../services/cafederonel-api.service';
import { CatalogCacheService } from '../../../../services/catalog-cache.service';
import { Producto, ProductoPayload } from '../../../../models/producto.model';
import { ProductoStockDisponible } from '../../../../models/producto-stock.model';

type ProductModalMode = 'detail' | 'edit' | 'create' | null;
type ProductStatusFilter = 'todos' | 'activo' | 'inactivo';
type ProductAlertFilter = 'todos' | 'sin_codigo' | 'margen_bajo' | 'inactivos' | 'sin_stock' | 'stock_bajo';

@Component({
  selector: 'app-productos-page',
  imports: [PageHeader, ReactiveFormsModule, SearchableSelectComponent],
  templateUrl: './productos-page.html',
  styleUrl: './productos-page.scss',
})
export class ProductosPage implements OnInit {
  private readonly cache = inject(CatalogCacheService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly fb = inject(FormBuilder);
  private readonly pageSize = 10;
  private readonly api = inject(CafederonelApiService);

  protected readonly columns = ['SKU', 'Producto', 'Codigo', 'Categoria', 'Precio', 'Margen', 'Estado', 'Acciones'];

  protected readonly productImageUrl = productImageUrl;
  protected readonly onProductImageError = onProductImageError;
  protected readonly rows = computed(() =>
    this.cache.productos().map((producto) => this.normalizeProducto(producto)),
  );
  protected readonly categoriasCatalogo = this.cache.categoriasProducto;
  protected readonly loading = computed(
    () => this.cache.productosLoading() && this.cache.productos().length === 0,
  );
  protected readonly errorMessage = this.cache.productosError;

  protected readonly formMessage = signal('');
  protected readonly searchTerm = signal('');
  protected readonly selectedCategory = signal('todos');
  protected readonly selectedStatus = signal<ProductStatusFilter>('todos');
  protected readonly selectedAlert = signal<ProductAlertFilter>('todos');
  protected readonly currentPage = signal(1);
  protected readonly selectedProduct = signal<Producto | null>(null);
  protected readonly modalMode = signal<ProductModalMode>(null);
  protected readonly saving = signal(false);
  protected readonly actionId = signal<number | null>(null);
  protected readonly actionMessage = signal('');
  protected readonly stockByProductId = signal<Map<number, ProductoStockDisponible>>(new Map());
  protected readonly stockLoading = signal(true);

  protected readonly productForm = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(150)]],
    descripcion: ['', [Validators.maxLength(500)]],
    precio: [0, [Validators.required, Validators.min(0.01)]],
    categoria: ['', [Validators.required, Validators.maxLength(80)]],
    sku: ['', [Validators.maxLength(50)]],
    codigoBarras: ['', [Validators.maxLength(80)]],
    costo: [0, [Validators.min(0)]],
    margenPorcentaje: [0, [Validators.min(0)]],
    imagenUrl: ['', [Validators.maxLength(500)]],
    unidadVenta: ['unidad', [Validators.maxLength(30)]],
    activo: [true],
  });

  protected readonly filteredRows = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();
    const category = this.selectedCategory();
    const status = this.selectedStatus();
    const alert = this.selectedAlert();

    return this.rows().filter((row) => {
      const matchesTerm = term
        ? [row.nombre, row.categoria, row.sku, row.codigoBarras, row.descripcion, row.unidadVenta]
            .filter(Boolean)
            .some((value) => String(value).toLowerCase().includes(term))
        : true;

      return (
        matchesTerm &&
        (category === 'todos' || row.categoria === category) &&
        (status === 'todos' || (status === 'activo' ? this.isActive(row) : !this.isActive(row))) &&
        this.matchesAlert(row, alert)
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
        ...this.categoriasCatalogo(),
        ...this.rows().map((row) => row.categoria).filter(Boolean),
      ]),
    ).sort((a, b) => a.localeCompare(b)),
  );

  protected readonly activeCount = computed(() => this.rows().filter((row) => this.isActive(row)).length);
  protected readonly inactiveCount = computed(() => this.rows().filter((row) => !this.isActive(row)).length);
  protected readonly missingBarcodeCount = computed(
    () => this.rows().filter((row) => !row.codigoBarras || !row.codigoBarras.trim()).length,
  );
  protected readonly lowMarginCount = computed(() => this.rows().filter((row) => this.margin(row) < 35).length);
  protected readonly noStockCount = computed(() =>
    this.rows().filter((row) => this.stockUnits(row) === 0).length,
  );
  protected readonly lowStockCount = computed(() =>
    this.rows().filter((row) => {
      const units = this.stockUnits(row);
      return units !== null && units > 0 && units <= 5;
    }).length,
  );

  protected readonly activeFilters = computed(
    () =>
      Boolean(this.searchTerm().trim()) ||
      this.selectedCategory() !== 'todos' ||
      this.selectedStatus() !== 'todos' ||
      this.selectedAlert() !== 'todos',
  );

  protected readonly productCountLabel = computed(() => {
    if (this.loading()) {
      return 'Cargando productos desde la base...';
    }

    if (this.errorMessage()) {
      return 'Sin datos cargados';
    }

    const total = this.rows().length;
    const visible = this.filteredRows().length;

    if (!total) {
      return '0 productos registrados';
    }

    if (this.activeFilters()) {
      return `${visible} de ${total} productos visibles`;
    }

    return `${total} productos registrados`;
  });

  ngOnInit(): void {
    this.cache.ensureProductos();
    this.loadStockVendible();
  }

  protected stockUnits(producto: Producto): number | null {
    const stock = this.stockByProductId().get(producto.id);
    if (!stock || stock.sinReceta) {
      return null;
    }
    return stock.unidadesDisponibles ?? 0;
  }

  protected stockLabel(producto: Producto): string {
    const stock = this.stockByProductId().get(producto.id);
    if (this.stockLoading()) {
      return '...';
    }
    if (!stock || stock.sinReceta) {
      return 'Sin insumos';
    }
    const units = stock.unidadesDisponibles ?? 0;
    return `${units} uds.`;
  }

  protected stockHint(producto: Producto): string {
    const stock = this.stockByProductId().get(producto.id);
    if (!stock?.insumoLimitante || stock.sinReceta) {
      return 'Segun insumos de inventario';
    }
    return `Limita: ${stock.insumoLimitante}`;
  }

  protected isStockDanger(producto: Producto): boolean {
    const units = this.stockUnits(producto);
    return units !== null && units === 0;
  }

  protected isStockWarning(producto: Producto): boolean {
    const units = this.stockUnits(producto);
    return units !== null && units > 0 && units <= 5;
  }

  protected updateSearch(event: Event): void {
    this.searchTerm.set((event.target as HTMLInputElement).value);
    this.resetPagination();
  }

  protected updateCategoryFilter(event: Event): void {
    this.selectedCategory.set((event.target as HTMLSelectElement).value);
    this.resetPagination();
  }

  protected updateStatusFilter(event: Event): void {
    this.selectedStatus.set((event.target as HTMLSelectElement).value as ProductStatusFilter);
    this.resetPagination();
  }

  protected updateAlertFilter(event: Event): void {
    this.selectedAlert.set((event.target as HTMLSelectElement).value as ProductAlertFilter);
    this.resetPagination();
  }

  protected clearAllFilters(): void {
    this.searchTerm.set('');
    this.selectedCategory.set('todos');
    this.selectedStatus.set('todos');
    this.selectedAlert.set('todos');
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

  protected openDetail(producto: Producto): void {
    this.selectedProduct.set(producto);
    this.formMessage.set('');
    this.modalMode.set('detail');
  }

  protected openEditor(producto?: Producto): void {
    this.ensureCategoriasLoaded();

    this.selectedProduct.set(producto ?? null);
    this.formMessage.set('');
    this.productForm.reset({
      nombre: producto?.nombre ?? '',
      descripcion: producto?.descripcion ?? '',
      precio: producto?.precio ?? 0,
      categoria: producto?.categoria ?? this.categoriasCatalogo()[0] ?? '',
      sku: producto?.sku ?? '',
      codigoBarras: producto?.codigoBarras ?? '',
      costo: producto?.costo ?? 0,
      margenPorcentaje: producto ? this.margin(producto) : 0,
      imagenUrl: producto?.imagenUrl ?? '',
      unidadVenta: producto?.unidadVenta ?? 'unidad',
      activo: producto?.activo ?? true,
    });
    this.productForm.markAsPristine();
    this.productForm.markAsUntouched();
    this.modalMode.set(producto ? 'edit' : 'create');
  }

  protected closeModal(): void {
    this.modalMode.set(null);
    this.formMessage.set('');
  }

  protected toggleActivo(producto: Producto, event: Event): void {
    event.stopPropagation();
    const nextActivo = !this.isActive(producto);
    const previous = { ...producto };

    this.actionId.set(producto.id);
    this.actionMessage.set('');
    this.upsertProduct({ ...producto, activo: nextActivo }, nextActivo);

    this.api
      .cambiarEstadoProducto(producto.id, nextActivo)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (saved) => {
          this.upsertProduct(saved, nextActivo);
          if (this.selectedProduct()?.id === saved.id) {
            this.selectedProduct.set(this.normalizeProducto(saved, nextActivo));
          }
          this.actionId.set(null);
        },
        error: (error) => {
          this.upsertProduct(previous, this.isActive(previous));
          this.actionMessage.set(this.saveErrorMessage(error));
          this.actionId.set(null);
        },
      });
  }

  protected deleteProduct(producto: Producto, event: Event): void {
    event.stopPropagation();
    const confirmed = confirm(
      `¿Eliminar "${producto.nombre}"?\n\nSe borrara del catalogo. Esta accion no se puede deshacer.`,
    );
    if (!confirmed) {
      return;
    }

    this.actionId.set(producto.id);
    this.api
      .eliminarProducto(producto.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.cache.removeProducto(producto.id);
          if (this.selectedProduct()?.id === producto.id) {
            this.closeModal();
            this.selectedProduct.set(null);
          }
          this.actionId.set(null);
        },
        error: (error) => {
          this.errorMessage.set(this.saveErrorMessage(error));
          this.actionId.set(null);
        },
      });
  }

  protected isActionLoading(id: number): boolean {
    return this.actionId() === id;
  }

  protected isActive(producto: Producto): boolean {
    return coerceActivo(producto.activo);
  }

  protected saveProduct(): void {
    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      this.formMessage.set('Revisa los campos obligatorios antes de guardar.');
      return;
    }

    const mode = this.modalMode();
    const selected = this.selectedProduct();
    const payload = this.buildPayload();
    const request =
      mode === 'edit' && selected?.id
        ? this.api.actualizarProducto(selected.id, payload)
        : this.api.crearProducto(payload);

    this.saving.set(true);
    this.formMessage.set('');

    request.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (saved) => {
        this.upsertProduct(saved);
        this.selectedProduct.set(this.normalizeProducto(saved));
        this.saving.set(false);
        this.modalMode.set('detail');
      },
      error: (error) => {
        this.formMessage.set(this.saveErrorMessage(error));
        this.saving.set(false);
      },
    });
  }

  protected fieldInvalid(controlName: string): boolean {
    const control = this.productForm.get(controlName);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  protected margin(producto: Producto): number {
    if (producto.margenPorcentaje !== undefined && producto.margenPorcentaje !== null) {
      return producto.margenPorcentaje;
    }

    if (!producto.precio || !producto.costo) {
      return 0;
    }

    return ((producto.precio - producto.costo) / producto.precio) * 100;
  }

  protected formatPercent(value: number): string {
    return new Intl.NumberFormat('es-PE', {
      minimumFractionDigits: 0,
      maximumFractionDigits: 1,
    }).format(value);
  }

  protected formatMoney(value?: number): string {
    return new Intl.NumberFormat('es-PE', {
      style: 'currency',
      currency: 'PEN',
      minimumFractionDigits: 2,
    }).format(value ?? 0);
  }

  private ensureCategoriasLoaded(): void {
    if (this.categoriasCatalogo().length) {
      return;
    }

    this.api
      .categoriasProducto()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (categorias) => {
          this.categoriasCatalogo.set(categorias.map((item) => item.nombre));
        },
      });
  }

  private buildPayload(): ProductoPayload {
    const raw = this.productForm.getRawValue();
    return this.toPayloadFromForm(raw);
  }

  private toPayload(producto: Producto, activo: boolean): ProductoPayload {
    return {
      nombre: producto.nombre.trim(),
      descripcion: producto.descripcion,
      precio: producto.precio,
      categoria: producto.categoria,
      sku: producto.sku,
      codigoBarras: producto.codigoBarras,
      costo: producto.costo ?? 0,
      margenPorcentaje: this.margin(producto),
      imagenUrl: producto.imagenUrl,
      unidadVenta: producto.unidadVenta || 'unidad',
      activo,
    };
  }

  private toPayloadFromForm(raw: ReturnType<typeof this.productForm.getRawValue>): ProductoPayload {
    return {
      nombre: raw.nombre.trim(),
      descripcion: this.optionalText(raw.descripcion),
      precio: Number(raw.precio || 0),
      categoria: raw.categoria.trim(),
      sku: this.optionalText(raw.sku),
      codigoBarras: this.optionalText(raw.codigoBarras),
      costo: Number(raw.costo || 0),
      margenPorcentaje: Number(raw.margenPorcentaje || 0),
      imagenUrl: this.optionalText(raw.imagenUrl),
      unidadVenta: raw.unidadVenta.trim() || 'unidad',
      activo: raw.activo,
    };
  }

  private optionalText(value: string): string | undefined {
    const trimmed = value.trim();
    return trimmed ? trimmed : undefined;
  }

  private upsertProduct(saved: Producto, activoFallback?: boolean): void {
    this.cache.patchProducto(this.normalizeProducto(saved, activoFallback));
  }

  private matchesAlert(producto: Producto, alert: ProductAlertFilter): boolean {
    if (alert === 'todos') {
      return true;
    }

    if (alert === 'sin_codigo') {
      return !producto.codigoBarras || !producto.codigoBarras.trim();
    }

    if (alert === 'margen_bajo') {
      return this.margin(producto) < 35;
    }

    if (alert === 'sin_stock') {
      return this.stockUnits(producto) === 0;
    }

    if (alert === 'stock_bajo') {
      const units = this.stockUnits(producto);
      return units !== null && units > 0 && units <= 5;
    }

    return !this.isActive(producto);
  }

  private loadStockVendible(): void {
    this.stockLoading.set(true);
    this.api
      .productosStockVendible()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (items) => {
          const map = new Map<number, ProductoStockDisponible>();
          for (const item of items) {
            map.set(item.productoId, item);
          }
          this.stockByProductId.set(map);
          this.stockLoading.set(false);
        },
        error: () => {
          this.stockLoading.set(false);
        },
      });
  }

  private normalizeProducto(producto: Producto, fallback = true): Producto {
    return { ...producto, activo: coerceActivo(producto.activo, fallback) };
  }

  private resetPagination(): void {
    this.currentPage.set(1);
  }

  private saveErrorMessage(error: unknown): string {
    const response = error as { status?: number; error?: { message?: string }; message?: string };

    if (response.status === 0) {
      return 'El backend no responde. Verifica que Spring Boot este corriendo en el puerto 8081.';
    }

    if (response.status === 401) {
      return 'Tu sesion expiro o falta iniciar sesion. Vuelve a entrar con tu usuario.';
    }

    return response.error?.message || response.message || 'No se pudo guardar el producto.';
  }
}
