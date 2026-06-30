import { Injectable, inject, signal } from '@angular/core';
import { catchError, finalize, forkJoin, of, timeout } from 'rxjs';

import { CafederonelApiService } from './cafederonel-api.service';
import { InventarioItem } from '../models/inventario.model';
import { Producto } from '../models/producto.model';
import { Almacen } from '../models/catalogo.model';
import { Proveedor } from '../models/proveedor.model';
import { normalizeUnidad } from '../core/utils/unidad.util';

@Injectable({ providedIn: 'root' })
export class CatalogCacheService {
  private readonly api = inject(CafederonelApiService);

  readonly inventario = signal<InventarioItem[]>([]);
  readonly productos = signal<Producto[]>([]);
  readonly almacenes = signal<Almacen[]>([]);
  readonly proveedores = signal<Proveedor[]>([]);
  readonly categoriasInventario = signal<string[]>([]);
  readonly categoriasProducto = signal<string[]>([]);

  readonly inventarioLoading = signal(false);
  readonly productosLoading = signal(false);
  readonly inventarioError = signal('');
  readonly productosError = signal('');

  private inventarioInflight = false;
  private productosInflight = false;

  constructor() {
    this.resetInventarioCache();
  }

  resetInventarioCache(): void {
    this.inventario.set([]);
    this.inventarioInflight = false;
    this.inventarioLoading.set(false);
    this.inventarioError.set('');
  }

  ensureInventario(force = false): void {
    this.rehydrateInventarioUnidades();

    const hasData = this.inventario().length > 0;
    if (hasData && !force) {
      this.refreshInventario(true);
      this.ensureCatalogosInventario();
      return;
    }

    this.refreshInventario(false);
    this.ensureCatalogosInventario();
  }

  ensureProductos(force = false): void {
    const hasData = this.productos().length > 0;
    if (hasData && !force) {
      this.refreshProductos(true);
      this.ensureCategoriasProducto();
      return;
    }

    this.refreshProductos(false);
    this.ensureCategoriasProducto();
  }

  refreshInventario(silent = false, onDone?: (items: InventarioItem[]) => void): void {
    if (this.inventarioInflight) {
      return;
    }

    this.inventarioInflight = true;

    if (!silent) {
      this.inventarioLoading.set(true);
    }
    this.inventarioError.set('');

    this.api
      .inventario()
      .pipe(
        timeout(15000),
        catchError((error) => {
          this.inventarioError.set(this.httpErrorMessage(error, 'inventario'));
          return of([] as InventarioItem[]);
        }),
        finalize(() => {
          this.inventarioInflight = false;
          this.inventarioLoading.set(false);
        }),
      )
      .subscribe((items) => {
        const normalized = items.map((item) => this.normalizeInventarioItem(item));
        this.inventario.set(normalized);
        onDone?.(normalized);
      });
  }

  refreshProductos(silent = false, onDone?: (items: Producto[]) => void): void {
    if (this.productosInflight) {
      return;
    }

    this.productosInflight = true;

    if (!silent) {
      this.productosLoading.set(true);
    }
    this.productosError.set('');

    this.api
      .productos()
      .pipe(
        timeout(15000),
        catchError((error) => {
          this.productosError.set(this.httpErrorMessage(error, 'productos'));
          return of([] as Producto[]);
        }),
        finalize(() => {
          this.productosInflight = false;
          this.productosLoading.set(false);
        }),
      )
      .subscribe((items) => {
        this.productos.set(items);
        onDone?.(items);
      });
  }

  ensureCatalogosInventario(): void {
    if (this.almacenes().length && this.proveedores().length && this.categoriasInventario().length) {
      return;
    }

    forkJoin({
      almacenes: this.api.almacenes().pipe(catchError(() => of([] as Almacen[]))),
      proveedores: this.api.proveedores().pipe(catchError(() => of([] as Proveedor[]))),
      categoriasInventario: this.api.categoriasInventario().pipe(catchError(() => of([] as string[]))),
    })
      .pipe(
        timeout(15000),
      )
      .subscribe(({ almacenes, proveedores, categoriasInventario }) => {
        if (almacenes.length) {
          this.almacenes.set(almacenes);
        }
        if (proveedores.length) {
          this.proveedores.set(proveedores);
        }
        if (categoriasInventario.length) {
          this.categoriasInventario.set(categoriasInventario);
        }
      });
  }

  ensureCategoriasProducto(): void {
    if (this.categoriasProducto().length) {
      return;
    }

    this.api
      .categoriasProducto()
      .pipe(timeout(15000), catchError(() => of([])))
      .subscribe((categorias) => {
        if (categorias.length) {
          this.categoriasProducto.set(categorias.map((item) => item.nombre));
        }
      });
  }

  patchProducto(producto: Producto): void {
    const current = this.productos();
    const exists = current.some((item) => item.id === producto.id);
    this.productos.set(
      exists ? current.map((item) => (item.id === producto.id ? producto : item)) : [producto, ...current],
    );
  }

  removeProducto(id: number): void {
    this.productos.set(this.productos().filter((item) => item.id !== id));
  }

  patchInventario(item: InventarioItem): void {
    const normalized = this.normalizeInventarioItem(item);
    const current = this.inventario();
    const exists = current.some((row) => row.id === normalized.id);
    this.inventario.set(
      exists ? current.map((row) => (row.id === normalized.id ? normalized : row)) : [normalized, ...current],
    );
  }

  removeInventario(id: number): void {
    this.inventario.set(this.inventario().filter((item) => item.id !== id));
  }

  private rehydrateInventarioUnidades(): void {
    const current = this.inventario();
    if (!current.length) {
      return;
    }

    this.inventario.set(current.map((item) => this.normalizeInventarioItem(item)));
  }

  private normalizeInventarioItem(item: InventarioItem): InventarioItem {
    return { ...item, unidad: normalizeUnidad(item.unidad) };
  }

  private httpErrorMessage(error: unknown, resource: 'inventario' | 'productos'): string {
    const response = error as { status?: number; name?: string };

    if (response.status === 0) {
      return 'El backend no responde. Verifica que Spring Boot este activo en el puerto 8081.';
    }

    if (response.status === 401) {
      return 'Sesion expirada. Vuelve a iniciar sesion.';
    }

    if (response.name === 'TimeoutError') {
      return 'La base tardo demasiado en responder. Intenta de nuevo.';
    }

    return resource === 'inventario'
      ? 'No se pudo cargar el inventario desde la base de datos.'
      : 'No se pudieron cargar productos desde la base de datos.';
  }
}
