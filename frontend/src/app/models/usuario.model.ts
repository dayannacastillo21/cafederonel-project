export type Usuario = {
  id: number;
  nombre: string;
  email: string;
  rol: 'ADMIN' | 'CAJERO' | 'INVENTARIO' | 'CONTADOR';
  activo: boolean;
};

export type UsuarioCreatePayload = {
  nombre: string;
  email: string;
  password: string;
  rol: Usuario['rol'];
};

export type UsuarioUpdatePayload = {
  nombre: string;
  email: string;
  password?: string;
  rol: Usuario['rol'];
  activo: boolean;
};
