import { Routes } from '@angular/router';

import { AuthLayout } from './layouts/auth-layout/auth-layout';
import { MainLayout } from './layouts/main-layout/main-layout';
import { authGuard } from './core/auth/auth.guard';
import { roleGuard } from './core/auth/role.guard';

export const routes: Routes = [
  {
    path: 'login',
    component: AuthLayout,
    loadChildren: () =>
      import('./features/auth/auth.routes').then((routes) => routes.AUTH_ROUTES),
  },
  {
    path: '',
    component: MainLayout,
    canActivateChild: [authGuard],
    children: [
      {
        path: 'dashboard',
        canActivate: [roleGuard(['ADMIN', 'CAJERO', 'INVENTARIO', 'CONTADOR'])],
        loadComponent: () =>
          import('./features/dashboard/pages/dashboard-page/dashboard-page').then(
            (component) => component.DashboardPage,
          ),
      },
      {
        path: 'productos',
        canActivate: [roleGuard(['ADMIN', 'CAJERO', 'INVENTARIO'])],
        loadComponent: () =>
          import('./features/productos/pages/productos-page/productos-page').then(
            (component) => component.ProductosPage,
          ),
      },
      {
        path: 'inventario',
        canActivate: [roleGuard(['ADMIN', 'INVENTARIO'])],
        loadComponent: () =>
          import('./features/inventario/pages/inventario-page/inventario-page').then(
            (component) => component.InventarioPage,
          ),
      },
      {
        path: 'kardex',
        canActivate: [roleGuard(['ADMIN', 'INVENTARIO'])],
        loadComponent: () =>
          import('./features/kardex/pages/kardex-page/kardex-page').then(
            (component) => component.KardexPage,
          ),
      },
      {
        path: 'proveedores',
        canActivate: [roleGuard(['ADMIN', 'INVENTARIO'])],
        loadComponent: () =>
          import('./features/proveedores/pages/proveedores-page/proveedores-page').then(
            (component) => component.ProveedoresPage,
          ),
      },
      {
        path: 'pedidos',
        canActivate: [roleGuard(['ADMIN', 'CAJERO', 'CONTADOR'])],
        loadComponent: () =>
          import('./features/pedidos/pages/pedidos-page/pedidos-page').then(
            (component) => component.PedidosPage,
          ),
      },
      {
        path: 'pos',
        canActivate: [roleGuard(['ADMIN', 'CAJERO'])],
        loadComponent: () =>
          import('./features/pos/pages/pos-page/pos-page').then((component) => component.PosPage),
      },
      {
        path: 'ventas',
        canActivate: [roleGuard(['ADMIN', 'CAJERO', 'CONTADOR'])],
        loadComponent: () =>
          import('./features/ventas/pages/ventas-page/ventas-page').then((component) => component.VentasPage),
      },
      {
        path: 'salon',
        canActivate: [roleGuard(['ADMIN', 'CAJERO'])],
        loadComponent: () =>
          import('./features/salon/pages/salon-page/salon-page').then((component) => component.SalonPage),
      },
      {
        path: 'usuarios',
        canActivate: [roleGuard(['ADMIN'])],
        loadComponent: () =>
          import('./features/usuarios/pages/usuarios-page/usuarios-page').then(
            (component) => component.UsuariosPage,
          ),
      },
      {
        path: 'reportes',
        canActivate: [roleGuard(['ADMIN', 'CONTADOR'])],
        loadComponent: () =>
          import('./features/reportes/pages/reportes-page/reportes-page').then(
            (component) => component.ReportesPage,
          ),
      },
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
    ],
  },
  { path: '**', redirectTo: 'dashboard' },
];
