export type ProductoStockDisponible = {
  productoId: number;
  unidadesDisponibles: number | null;
  sinReceta: boolean;
  insumoLimitante?: string;
};
