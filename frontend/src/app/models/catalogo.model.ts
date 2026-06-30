export type Almacen = {
  id: number;
  codigo: string;
  nombre: string;
  descripcion?: string;
  activo: boolean;
};

export type CategoriaProducto = {
  id: number;
  nombre: string;
  descripcion?: string;
  activo: boolean;
};
