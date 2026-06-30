import { Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { interval } from 'rxjs';

import { PageHeader } from '../../../../shared/components/page-header/page-header';
import { AuthService } from '../../../../core/auth/auth.service';
import { CafederonelApiService } from '../../../../services/cafederonel-api.service';
import { Pedido } from '../../../../models/pedido.model';
import { Producto } from '../../../../models/producto.model';

const VENTANA_EDICION_MS = 2 * 60 * 1000;

type EditLine = {
  productoId: number;
  nombre: string;
  precio: number;
  cantidad: number;
};

@Component({
  selector: 'app-pedidos-page',
  imports: [PageHeader],
  templateUrl: './pedidos-page.html',
  styleUrl: './pedidos-page.scss',
})
export class PedidosPage {
  private readonly api = inject(CafederonelApiService);
  private readonly auth = inject(AuthService);

  protected readonly columns = ['Pedido', 'Mesa / servicio', 'Productos', 'Estado', 'Total', 'Fecha', 'Acciones'];
  protected readonly mesas = Array.from({ length: 10 }, (_, index) => index + 1);
  protected readonly rows = signal<Pedido[]>([]);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal('');
  protected readonly actionMessage = signal('');
  protected readonly actionError = signal('');
  protected readonly saving = signal(false);
  protected readonly editPedido = signal<Pedido | null>(null);
  protected readonly editAsAdmin = signal(false);
  protected readonly editCliente = signal('Cliente libre');
  protected readonly editLines = signal<EditLine[]>([]);
  protected readonly catalog = signal<Producto[]>([]);
  protected readonly catalogLoading = signal(false);
  protected readonly selectedProductId = signal<number | null>(null);
  protected readonly addQty = signal(1);
  protected readonly destinoMenuOpen = signal(false);
  protected readonly now = signal(Date.now());
  protected readonly adminPinInput = signal('');
  protected readonly adminPinSession = signal('');
  protected readonly adminUnlocked = signal(false);
  protected readonly adminUnlockError = signal('');

  protected readonly isAdmin = computed(() => this.auth.session()?.role === 'ADMIN');
  protected readonly destinoBadge = computed(() => this.destinoMeta(this.editCliente()));
  protected readonly editTotal = computed(() =>
    this.editLines().reduce((sum, line) => sum + line.precio * line.cantidad, 0),
  );
  protected readonly canSaveEdit = computed(() => this.editLines().length > 0);

  constructor() {
    interval(1000)
      .pipe(takeUntilDestroyed())
      .subscribe(() => this.now.set(Date.now()));

    this.loadPedidos();
  }

  protected estadoLabel(estado: Pedido['estado']): string {
    const labels: Record<Pedido['estado'], string> = {
      pendiente: 'Pendiente',
      en_proceso: 'Preparacion',
      completado: 'Completado',
      cancelado: 'Cancelado',
    };

    return labels[estado];
  }

  protected servicioLabel(cliente: string): string {
    if (cliente === 'Cliente libre') {
      return 'Mostrador';
    }
    return cliente;
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

    return new Intl.DateTimeFormat('es-PE', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    }).format(new Date(value.replace(' ', 'T')));
  }

  protected productosLabel(pedido: Pedido): string {
    if (!pedido.detalles?.length) {
      return 'Sin detalle';
    }

    return pedido.detalles
      .map((detalle) => `${detalle.cantidad}x ${detalle.producto?.nombre ?? 'Producto'}`)
      .join(' · ');
  }

  protected puedeEditar(pedido: Pedido): boolean {
    this.now();
    if (pedido.estado === 'cancelado') {
      return false;
    }
    return this.segundosRestantes(pedido) > 0;
  }

  protected segundosRestantes(pedido: Pedido): number {
    this.now();
    const creado = this.parseFecha(pedido.fechaCreacion);
    if (!creado) {
      return 0;
    }
    const restante = VENTANA_EDICION_MS - (Date.now() - creado.getTime());
    return Math.max(0, Math.ceil(restante / 1000));
  }

  protected countdownLabel(pedido: Pedido): string {
    const segundos = this.segundosRestantes(pedido);
    const minutos = Math.floor(segundos / 60);
    const resto = segundos % 60;
    return `${minutos}:${resto.toString().padStart(2, '0')}`;
  }

  protected puedeCorregirAdmin(pedido: Pedido): boolean {
    return this.isAdmin() && this.adminUnlocked() && pedido.estado !== 'cancelado';
  }

  protected puedeAnularAdmin(pedido: Pedido): boolean {
    return this.isAdmin() && this.adminUnlocked() && pedido.estado !== 'cancelado';
  }

  protected onAdminPinInput(event: Event): void {
    const value = (event.target as HTMLInputElement).value.replace(/\D/g, '').slice(0, 4);
    this.adminPinInput.set(value);
    this.adminUnlockError.set('');
  }

  protected activarModoAdmin(): void {
    const pin = this.adminPinInput().trim();
    if (pin.length !== 4) {
      this.adminUnlockError.set('Ingresa los 4 digitos del PIN.');
      return;
    }

    this.api.verificarPinAdminPedidos({ adminPin: pin }).subscribe({
      next: () => {
        this.adminPinSession.set(pin);
        this.adminUnlocked.set(true);
        this.adminPinInput.set('');
        this.adminUnlockError.set('');
        this.actionMessage.set('Modo supervisor activo. Puedes corregir pedidos sin limite de tiempo.');
      },
      error: (error) => {
        this.adminUnlockError.set(this.extractError(error));
      },
    });
  }

  protected cerrarModoAdmin(): void {
    this.adminUnlocked.set(false);
    this.adminPinSession.set('');
    this.adminPinInput.set('');
    this.adminUnlockError.set('');
    this.closeEdit();
    this.actionMessage.set('Modo supervisor cerrado.');
  }

  protected openEdit(pedido: Pedido, asAdmin = false): void {
    if (asAdmin) {
      if (!this.puedeCorregirAdmin(pedido)) {
        return;
      }
      this.editAsAdmin.set(true);
    } else {
      if (!this.puedeEditar(pedido)) {
        return;
      }
      this.editAsAdmin.set(false);
    }

    this.actionMessage.set('');
    this.actionError.set('');
    this.editPedido.set(pedido);
    this.editCliente.set(pedido.cliente);
    this.editLines.set(
      (pedido.detalles ?? []).map((detalle) => ({
        productoId: detalle.producto.id,
        nombre: detalle.producto?.nombre ?? 'Producto',
        precio: detalle.precio,
        cantidad: detalle.cantidad,
      })),
    );
    this.selectedProductId.set(null);
    this.addQty.set(1);
    this.destinoMenuOpen.set(false);
    this.loadCatalog();
  }

  protected openEditAdmin(pedido: Pedido): void {
    this.openEdit(pedido, true);
  }

  protected closeEdit(): void {
    this.editPedido.set(null);
    this.editAsAdmin.set(false);
    this.destinoMenuOpen.set(false);
  }

  protected toggleDestinoMenu(): void {
    this.destinoMenuOpen.update((open) => !open);
  }

  protected closeDestinoMenu(): void {
    this.destinoMenuOpen.set(false);
  }

  protected isDestinoSelected(tipo: string): boolean {
    const cliente = this.editCliente();
    if (tipo === 'libre') {
      return cliente === 'Cliente libre';
    }
    if (tipo === 'delivery') {
      return cliente === 'Delivery';
    }
    const numero = tipo.split(':')[1];
    return cliente === `Mesa ${numero}`;
  }

  protected pickDestino(tipo: string): void {
    if (tipo === 'libre') {
      this.editCliente.set('Cliente libre');
    } else if (tipo === 'delivery') {
      this.editCliente.set('Delivery');
    } else {
      const numero = tipo.split(':')[1];
      this.editCliente.set(`Mesa ${numero}`);
    }
    this.closeDestinoMenu();
  }

  protected incrementLine(line: EditLine): void {
    this.editLines.update((lines) =>
      lines.map((row) => (row.productoId === line.productoId ? { ...row, cantidad: row.cantidad + 1 } : row)),
    );
  }

  protected decrementLine(line: EditLine): void {
    if (line.cantidad <= 1) {
      this.removeLine(line);
      return;
    }
    this.editLines.update((lines) =>
      lines.map((row) => (row.productoId === line.productoId ? { ...row, cantidad: row.cantidad - 1 } : row)),
    );
  }

  protected removeLine(line: EditLine): void {
    this.editLines.update((lines) => lines.filter((row) => row.productoId !== line.productoId));
  }

  protected onProductSelect(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.selectedProductId.set(value ? Number(value) : null);
  }

  protected onAddQtyChange(event: Event): void {
    const value = Number((event.target as HTMLInputElement).value);
    this.addQty.set(Number.isFinite(value) && value > 0 ? Math.floor(value) : 1);
  }

  protected addProduct(): void {
    const productId = this.selectedProductId();
    if (!productId) {
      return;
    }

    const product = this.catalog().find((row) => row.id === productId);
    if (!product) {
      return;
    }

    const qty = Math.max(1, this.addQty());
    const existing = this.editLines().find((row) => row.productoId === productId);
    if (existing) {
      this.editLines.update((lines) =>
        lines.map((row) =>
          row.productoId === productId ? { ...row, cantidad: row.cantidad + qty, precio: product.precio } : row,
        ),
      );
    } else {
      this.editLines.update((lines) => [
        ...lines,
        {
          productoId: product.id,
          nombre: product.nombre,
          precio: product.precio,
          cantidad: qty,
        },
      ]);
    }

    this.selectedProductId.set(null);
    this.addQty.set(1);
  }

  protected saveEdit(): void {
    const pedido = this.editPedido();
    if (!pedido || this.saving() || !this.canSaveEdit()) {
      return;
    }

    this.saving.set(true);
    this.actionError.set('');

    const payload = {
      cliente: this.editCliente().trim(),
      detalles: this.editLines().map((line) => ({
        productoId: line.productoId,
        cantidad: line.cantidad,
      })),
    };

    const request$ = this.editAsAdmin()
      ? this.api.actualizarPedidoAdmin(pedido.id, {
          ...payload,
          adminPin: this.adminPinSession(),
        })
      : this.api.actualizarPedido(pedido.id, payload);

    const wasAdmin = this.editAsAdmin();

    request$.subscribe({
      next: (actualizado) => {
        this.replacePedido(actualizado);
        this.saving.set(false);
        this.closeEdit();
        this.actionMessage.set(
          wasAdmin
            ? `Pedido #${actualizado.id} corregido por administrador.`
            : `Pedido #${actualizado.id} corregido correctamente.`,
        );
      },
      error: (error) => {
        this.saving.set(false);
        const message = this.extractError(error);
        if (this.editAsAdmin() && message.toLowerCase().includes('pin')) {
          this.cerrarModoAdmin();
        }
        this.actionError.set(message);
      },
    });
  }

  protected cancelarPedido(pedido: Pedido, asAdmin = false): void {
    if (asAdmin) {
      if (!this.puedeAnularAdmin(pedido) || this.saving()) {
        return;
      }
    } else if (!this.puedeEditar(pedido) || this.saving()) {
      return;
    }

    const confirmacion = window.confirm(
      asAdmin
        ? `¿Anular pedido #${pedido.id} como administrador?\n\nEsta accion no tiene limite de tiempo.`
        : `¿Anular pedido #${pedido.id} por ${this.formatMoney(pedido.total)}?\n\nUsa esto si la venta fue un error completo. Solo disponible 2 minutos.`,
    );
    if (!confirmacion) {
      return;
    }

    this.saving.set(true);
    this.actionMessage.set('');
    this.actionError.set('');

    const request$ = asAdmin
      ? this.api.cancelarPedidoAdmin(pedido.id, { adminPin: this.adminPinSession() })
      : this.api.cancelarPedido(pedido.id);

    request$.subscribe({
      next: (cancelado) => {
        this.replacePedido(cancelado);
        this.saving.set(false);
        this.actionMessage.set(
          asAdmin ? `Pedido #${cancelado.id} anulado por administrador.` : `Pedido #${cancelado.id} anulado.`,
        );
      },
      error: (error) => {
        this.saving.set(false);
        const message = this.extractError(error);
        if (asAdmin && message.toLowerCase().includes('pin')) {
          this.cerrarModoAdmin();
        }
        this.actionError.set(message);
      },
    });
  }

  protected cancelarPedidoAdmin(pedido: Pedido): void {
    this.cancelarPedido(pedido, true);
  }

  private loadPedidos(): void {
    this.loading.set(true);
    this.api
      .pedidos()
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: (pedidos) => {
          this.rows.set(pedidos);
          this.loading.set(false);
        },
        error: () => {
          this.errorMessage.set('No se pudieron cargar pedidos desde la base de datos.');
          this.loading.set(false);
        },
      });
  }

  private loadCatalog(): void {
    if (this.catalog().length || this.catalogLoading()) {
      return;
    }

    this.catalogLoading.set(true);
    this.api
      .productosActivos()
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: (productos) => {
          this.catalog.set(productos.sort((a, b) => a.nombre.localeCompare(b.nombre, 'es')));
          this.catalogLoading.set(false);
        },
        error: () => {
          this.catalogLoading.set(false);
          this.actionError.set('No se pudo cargar el catalogo para agregar productos.');
        },
      });
  }

  private replacePedido(actualizado: Pedido): void {
    this.rows.update((pedidos) => pedidos.map((pedido) => (pedido.id === actualizado.id ? actualizado : pedido)));
  }

  private parseFecha(value?: string): Date | null {
    if (!value) {
      return null;
    }
    const parsed = new Date(value.replace(' ', 'T'));
    return Number.isNaN(parsed.getTime()) ? null : parsed;
  }

  private destinoMeta(cliente: string): { icon: string; label: string; hint: string } {
    if (cliente === 'Cliente libre') {
      return { icon: '🧍', label: 'Mostrador', hint: 'Para llevar / sin mesa' };
    }
    if (cliente === 'Delivery') {
      return { icon: '🛵', label: 'Delivery', hint: 'Pedido a domicilio' };
    }
    if (cliente.startsWith('Mesa ')) {
      return { icon: '🪑', label: cliente, hint: 'Corregir mesa del salon' };
    }
    return { icon: '📍', label: cliente, hint: 'Servicio personalizado' };
  }

  private extractError(error: { error?: { message?: string } }): string {
    return error?.error?.message ?? 'No se pudo completar la accion. Intenta de nuevo.';
  }
}
