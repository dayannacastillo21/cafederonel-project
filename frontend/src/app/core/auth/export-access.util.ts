import { SessionUser } from '../../models/auth.models';

export function canExportInventarioReports(role?: SessionUser['role'] | null): boolean {
  return role === 'ADMIN' || role === 'INVENTARIO';
}
