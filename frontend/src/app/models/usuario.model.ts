export type Usuario = {
  id: number;
  nombre: string;
  email: string;
  rol: 'ADMIN' | 'CAJERO' | 'INVENTARIO' | 'CONTADOR';
  activo: boolean;
};
