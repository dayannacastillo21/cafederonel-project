export type Producto = {
  id: number;
  nombre: string;
  descripcion?: string;
  precio: number;
  categoria: string;
  sku: string;
  codigoBarras?: string;
  costo: number;
  margenPorcentaje: number;
  imagenUrl?: string;
  unidadVenta: string;
  activo: boolean;
  fechaCreacion?: string;
  fechaActualizacion?: string;
};

export type ProductoPayload = {
  nombre: string;
  descripcion?: string;
  precio: number;
  categoria: string;
  sku?: string;
  codigoBarras?: string;
  costo?: number;
  margenPorcentaje?: number;
  imagenUrl?: string;
  unidadVenta?: string;
  activo?: boolean;
};
