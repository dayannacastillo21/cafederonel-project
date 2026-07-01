import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { API_BASE_URL } from '../core/api/api.config';
import {
  InventarioItem,
  InventarioPayload,
  MovimientoInventario,
  MovimientoInventarioPayload,
} from '../models/inventario.model';
import { Pedido, PedidoAdminPinPayload, PedidoAdminUpdatePayload, PedidoUpdatePayload } from '../models/pedido.model';
import { PosCheckoutPayload, PosCheckoutResult } from '../models/pos.model';
import { CajaAperturaPayload, CajaCierrePayload, CajaSesion } from '../models/caja.model';
import { Producto, ProductoPayload } from '../models/producto.model';
import { ProductoStockDisponible } from '../models/producto-stock.model';
import { Proveedor, ProveedorPayload } from '../models/proveedor.model';
import { ResumenFinanciero } from '../models/reporte.model';
import { Almacen, CategoriaProducto } from '../models/catalogo.model';
import { Usuario, UsuarioCreatePayload, UsuarioUpdatePayload } from '../models/usuario.model';
import { Venta } from '../models/venta.model';
import { SalonMesa } from '../models/salon.model';

@Injectable({ providedIn: 'root' })
export class CafederonelApiService {
  private readonly http = inject(HttpClient);

  productos() {
    return this.http.get<Producto[]>(`${API_BASE_URL}/productos`);
  }

  productosActivos() {
    return this.http.get<Producto[]>(`${API_BASE_URL}/productos/activos`);
  }

  productosStockVendible() {
    return this.http.get<ProductoStockDisponible[]>(`${API_BASE_URL}/productos/stock-vendible`);
  }

  buscarProductos(q: string) {
    return this.http.get<Producto[]>(`${API_BASE_URL}/productos/busqueda`, { params: { q } });
  }

  crearProducto(payload: ProductoPayload) {
    return this.http.post<Producto>(`${API_BASE_URL}/productos`, payload);
  }

  actualizarProducto(id: number, payload: ProductoPayload) {
    return this.http.put<Producto>(`${API_BASE_URL}/productos/${id}`, payload);
  }

  cambiarEstadoProducto(id: number, activo: boolean) {
    return this.http.patch<Producto>(`${API_BASE_URL}/productos/${id}/estado`, { activo });
  }

  eliminarProducto(id: number) {
    return this.http.delete<void>(`${API_BASE_URL}/productos/${id}`);
  }

  inventario(q?: string) {
    const options = q?.trim() ? { params: { q: q.trim() } } : {};
    return this.http.get<InventarioItem[]>(`${API_BASE_URL}/inventario`, options);
  }

  stockBajo() {
    return this.http.get<InventarioItem[]>(`${API_BASE_URL}/inventario/alertas/stock-bajo`);
  }

  crearInventario(payload: InventarioPayload) {
    return this.http.post<InventarioItem>(`${API_BASE_URL}/inventario`, payload);
  }

  actualizarInventario(id: number, payload: InventarioPayload) {
    return this.http.put<InventarioItem>(`${API_BASE_URL}/inventario/${id}`, payload);
  }

  cambiarEstadoInventario(id: number, activo: boolean) {
    return this.http.patch<InventarioItem>(`${API_BASE_URL}/inventario/${id}/estado`, { activo });
  }

  eliminarInventario(id: number) {
    return this.http.delete<void>(`${API_BASE_URL}/inventario/${id}`);
  }

  movimientosInventario(id: number) {
    return this.http.get<MovimientoInventario[]>(`${API_BASE_URL}/inventario/${id}/movimientos`);
  }

  ultimosMovimientosInventario() {
    return this.http.get<MovimientoInventario[]>(`${API_BASE_URL}/inventario/movimientos`);
  }

  registrarMovimientoInventario(id: number, payload: MovimientoInventarioPayload) {
    return this.http.post<MovimientoInventario>(`${API_BASE_URL}/inventario/${id}/movimientos`, payload);
  }

  proveedores() {
    return this.http.get<Proveedor[]>(`${API_BASE_URL}/proveedores`);
  }

  crearProveedor(payload: ProveedorPayload) {
    return this.http.post<Proveedor>(`${API_BASE_URL}/proveedores`, payload);
  }

  actualizarProveedor(id: number, payload: ProveedorPayload) {
    return this.http.put<Proveedor>(`${API_BASE_URL}/proveedores/${id}`, payload);
  }

  cambiarEstadoProveedor(id: number, activo: boolean) {
    return this.http.patch<Proveedor>(`${API_BASE_URL}/proveedores/${id}/estado`, { activo });
  }

  eliminarProveedor(id: number) {
    return this.http.delete<void>(`${API_BASE_URL}/proveedores/${id}`);
  }

  pedidos() {
    return this.http.get<Pedido[]>(`${API_BASE_URL}/pedidos`);
  }

  actualizarPedidoCliente(id: number, cliente: string) {
    return this.http.patch<Pedido>(`${API_BASE_URL}/pedidos/${id}/cliente`, { cliente });
  }

  actualizarPedido(id: number, payload: PedidoUpdatePayload) {
    return this.http.put<Pedido>(`${API_BASE_URL}/pedidos/${id}`, payload);
  }

  actualizarPedidoAdmin(id: number, payload: PedidoAdminUpdatePayload) {
    return this.http.put<Pedido>(`${API_BASE_URL}/admin/pedidos/${id}`, payload);
  }

  cancelarPedidoAdmin(id: number, payload: PedidoAdminPinPayload) {
    return this.http.post<Pedido>(`${API_BASE_URL}/admin/pedidos/${id}/cancelar`, payload);
  }

  verificarPinAdminPedidos(payload: PedidoAdminPinPayload) {
    return this.http.post<void>(`${API_BASE_URL}/admin/pedidos/verificar-pin`, payload);
  }

  cancelarPedido(id: number) {
    return this.http.post<Pedido>(`${API_BASE_URL}/pedidos/${id}/cancelar`, null);
  }

  ventas() {
    return this.http.get<Venta[]>(`${API_BASE_URL}/ventas`);
  }

  posCheckout(payload: PosCheckoutPayload) {
    return this.http.post<PosCheckoutResult>(`${API_BASE_URL}/pos/checkout`, payload);
  }

  cajaActiva() {
    return this.http.get<CajaSesion>(`${API_BASE_URL}/caja/activa`, { observe: 'response' });
  }

  abrirCaja(payload: CajaAperturaPayload) {
    return this.http.post<CajaSesion>(`${API_BASE_URL}/caja/apertura`, payload);
  }

  cerrarCaja(payload: CajaCierrePayload) {
    return this.http.post<CajaSesion>(`${API_BASE_URL}/caja/cierre`, payload);
  }

  usuarios() {
    return this.http.get<Usuario[]>(`${API_BASE_URL}/usuarios`);
  }

  crearUsuario(payload: UsuarioCreatePayload) {
    return this.http.post<Usuario>(`${API_BASE_URL}/usuarios`, payload);
  }

  actualizarUsuario(id: number, payload: UsuarioUpdatePayload) {
    return this.http.put<Usuario>(`${API_BASE_URL}/usuarios/${id}`, payload);
  }

  eliminarUsuario(id: number) {
    return this.http.delete<void>(`${API_BASE_URL}/usuarios/${id}`);
  }

  resumenFinanciero() {
    return this.http.get<ResumenFinanciero>(`${API_BASE_URL}/reportes/resumen-financiero`);
  }

  almacenes() {
    return this.http.get<Almacen[]>(`${API_BASE_URL}/catalogo/almacenes`);
  }

  categoriasProducto() {
    return this.http.get<CategoriaProducto[]>(`${API_BASE_URL}/catalogo/categorias-producto`);
  }

  categoriasInventario() {
    return this.http.get<string[]>(`${API_BASE_URL}/catalogo/categorias-inventario`);
  }

  salonMesas() {
    return this.http.get<SalonMesa[]>(`${API_BASE_URL}/salon/mesas`);
  }

  salonMarcarCuenta(numero: number) {
    return this.http.post<SalonMesa>(`${API_BASE_URL}/salon/mesas/${numero}/cuenta`, null);
  }

  salonLiberarMesa(numero: number) {
    return this.http.post<SalonMesa>(`${API_BASE_URL}/salon/mesas/${numero}/liberar`, null);
  }
}
