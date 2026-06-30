import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { forkJoin } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { PageHeader } from '../../../../shared/components/page-header/page-header';
import { SearchableSelectComponent } from '../../../../shared/components/searchable-select/searchable-select';
import { coerceActivo } from '../../../../core/utils/boolean.util';
import { CafederonelApiService } from '../../../../services/cafederonel-api.service';
import { Proveedor, ProveedorPayload } from '../../../../models/proveedor.model';

type ProveedorModalMode = 'detail' | 'edit' | 'create' | null;
type ProveedorStatusFilter = 'todos' | 'activo' | 'inactivo';

const DEPARTAMENTOS_PERU = [
  'Amazonas',
  'Ancash',
  'Apurimac',
  'Arequipa',
  'Ayacucho',
  'Cajamarca',
  'Callao',
  'Cusco',
  'Huancavelica',
  'Huanuco',
  'Ica',
  'Junin',
  'La Libertad',
  'Lambayeque',
  'Lima',
  'Loreto',
  'Madre de Dios',
  'Moquegua',
  'Pasco',
  'Piura',
  'Puno',
  'San Martin',
  'Tacna',
  'Tumbes',
  'Ucayali',
] as const;

@Component({
  selector: 'app-proveedores-page',
  imports: [PageHeader, ReactiveFormsModule, SearchableSelectComponent],
  templateUrl: './proveedores-page.html',
  styleUrl: './proveedores-page.scss',
})
export class ProveedoresPage {
  private readonly api = inject(CafederonelApiService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly fb = inject(FormBuilder);
  private readonly pageSize = 10;

  protected readonly categoriasCatalogo = signal<string[]>([]);
  protected readonly departamentos = DEPARTAMENTOS_PERU;
  protected readonly rows = signal<Proveedor[]>([]);
  protected readonly searchTerm = signal('');
  protected readonly selectedCategory = signal('todos');
  protected readonly selectedStatus = signal<ProveedorStatusFilter>('todos');
  protected readonly currentPage = signal(1);
  protected readonly selectedProveedor = signal<Proveedor | null>(null);
  protected readonly modalMode = signal<ProveedorModalMode>(null);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly actionId = signal<number | null>(null);
  protected readonly errorMessage = signal('');
  protected readonly actionMessage = signal('');
  protected readonly formMessage = signal('');

  protected readonly supplierForm = this.fb.nonNullable.group({
    codigoProveedor: ['', [Validators.maxLength(50)]],
    nombre: ['', [Validators.required, Validators.maxLength(150)]],
    categoria: ['', [Validators.required, Validators.maxLength(80)]],
    ruc: ['', [Validators.pattern(/^(\d{21})?$/)]],
    contacto: ['', [Validators.maxLength(120)]],
    telefono: ['', [Validators.required, Validators.maxLength(30)]],
    direccion: ['', [Validators.required, Validators.maxLength(255)]],
    ciudad: ['Lima', [Validators.required, Validators.maxLength(80)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
    sitioWeb: ['', [Validators.maxLength(255)]],
    notas: ['', [Validators.maxLength(500)]],
    activo: [true],
  });

  protected readonly filteredRows = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();
    const category = this.selectedCategory();
    const status = this.selectedStatus();

    return this.rows().filter((row) => {
      const matchesTerm = term
        ? [
            row.codigoProveedor,
            row.nombre,
            row.categoria,
            row.ruc,
            row.contacto,
            row.telefono,
            row.email,
            row.direccion,
            row.ciudad,
            row.notas,
          ]
            .filter(Boolean)
            .some((value) => String(value).toLowerCase().includes(term))
        : true;

      return (
        matchesTerm &&
        (category === 'todos' || row.categoria === category) &&
        (status === 'todos' || (status === 'activo' ? this.isActive(row) : !this.isActive(row)))
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
  protected readonly categoryCount = computed(() => this.categoryOptions().length);

  protected readonly supplierCountLabel = computed(() => {
    if (this.loading()) return 'Cargando proveedores desde la base...';
    if (this.errorMessage()) return 'Sin datos cargados';
    const total = this.rows().length;
    const visible = this.filteredRows().length;
    if (!total) return '0 proveedores registrados';
    if (visible !== total) return `${visible} de ${total} proveedores visibles`;
    return `${total} proveedores registrados`;
  });

  constructor() {
    forkJoin({
      proveedores: this.api.proveedores(),
      categorias: this.api.categoriasProducto(),
    })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: ({ proveedores, categorias }) => {
          this.categoriasCatalogo.set(categorias.map((item) => item.nombre));
          this.rows.set(proveedores.map((proveedor) => this.normalizeProveedor(proveedor)));
          this.loading.set(false);
        },
        error: () => {
          this.errorMessage.set('No se pudieron cargar proveedores desde la base de datos.');
          this.loading.set(false);
        },
      });
  }

  protected updateSearch(event: Event): void {
    this.searchTerm.set((event.target as HTMLInputElement).value);
    this.currentPage.set(1);
  }

  protected updateCategoryFilter(event: Event): void {
    this.selectedCategory.set((event.target as HTMLSelectElement).value);
    this.currentPage.set(1);
  }

  protected updateStatusFilter(event: Event): void {
    this.selectedStatus.set((event.target as HTMLSelectElement).value as ProveedorStatusFilter);
    this.currentPage.set(1);
  }

  protected clearAllFilters(): void {
    this.searchTerm.set('');
    this.selectedCategory.set('todos');
    this.selectedStatus.set('todos');
    this.currentPage.set(1);
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

  protected openDetail(proveedor: Proveedor): void {
    this.selectedProveedor.set(proveedor);
    this.formMessage.set('');
    this.modalMode.set('detail');
  }

  protected openEditor(proveedor?: Proveedor): void {
    this.selectedProveedor.set(proveedor ?? null);
    this.formMessage.set('');
    this.supplierForm.reset({
      codigoProveedor: proveedor?.codigoProveedor ?? '',
      nombre: proveedor?.nombre ?? '',
      categoria: proveedor?.categoria ?? this.categoriasCatalogo()[0] ?? '',
      ruc: proveedor?.ruc ?? '',
      contacto: proveedor?.contacto ?? '',
      telefono: proveedor?.telefono ?? '',
      direccion: proveedor?.direccion ?? '',
      ciudad: this.resolveDepartamento(proveedor?.ciudad),
      email: proveedor?.email ?? '',
      sitioWeb: proveedor?.sitioWeb ?? '',
      notas: proveedor?.notas ?? '',
      activo: proveedor?.activo ?? true,
    });
    this.modalMode.set(proveedor ? 'edit' : 'create');
  }

  protected closeModal(): void {
    this.modalMode.set(null);
    this.formMessage.set('');
  }

  protected toggleActivo(proveedor: Proveedor, event: Event): void {
    event.stopPropagation();
    const nextActivo = !this.isActive(proveedor);
    const previous = { ...proveedor };

    this.actionId.set(proveedor.id);
    this.actionMessage.set('');
    this.upsertProveedor({ ...proveedor, activo: nextActivo }, nextActivo);

    this.api
      .cambiarEstadoProveedor(proveedor.id, nextActivo)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (saved) => {
          this.upsertProveedor(saved, nextActivo);
          if (this.selectedProveedor()?.id === saved.id) {
            this.selectedProveedor.set(this.normalizeProveedor(saved, nextActivo));
          }
          this.actionId.set(null);
        },
        error: (error) => {
          this.upsertProveedor(previous, this.isActive(previous));
          this.actionMessage.set(this.saveErrorMessage(error));
          this.actionId.set(null);
        },
      });
  }

  protected deleteProveedor(proveedor: Proveedor, event: Event): void {
    event.stopPropagation();
    if (!confirm(`¿Eliminar "${proveedor.nombre}"?\n\nEsta accion no se puede deshacer.`)) {
      return;
    }

    this.actionId.set(proveedor.id);
    this.api
      .eliminarProveedor(proveedor.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.rows.set(this.rows().filter((row) => row.id !== proveedor.id));
          if (this.selectedProveedor()?.id === proveedor.id) {
            this.closeModal();
          }
          this.actionId.set(null);
        },
        error: (error) => {
          this.actionMessage.set(this.saveErrorMessage(error));
          this.actionId.set(null);
        },
      });
  }

  protected saveProveedor(): void {
    if (this.supplierForm.invalid) {
      this.supplierForm.markAllAsTouched();
      this.formMessage.set('Revisa los campos obligatorios antes de guardar.');
      return;
    }

    const selected = this.selectedProveedor();
    const payload = this.buildPayload();
    const request =
      this.modalMode() === 'edit' && selected?.id
        ? this.api.actualizarProveedor(selected.id, payload)
        : this.api.crearProveedor(payload);

    this.saving.set(true);
    this.formMessage.set('');

    request.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (saved) => {
        this.upsertProveedor(saved);
        this.selectedProveedor.set(this.normalizeProveedor(saved));
        this.saving.set(false);
        this.modalMode.set('detail');
      },
      error: (error) => {
        this.formMessage.set(this.saveErrorMessage(error));
        this.saving.set(false);
      },
    });
  }

  protected isActive(proveedor: Proveedor): boolean {
    return coerceActivo(proveedor.activo);
  }

  protected isActionLoading(id: number): boolean {
    return this.actionId() === id;
  }

  protected fieldInvalid(controlName: string): boolean {
    const control = this.supplierForm.get(controlName);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  private resolveDepartamento(ciudad?: string): string {
    if (!ciudad?.trim()) {
      return 'Lima';
    }

    const normalized = ciudad.trim();
    const direct = DEPARTAMENTOS_PERU.find((departamento) => departamento.toLowerCase() === normalized.toLowerCase());
    return direct ?? normalized;
  }

  private buildPayload(): ProveedorPayload {
    const raw = this.supplierForm.getRawValue();
    return {
      codigoProveedor: this.optionalText(raw.codigoProveedor),
      nombre: raw.nombre.trim(),
      categoria: raw.categoria.trim(),
      ruc: this.optionalText(raw.ruc),
      contacto: this.optionalText(raw.contacto),
      telefono: raw.telefono.trim(),
      direccion: raw.direccion.trim(),
      ciudad: raw.ciudad.trim() || 'Lima',
      email: raw.email.trim(),
      sitioWeb: this.optionalText(raw.sitioWeb),
      notas: this.optionalText(raw.notas),
      activo: raw.activo,
    };
  }

  private upsertProveedor(saved: Proveedor, activoFallback?: boolean): void {
    const normalized = this.normalizeProveedor(saved, activoFallback);
    const current = this.rows();
    const exists = current.some((row) => row.id === normalized.id);
    this.rows.set(
      exists ? current.map((row) => (row.id === normalized.id ? normalized : row)) : [normalized, ...current],
    );
  }

  private normalizeProveedor(proveedor: Proveedor, fallback = true): Proveedor {
    return {
      ...proveedor,
      activo: coerceActivo(proveedor.activo, fallback),
      ciudad: this.resolveDepartamento(proveedor.ciudad),
    };
  }

  private optionalText(value: string): string | undefined {
    const trimmed = value.trim();
    return trimmed ? trimmed : undefined;
  }

  private saveErrorMessage(error: unknown): string {
    const response = error as { status?: number; error?: { message?: string } };
    if (response.status === 0) {
      return 'El backend no responde. Reinicia Spring Boot en el puerto 8081.';
    }
    return response.error?.message || 'No se pudo completar la operacion.';
  }
}
