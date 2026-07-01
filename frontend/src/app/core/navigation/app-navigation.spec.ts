import { NAVIGATION_ITEMS } from './app-navigation';
import { SessionUser } from '../../models/auth.models';

const visibleLabels = (role: SessionUser['role']) =>
  NAVIGATION_ITEMS.filter((item) => item.roles.includes(role)).map((item) => item.label);

describe('NAVIGATION_ITEMS', () => {
  it('filters modules for ADMIN', () => {
    expect(visibleLabels('ADMIN')).toEqual([
      'Dashboard',
      'Productos',
      'Inventario',
      'Kardex',
      'Proveedores',
      'Pedidos',
      'POS',
      'Ventas',
      'Usuarios',
      'Reportes',
    ]);
  });

  it('filters modules for CAJERO', () => {
    expect(visibleLabels('CAJERO')).toEqual(['Dashboard', 'Productos', 'Pedidos', 'POS', 'Ventas']);
  });

  it('filters modules for INVENTARIO', () => {
    expect(visibleLabels('INVENTARIO')).toEqual([
      'Dashboard',
      'Productos',
      'Inventario',
      'Kardex',
      'Proveedores',
    ]);
  });

  it('filters modules for CONTADOR', () => {
    expect(visibleLabels('CONTADOR')).toEqual(['Dashboard', 'Pedidos', 'Ventas', 'Reportes']);
  });
});
