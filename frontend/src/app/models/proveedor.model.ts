export type Proveedor = {
  id: number;
  codigoProveedor: string;
  nombre: string;
  categoria: string;
  ruc?: string;
  contacto?: string;
  telefono: string;
  direccion: string;
  ciudad?: string;
  email: string;
  sitioWeb?: string;
  notas?: string;
  activo: boolean;
  fechaActualizacion?: string;
};

export type ProveedorPayload = {
  codigoProveedor?: string;
  nombre: string;
  categoria: string;
  ruc?: string;
  contacto?: string;
  telefono: string;
  direccion: string;
  ciudad?: string;
  email: string;
  sitioWeb?: string;
  notas?: string;
  activo?: boolean;
};
