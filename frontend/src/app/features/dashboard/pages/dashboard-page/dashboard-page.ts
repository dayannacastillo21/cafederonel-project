import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { forkJoin } from 'rxjs';

import { PageHeader } from '../../../../shared/components/page-header/page-header';
import { normalizeUnidad } from '../../../../core/utils/unidad.util';
import { CafederonelApiService } from '../../../../services/cafederonel-api.service';
import { InventarioItem } from '../../../../models/inventario.model';
import { Pedido } from '../../../../models/pedido.model';
import { Producto } from '../../../../models/producto.model';
import { Proveedor } from '../../../../models/proveedor.model';
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

  protected readonly loading = signal(true);
  protected readonly errorMessage = signal('');
  protected readonly metrics = signal<Metric[]>([]);
  protected readonly lowStock = signal<StockAlert[]>([]);
  protected readonly activity = signal<ActivityItem[]>([]);

  protected readonly moduleSummaries = signal<ModuleSummary[]>([]);

  constructor() {
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
          this.moduleSummaries.set(this.buildModuleSummaries(productos, inventario, proveedores, pedidos, ventas, usuarios));
          this.lowStock.set(this.buildStockAlerts(inventario));
          this.activity.set(this.buildActivity(pedidos, ventas, inventario));
          this.loading.set(false);
        },
        error: () => {
          this.errorMessage.set('No se pudieron cargar los datos desde el backend.');
          this.loading.set(false);
        },
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

    return [
      latestPedido
        ? { title: `Pedido #${latestPedido.id} actualizado`, detail: `Estado ${this.humanizeEstado(latestPedido.estado)}`, type: 'ok' }
        : { title: 'Pedidos listos para operar', detail: 'Sin movimientos registrados aun', type: 'info' },
      criticalStock
        ? { title: 'Stock bajo detectado', detail: `${criticalStock.nombreInsumo} debajo del minimo`, type: 'warning' }
        : { title: 'Inventario estable', detail: 'No hay alertas de stock bajo', type: 'ok' },
      latestVenta
        ? { title: 'Venta registrada', detail: `Total ${this.formatMoney(latestVenta.total)}`, type: 'info' }
        : { title: 'Ventas listas para operar', detail: 'Sin ventas registradas aun', type: 'info' },
    ];
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
