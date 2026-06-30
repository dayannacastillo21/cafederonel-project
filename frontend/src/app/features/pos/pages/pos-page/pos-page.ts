import { Component, OnDestroy, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { map } from 'rxjs/operators';

import { AuthService } from '../../../../core/auth/auth.service';
import { onProductImageError, productImageUrl } from '../../../../core/utils/product-image.util';
import { CafederonelApiService } from '../../../../services/cafederonel-api.service';
import { CajaSesion } from '../../../../models/caja.model';
import { Producto } from '../../../../models/producto.model';
import { PosCartItem } from '../../../../models/pos.model';

type CategoryChip = {
  value: string;
  label: string;
  icon: string;
};

type CajaPanel = 'apertura' | 'cierre' | 'menu' | null;

@Component({
  selector: 'app-pos-page',
  imports: [FormsModule],
  templateUrl: './pos-page.html',
  styleUrl: './pos-page.scss',
})
export class PosPage implements OnInit, OnDestroy {
  private readonly api = inject(CafederonelApiService);
  private readonly auth = inject(AuthService);
  private readonly route = inject(ActivatedRoute);
  private clockTimer?: ReturnType<typeof setInterval>;

  protected readonly productImageUrl = productImageUrl;
  protected readonly onProductImageError = onProductImageError;

  protected readonly productos = signal<Producto[]>([]);
  protected readonly cart = signal<PosCartItem[]>([]);
  protected readonly searchTerm = signal('');
  protected readonly selectedCategory = signal('todos');
  protected readonly loading = signal(true);
  protected readonly submitting = signal(false);
  protected readonly cajaLoading = signal(true);
  protected readonly cajaSubmitting = signal(false);
  protected readonly errorMessage = signal('');
  protected readonly successMessage = signal('');
  protected readonly nowLabel = signal('');

  protected readonly cajaSesion = signal<CajaSesion | null>(null);
  protected readonly cajaPanel = signal<CajaPanel>(null);
  protected readonly montoApertura = signal(0);
  protected readonly montoCierre = signal(0);
  protected readonly observacionesCierre = signal('');

  protected readonly cliente = signal('Cliente libre');
  protected readonly destinoVenta = signal('libre');
  protected readonly destinoMenuOpen = signal(false);
  protected readonly mesas = Array.from({ length: 10 }, (_, index) => index + 1);
  protected readonly metodoPago = signal('efectivo');
  protected readonly pagaCon = signal(0);

  protected readonly esEfectivo = computed(() => this.metodoPago() === 'efectivo');

  protected readonly vuelto = computed(() => {
    if (!this.esEfectivo()) {
      return 0;
    }
    return Math.max(0, this.roundMoney(this.pagaCon() - this.cartTotal()));
  });

  protected readonly faltaPorPagar = computed(() => {
    if (!this.esEfectivo()) {
      return 0;
    }
    return Math.max(0, this.roundMoney(this.cartTotal() - this.pagaCon()));
  });

  protected readonly puedeCobrar = computed(() => {
    if (!this.cart().length || !this.cajaAbierta()) {
      return false;
    }
    if (this.esEfectivo()) {
      return this.pagaCon() >= this.roundMoney(this.cartTotal());
    }
    return true;
  });

  protected readonly metodosPago = [
    { value: 'efectivo', label: 'Efectivo', icon: '💵' },
    { value: 'tarjeta', label: 'Tarjeta', icon: '💳' },
    { value: 'yape', label: 'Yape / Plin', icon: '📱' },
    { value: 'transferencia', label: 'Transferencia', icon: '🏦' },
  ];

  protected readonly cajaAbierta = computed(() => this.cajaSesion()?.estado === 'abierta');

  protected readonly destinoBadge = computed(() => {
    const destino = this.destinoVenta();
    if (destino === 'libre') {
      return { icon: '🧍', label: 'Cliente libre', hint: 'Para llevar / mostrador' };
    }
    if (destino === 'delivery') {
      return { icon: '🛵', label: 'Delivery', hint: 'Pedido a domicilio' };
    }
    if (destino.startsWith('mesa:')) {
      const numero = destino.split(':')[1];
      return { icon: '🪑', label: `Mesa ${numero}`, hint: 'Servicio en salon' };
    }
    return { icon: '🧍', label: this.cliente(), hint: 'Destino de venta' };
  });

  protected readonly categoryChips = computed<CategoryChip[]>(() => {
    const icons: Record<string, string> = {
      'Bebidas calientes': '☕',
      'Bebidas frias': '🧊',
      Postres: '🍰',
      Sandwiches: '🥪',
      Panaderia: '🥐',
      Desayunos: '🍳',
      Ensaladas: '🥗',
      Snacks: '🍿',
    };

    return [
      { value: 'todos', label: 'Todo', icon: '✨' },
      ...this.categories().map((category) => ({
        value: category,
        label: category,
        icon: icons[category] ?? '📦',
      })),
    ];
  });

  protected readonly categories = computed(() =>
    Array.from(new Set(this.productos().map((item) => item.categoria))).sort((a, b) => a.localeCompare(b)),
  );

  protected readonly filteredProducts = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();
    const category = this.selectedCategory();

    return this.productos().filter((product) => {
      const matchesTerm = term
        ? [product.nombre, product.categoria, product.sku, product.codigoBarras]
            .filter(Boolean)
            .some((value) => String(value).toLowerCase().includes(term))
        : true;

      return matchesTerm && (category === 'todos' || product.categoria === category);
    });
  });

  protected readonly cartTotal = computed(() =>
    this.cart().reduce((sum, item) => sum + item.precio * item.cantidad, 0),
  );

  protected readonly cartCount = computed(() =>
    this.cart().reduce((sum, item) => sum + item.cantidad, 0),
  );

  protected readonly cartQtyMap = computed(() => {
    const map = new Map<number, number>();
    for (const item of this.cart()) {
      map.set(item.productoId, item.cantidad);
    }
    return map;
  });

  protected readonly cajeroLabel = computed(() => {
    const session = this.auth.session();
    return session?.userName ?? 'Cajero';
  });

  protected readonly cajeroRole = computed(() => {
    const session = this.auth.session();
    return session?.role ?? 'CAJERO';
  });

  ngOnInit(): void {
    this.updateClock();
    this.clockTimer = setInterval(() => this.updateClock(), 30_000);
    this.applyMesaFromRoute();
    this.loadProducts();
    this.loadCaja();
  }

  private applyMesaFromRoute(): void {
    const mesaParam = this.route.snapshot.queryParamMap.get('mesa');
    if (!mesaParam) {
      return;
    }
    const numero = Number(mesaParam);
    if (!Number.isFinite(numero) || numero < 1 || numero > 10) {
      return;
    }
    this.cliente.set(`Mesa ${numero}`);
    this.destinoVenta.set(`mesa:${numero}`);
  }

  ngOnDestroy(): void {
    if (this.clockTimer) {
      clearInterval(this.clockTimer);
    }
  }

  protected updateSearch(event: Event): void {
    this.searchTerm.set((event.target as HTMLInputElement).value);
  }

  protected selectCategory(value: string): void {
    this.selectedCategory.set(value);
  }

  protected cartQty(productId: number): number {
    return this.cartQtyMap().get(productId) ?? 0;
  }

  protected toggleCajaMenu(): void {
    this.cajaPanel.update((current) => (current === 'menu' ? null : 'menu'));
  }

  protected openAperturaPanel(): void {
    this.cajaPanel.set('apertura');
    this.montoApertura.set(0);
  }

  protected openCierrePanel(): void {
    const sesion = this.cajaSesion();
    this.cajaPanel.set('cierre');
    this.montoCierre.set(sesion?.efectivoEnCaja ?? 0);
    this.observacionesCierre.set('');
  }

  protected closeCajaPanel(): void {
    this.cajaPanel.set(null);
  }

  protected abrirCaja(): void {
    const monto = this.montoApertura();
    if (monto < 0) {
      this.errorMessage.set('El monto inicial no puede ser negativo.');
      return;
    }

    this.cajaSubmitting.set(true);
    this.errorMessage.set('');

    this.api.abrirCaja({ montoInicial: monto }).subscribe({
      next: (sesion) => {
        this.cajaSesion.set(sesion);
        this.cajaSubmitting.set(false);
        this.cajaPanel.set(null);
        this.successMessage.set(`Caja abierta con ${this.formatMoney(sesion.montoInicial)} de fondo inicial.`);
      },
      error: (error) => {
        this.cajaSubmitting.set(false);
        this.errorMessage.set(this.extractError(error));
      },
    });
  }

  protected cerrarCaja(): void {
    const monto = this.montoCierre();
    if (monto < 0) {
      this.errorMessage.set('El monto de cierre no puede ser negativo.');
      return;
    }

    this.cajaSubmitting.set(true);
    this.errorMessage.set('');

    this.api
      .cerrarCaja({
        montoCierre: monto,
        observaciones: this.observacionesCierre().trim() || undefined,
      })
      .subscribe({
        next: () => {
          this.cajaSesion.set(null);
          this.cajaSubmitting.set(false);
          this.cajaPanel.set(null);
          this.cart.set([]);
          this.successMessage.set(`Caja cerrada. Monto contado: ${this.formatMoney(monto)}.`);
        },
        error: (error) => {
          this.cajaSubmitting.set(false);
          this.errorMessage.set(this.extractError(error));
        },
      });
  }

  protected addToCart(product: Producto): void {
    if (!this.cajaAbierta()) {
      this.errorMessage.set('Abre caja con un monto inicial antes de vender.');
      this.openAperturaPanel();
      return;
    }

    this.successMessage.set('');
    this.errorMessage.set('');

    const current = this.cart();
    const existing = current.find((item) => item.productoId === product.id);

    if (existing) {
      this.cart.set(
        current.map((item) =>
          item.productoId === product.id ? { ...item, cantidad: item.cantidad + 1 } : item,
        ),
      );
      return;
    }

    this.cart.set([
      ...current,
      {
        productoId: product.id,
        nombre: product.nombre,
        categoria: product.categoria,
        precio: product.precio,
        cantidad: 1,
        imagenUrl: product.imagenUrl,
      },
    ]);
    this.syncPagaCon();
  }

  protected increment(item: PosCartItem): void {
    this.cart.set(
      this.cart().map((row) =>
        row.productoId === item.productoId ? { ...row, cantidad: row.cantidad + 1 } : row,
      ),
    );
    this.syncPagaCon();
  }

  protected decrement(item: PosCartItem): void {
    this.cart.set(
      this.cart()
        .map((row) =>
          row.productoId === item.productoId ? { ...row, cantidad: row.cantidad - 1 } : row,
        )
        .filter((row) => row.cantidad > 0),
    );
    this.syncPagaCon();
  }

  protected removeItem(item: PosCartItem): void {
    this.cart.set(this.cart().filter((row) => row.productoId !== item.productoId));
    this.syncPagaCon();
  }

  protected clearCart(): void {
    this.cart.set([]);
    this.pagaCon.set(0);
    this.successMessage.set('');
    this.errorMessage.set('');
  }

  protected setMetodoPago(value: string): void {
    this.metodoPago.set(value);
    if (value === 'efectivo') {
      this.syncPagaCon();
    }
  }

  protected setDestinoVenta(value: string): void {
    this.destinoVenta.set(value);
    if (value === 'libre') {
      this.cliente.set('Cliente libre');
      return;
    }
    if (value === 'delivery') {
      this.cliente.set('Delivery');
      return;
    }
    if (value.startsWith('mesa:')) {
      const numero = value.split(':')[1];
      this.cliente.set(`Mesa ${numero}`);
    }
  }

  protected toggleDestinoMenu(): void {
    this.destinoMenuOpen.update((open) => !open);
  }

  protected closeDestinoMenu(): void {
    this.destinoMenuOpen.set(false);
  }

  protected pickDestino(value: string): void {
    this.setDestinoVenta(value);
    this.destinoMenuOpen.set(false);
  }

  protected isDestinoSelected(value: string): boolean {
    return this.destinoVenta() === value;
  }

  protected setPagaCon(value: string | number): void {
    const parsed = typeof value === 'number' ? value : Number.parseFloat(String(value));
    this.pagaCon.set(Number.isFinite(parsed) && parsed >= 0 ? this.roundMoney(parsed) : 0);
  }

  protected usarMontoExacto(): void {
    this.pagaCon.set(this.roundMoney(this.cartTotal()));
  }

  protected agregarBillete(monto: number): void {
    this.pagaCon.set(this.roundMoney(this.pagaCon() + monto));
  }

  protected checkout(): void {
    if (!this.cajaAbierta()) {
      this.errorMessage.set('Abre caja antes de cobrar.');
      this.openAperturaPanel();
      return;
    }

    if (!this.cart().length) {
      this.errorMessage.set('Agrega productos al carrito antes de cobrar.');
      return;
    }

    if (this.esEfectivo() && this.faltaPorPagar() > 0) {
      this.errorMessage.set(
        `Falta ${this.formatMoney(this.faltaPorPagar())}. El cliente debe pagar al menos el total.`,
      );
      return;
    }

    this.submitting.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    this.api
      .posCheckout({
        cliente: this.cliente().trim() || 'Cliente libre',
        metodoPago: this.metodoPago(),
        lineas: this.cart().map((item) => ({
          productoId: item.productoId,
          cantidad: item.cantidad,
        })),
      })
      .subscribe({
        next: (result) => {
          this.submitting.set(false);
          const vueltoEntregado = this.esEfectivo() ? this.vuelto() : 0;
          this.cart.set([]);
          this.pagaCon.set(0);
          this.refreshCajaSesion();
          let message = `Pedido #${result.pedidoId} registrado · ${this.formatMoney(result.total)}`;
          if (vueltoEntregado > 0) {
            message += ` · Vuelto: ${this.formatMoney(vueltoEntregado)}`;
          }
          if (result.insumosDescontados > 0) {
            message += ` · ${result.insumosDescontados} insumo(s) descontados`;
          }
          if (result.productosSinReceta?.length) {
            message += `. Sin receta: ${result.productosSinReceta.join(', ')}`;
          }
          this.successMessage.set(message);
        },
        error: (error) => {
          this.submitting.set(false);
          this.errorMessage.set(this.extractError(error));
        },
      });
  }

  protected formatMoney(value: number): string {
    return new Intl.NumberFormat('es-PE', {
      style: 'currency',
      currency: 'PEN',
      minimumFractionDigits: 2,
    }).format(value ?? 0);
  }

  private loadProducts(): void {
    this.loading.set(true);
    this.api.productosActivos().subscribe({
      next: (productos) => {
        this.productos.set(productos);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('No se pudo cargar el catalogo de productos.');
        this.loading.set(false);
      },
    });
  }

  private loadCaja(): void {
    this.cajaLoading.set(true);
    this.api
      .cajaActiva()
      .pipe(map((response) => (response.status === 204 ? null : response.body)))
      .subscribe({
        next: (sesion) => {
          this.cajaSesion.set(sesion ?? null);
          this.cajaLoading.set(false);
          if (!sesion) {
            this.cajaPanel.set('apertura');
          }
        },
        error: () => {
          this.cajaLoading.set(false);
          this.errorMessage.set('No se pudo consultar el estado de caja.');
        },
      });
  }

  private refreshCajaSesion(): void {
    this.api
      .cajaActiva()
      .pipe(map((response) => (response.status === 204 ? null : response.body)))
      .subscribe({
        next: (sesion) => this.cajaSesion.set(sesion ?? null),
      });
  }

  private updateClock(): void {
    this.nowLabel.set(
      new Intl.DateTimeFormat('es-PE', {
        weekday: 'short',
        day: '2-digit',
        month: 'short',
        hour: '2-digit',
        minute: '2-digit',
      }).format(new Date()),
    );
  }

  private extractError(error: unknown): string {
    const response = error as { error?: { message?: string }; status?: number };
    if (response.status === 403) {
      return 'No tienes permiso para operar la caja. Solo ADMIN y CAJERO.';
    }
    return response.error?.message || 'No se pudo completar la operacion.';
  }

  private syncPagaCon(): void {
    if (!this.esEfectivo() || !this.cart().length) {
      return;
    }
    const total = this.roundMoney(this.cartTotal());
    if (this.pagaCon() < total) {
      this.pagaCon.set(total);
    }
  }

  private roundMoney(value: number): number {
    return Math.round((value + Number.EPSILON) * 100) / 100;
  }
}
