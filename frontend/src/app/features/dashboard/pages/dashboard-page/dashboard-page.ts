import { Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { forkJoin } from 'rxjs';

import { AuthService } from '../../../../core/auth/auth.service';
import { PageHeader } from '../../../../shared/components/page-header/page-header';
import { normalizeUnidad } from '../../../../core/utils/unidad.util';
import { CafederonelApiService } from '../../../../services/cafederonel-api.service';
import { InventarioItem } from '../../../../models/inventario.model';
import { Pedido } from '../../../../models/pedido.model';
import { Producto } from '../../../../models/producto.model';
import { Proveedor } from '../../../../models/proveedor.model';
import { ResumenFinanciero } from '../../../../models/reporte.model';
import { Venta } from '../../../../models/venta.model';
import { Usuario } from '../../../../models/usuario.model';

type Metric = {
  label: string;
  value: string;
  detail: string;
  tone: 'green' | 'amber' | 'blue' | 'gray';
};

type StockAlert = {
  item: string;
  unit: string;
  available: number;
  minimum: number;
  width: number;
};

type ModuleSummary = {
  name: string;
  count: string;
  detail: string;
};

type ActivityItem = {
  title: string;
  detail: string;
  type: 'ok' | 'warning' | 'info';
};

@Component({
  selector: 'app-dashboard-page',
  imports: [PageHeader],
  templateUrl: './dashboard-page.html',
  styleUrl: './dashboard-page.scss',
})
export class DashboardPage {
  private readonly api = inject(CafederonelApiService);
  private readonly auth = inject(AuthService);

  protected readonly loading = signal(true);
  protected readonly errorMessage = signal('');
  protected readonly metrics = signal<Metric[]>([]);
  protected readonly lowStock = signal<StockAlert[]>([]);
  protected readonly activity = signal<ActivityItem[]>([]);
  protected readonly moduleSummaries = signal<ModuleSummary[]>([]);
  protected readonly header = computed(() => {
    const role = this.auth.session()?.role;
    if (role === 'CAJERO') {
      return {
        eyebrow: 'Panel de caja',
        title: 'Dashboard limitado',
        description: 'POS, pedidos y productos necesarios para la atencion diaria.',
      };
    }
    if (role === 'INVENTARIO') {
      return {
        eyebrow: 'Panel operativo',
        title: 'Dashboard operativo',
        description: 'Inventario, Kardex, proveedores y productos bajo control.',
      };
    }
    if (role === 'CONTADOR') {
      return {
        eyebrow: 'Panel financiero',
        title: 'Dashboard financiero',
        description: 'Ventas, pedidos, cobros y resumen financiero del negocio.',
      };
    }
    return {
      eyebrow: 'Panel principal',
      title: 'Dashboard administrativo',
      description: 'Resumen general de productos, stock, ventas, usuarios y proveedores.',
    };
  });

  constructor() {
    this.loadByRole();
  }

  private loadByRole(): void {
    const role = this.auth.session()?.role;

    if (role === 'CAJERO') {
      this.loadCajeroDashboard();
      return;
    }

    if (role === 'INVENTARIO') {
      this.loadInventarioDashboard();
      return;
    }

    if (role === 'CONTADOR') {
      this.loadContadorDashboard();
      return;
    }

    this.loadAdminDashboard();
  }

  private loadAdminDashboard(): void {
    forkJoin({
      productos: this.api.productos(),
      inventario: this.api.inventario(),
      pedidos: this.api.pedidos(),
      ventas: this.api.ventas(),
      usuarios: this.api.usuarios(),
      proveedores: this.api.proveedores(),
    })
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: ({ productos, inventario, pedidos, ventas, usuarios, proveedores }) => {
          this.metrics.set(this.buildMetrics(productos, inventario, ventas, usuarios));
          this.moduleSummaries.set(
            this.buildModuleSummaries(productos, inventario, proveedores, pedidos, ventas, usuarios),
          );
          this.lowStock.set(this.buildStockAlerts(inventario));
          this.activity.set(this.buildActivity(pedidos, ventas, inventario));
          this.loading.set(false);
        },
        error: () => this.failDashboard(),
      });
  }

  private loadCajeroDashboard(): void {
    forkJoin({
      productos: this.api.productosActivos(),
      pedidos: this.api.pedidos(),
      ventas: this.api.ventas(),
    })
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: ({ productos, pedidos, ventas }) => {
          const ventasCompletadas = ventas.filter((venta) => venta.estado === 'completado');
          const totalVentas = ventasCompletadas.reduce((sum, venta) => sum + Number(venta.total), 0);
          this.metrics.set([
            { label: 'Productos disponibles', value: String(productos.length), detail: 'Catalogo para venta', tone: 'green' },
            { label: 'Pedidos', value: String(pedidos.length), detail: 'Pedidos registrados', tone: 'blue' },
            { label: 'Ventas completadas', value: String(ventasCompletadas.length), detail: this.formatMoney(totalVentas), tone: 'amber' },
            { label: 'Total cobrado', value: this.formatMoney(totalVentas), detail: 'Ventas finalizadas', tone: 'gray' },
          ]);
          this.moduleSummaries.set([
            { name: 'Productos', count: String(productos.length), detail: 'Activos para POS' },
            { name: 'Pedidos', count: String(pedidos.length), detail: 'Correcciones operativas' },
            { name: 'Ventas', count: String(ventas.length), detail: 'Cobros registrados' },
          ]);
          this.lowStock.set([]);
          this.activity.set(this.buildActivity(pedidos, ventas, []));
          this.loading.set(false);
        },
        error: () => this.failDashboard(),
      });
  }

  private loadInventarioDashboard(): void {
    forkJoin({
      productos: this.api.productos(),
      inventario: this.api.inventario(),
      proveedores: this.api.proveedores(),
    })
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: ({ productos, inventario, proveedores }) => {
          const productosActivos = productos.filter((producto) => producto.activo).length;
          const alertasStock = inventario.filter((item) => item.cantidad <= item.stockMinimo).length;
          const proveedoresActivos = proveedores.filter((proveedor) => proveedor.activo).length;
          const valorStock = inventario.reduce((sum, item) => sum + item.cantidad * item.precioUnitario, 0);
          this.metrics.set([
            { label: 'Productos activos', value: String(productosActivos), detail: `${productos.length} en catalogo`, tone: 'green' },
            { label: 'Alertas de stock', value: String(alertasStock), detail: `${inventario.length} insumos`, tone: 'amber' },
            { label: 'Proveedores activos', value: String(proveedoresActivos), detail: `${proveedores.length} registrados`, tone: 'blue' },
            { label: 'Valor de stock', value: this.formatMoney(valorStock), detail: 'Inventario valorizado', tone: 'gray' },
          ]);
          this.moduleSummaries.set([
            { name: 'Productos', count: String(productos.length), detail: `${productosActivos} activos en catalogo` },
            { name: 'Inventario', count: String(inventario.length), detail: `${alertasStock} alertas` },
            { name: 'Kardex', count: String(this.buildStockAlerts(inventario).length), detail: 'Insumos sensibles' },
            { name: 'Proveedores', count: String(proveedores.length), detail: `${proveedoresActivos} activos` },
          ]);
          this.lowStock.set(this.buildStockAlerts(inventario));
          this.activity.set(this.buildActivity([], [], inventario));
          this.loading.set(false);
        },
        error: () => this.failDashboard(),
      });
  }

  private loadContadorDashboard(): void {
    forkJoin({
      pedidos: this.api.pedidos(),
      ventas: this.api.ventas(),
      resumen: this.api.resumenFinanciero(),
    })
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: ({ pedidos, ventas, resumen }) => {
          this.metrics.set(this.buildFinancialMetrics(resumen));
          this.moduleSummaries.set([
            { name: 'Ventas', count: String(ventas.length), detail: this.formatMoney(resumen.totalCobrado) },
            { name: 'Pedidos', count: String(pedidos.length), detail: `${resumen.pedidosPendientes} pendientes` },
            { name: 'Cobros', count: String(resumen.ultimosCobros.length), detail: 'Ultimos movimientos' },
            { name: 'Reportes', count: String(resumen.cobrosPorMetodo.length), detail: 'Metodos de pago' },
          ]);
          this.lowStock.set([]);
          this.activity.set(this.buildFinancialActivity(resumen, pedidos, ventas));
          this.loading.set(false);
        },
        error: () => this.failDashboard(),
      });
  }

  private buildMetrics(
    productos: Producto[],
    inventario: InventarioItem[],
    ventas: Venta[],
    usuarios: Usuario[],
  ): Metric[] {
    const productosActivos = productos.filter((producto) => producto.activo).length;
    const alertasStock = inventario.filter((item) => item.cantidad <= item.stockMinimo).length;
    const ventasCompletadas = ventas.filter((venta) => venta.estado === 'completado');
    const totalVentas = ventasCompletadas.reduce((sum, venta) => sum + Number(venta.total), 0);
    const administradores = usuarios.filter((usuario) => usuario.rol === 'ADMIN').length;

    return [
      { label: 'Productos activos', value: String(productosActivos), detail: `${productos.length} en catalogo`, tone: 'green' },
      { label: 'Alertas de stock', value: String(alertasStock), detail: `${inventario.length} insumos controlados`, tone: 'amber' },
      { label: 'Ventas registradas', value: String(ventas.length), detail: this.formatMoney(totalVentas), tone: 'blue' },
      { label: 'Usuarios', value: String(usuarios.length), detail: `${administradores} administradores`, tone: 'gray' },
    ];
  }

  private buildModuleSummaries(
    productos: Producto[],
    inventario: InventarioItem[],
    proveedores: Proveedor[],
    pedidos: Pedido[],
    ventas: Venta[],
    usuarios: Usuario[],
  ): ModuleSummary[] {
    const productosActivos = productos.filter((producto) => producto.activo).length;
    const insumosActivos = inventario.filter((item) => item.activo).length;
    const proveedoresActivos = proveedores.filter((proveedor) => proveedor.activo).length;

    return [
      { name: 'Productos', count: String(productos.length), detail: `${productosActivos} activos en catalogo` },
      { name: 'Inventario', count: String(inventario.length), detail: `${insumosActivos} insumos activos` },
      { name: 'Proveedores', count: String(proveedores.length), detail: `${proveedoresActivos} activos para compras` },
      { name: 'Pedidos', count: String(pedidos.length), detail: 'Registros en base de datos' },
      { name: 'Ventas', count: String(ventas.length), detail: 'Transacciones registradas' },
      { name: 'Usuarios', count: String(usuarios.length), detail: 'Cuentas del sistema' },
    ];
  }

  private buildStockAlerts(inventario: InventarioItem[]): StockAlert[] {
    return inventario
      .filter((item) => item.cantidad <= item.stockMinimo)
      .slice(0, 4)
      .map((item) => ({
        item: item.nombreInsumo,
        unit: normalizeUnidad(item.unidad),
        available: item.cantidad,
        minimum: item.stockMinimo,
        width: item.stockMinimo > 0 ? Math.min(100, Math.round((item.cantidad / item.stockMinimo) * 100)) : 100,
      }));
  }

  private buildActivity(pedidos: Pedido[], ventas: Venta[], inventario: InventarioItem[]): ActivityItem[] {
    const latestPedido = [...pedidos].sort((a, b) => b.id - a.id)[0];
    const latestVenta = [...ventas].sort((a, b) => b.id - a.id)[0];
    const criticalStock = inventario.find((item) => item.cantidad <= item.stockMinimo);
    const activity: ActivityItem[] = [];

    if (latestPedido) {
      activity.push({ title: `Pedido #${latestPedido.id} actualizado`, detail: `Estado ${this.humanizeEstado(latestPedido.estado)}`, type: 'ok' });
    }

    if (inventario.length) {
      activity.push(
        criticalStock
          ? { title: 'Stock bajo detectado', detail: `${criticalStock.nombreInsumo} debajo del minimo`, type: 'warning' }
          : { title: 'Inventario estable', detail: 'No hay alertas de stock bajo', type: 'ok' },
      );
    }

    if (latestVenta) {
      activity.push({ title: 'Venta registrada', detail: `Total ${this.formatMoney(latestVenta.total)}`, type: 'info' });
    }

    if (!activity.length) {
      activity.push({ title: 'Sistema listo', detail: 'Sin movimientos registrados aun', type: 'info' });
    }

    return activity;
  }

  private buildFinancialMetrics(resumen: ResumenFinanciero): Metric[] {
    return [
      { label: 'Total cobrado', value: this.formatMoney(resumen.totalCobrado), detail: `${resumen.ventasCompletadas} ventas`, tone: 'green' },
      { label: 'Ticket promedio', value: this.formatMoney(resumen.ticketPromedio), detail: 'Ventas completadas', tone: 'blue' },
      { label: 'Pedidos pendientes', value: String(resumen.pedidosPendientes), detail: `${resumen.pedidosRegistrados} pedidos`, tone: 'amber' },
      { label: 'Cajas abiertas', value: String(resumen.cajasAbiertas), detail: `${resumen.cajasCerradas} cerradas`, tone: 'gray' },
    ];
  }

  private buildFinancialActivity(resumen: ResumenFinanciero, pedidos: Pedido[], ventas: Venta[]): ActivityItem[] {
    const latestPedido = [...pedidos].sort((a, b) => b.id - a.id)[0];
    const latestVenta = [...ventas].sort((a, b) => b.id - a.id)[0];
    return [
      { title: 'Cobros confirmados', detail: this.formatMoney(resumen.totalCobrado), type: 'ok' },
      latestPedido
        ? { title: `Pedido #${latestPedido.id}`, detail: `Estado ${this.humanizeEstado(latestPedido.estado)}`, type: 'info' }
        : { title: 'Pedidos', detail: 'Sin pedidos registrados', type: 'info' },
      latestVenta
        ? { title: `Venta #${latestVenta.id}`, detail: `${this.formatMoney(latestVenta.total)} por ${latestVenta.metodoPago || 'sin metodo'}`, type: 'info' }
        : { title: 'Ventas', detail: 'Sin ventas registradas', type: 'info' },
    ];
  }

  private failDashboard(): void {
    this.errorMessage.set('No se pudieron cargar los datos permitidos para este rol.');
    this.loading.set(false);
  }

  private humanizeEstado(estado: string): string {
    return estado.replace(/_/g, ' ');
  }

  private formatMoney(value: number): string {
    return new Intl.NumberFormat('es-PE', {
      style: 'currency',
      currency: 'PEN',
      minimumFractionDigits: 2,
    }).format(value);
  }
}
