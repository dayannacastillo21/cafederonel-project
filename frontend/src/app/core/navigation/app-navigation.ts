import { SessionUser } from '../../models/auth.models';
import { NavigationIcon } from './navigation-item.model';

export type NavigationItem = {
  label: string;
  path: string;
  icon: NavigationIcon;
  roles?: SessionUser['role'][];
};

export const NAVIGATION_ITEMS: NavigationItem[] = [
  { label: 'Panel', path: '/dashboard', icon: 'dashboard' },
  { label: 'Productos', path: '/productos', icon: 'productos' },
  { label: 'Inventario', path: '/inventario', icon: 'inventario' },
  { label: 'Kardex', path: '/kardex', icon: 'kardex' },
  { label: 'Proveedores', path: '/proveedores', icon: 'proveedores' },
  { label: 'Pedidos', path: '/pedidos', icon: 'pedidos' },
  { label: 'Punto de venta', path: '/ventas', icon: 'ventas', roles: ['ADMIN', 'CAJERO'] },
  { label: 'Mapa de la cafetería', path: '/salon', icon: 'salon', roles: ['ADMIN', 'CAJERO'] },
  { label: 'Usuarios', path: '/usuarios', icon: 'usuarios', roles: ['ADMIN'] },
];
