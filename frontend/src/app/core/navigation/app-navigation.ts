import { SessionUser } from '../../models/auth.models';
import { NavigationIcon } from './navigation-item.model';

export type NavigationItem = {
  label: string;
  path: string;
  icon: NavigationIcon;
  roles: SessionUser['role'][];
};

export const NAVIGATION_ITEMS: NavigationItem[] = [
  { label: 'Dashboard', path: '/dashboard', icon: 'dashboard', roles: ['ADMIN', 'CAJERO', 'INVENTARIO', 'CONTADOR'] },
  { label: 'Productos', path: '/productos', icon: 'productos', roles: ['ADMIN', 'CAJERO', 'INVENTARIO'] },
  { label: 'Inventario', path: '/inventario', icon: 'inventario', roles: ['ADMIN', 'INVENTARIO'] },
  { label: 'Kardex', path: '/kardex', icon: 'kardex', roles: ['ADMIN', 'INVENTARIO'] },
  { label: 'Proveedores', path: '/proveedores', icon: 'proveedores', roles: ['ADMIN', 'INVENTARIO'] },
  { label: 'Pedidos', path: '/pedidos', icon: 'pedidos', roles: ['ADMIN', 'CAJERO', 'CONTADOR'] },
  { label: 'POS', path: '/pos', icon: 'pos', roles: ['ADMIN', 'CAJERO'] },
  { label: 'Ventas', path: '/ventas', icon: 'ventas', roles: ['ADMIN', 'CAJERO', 'CONTADOR'] },
  { label: 'Usuarios', path: '/usuarios', icon: 'usuarios', roles: ['ADMIN'] },
  { label: 'Reportes', path: '/reportes', icon: 'reportes', roles: ['ADMIN', 'CONTADOR'] },
];
