import { EstadoMesaSalon, SalonMesa } from '../../../../models/salon.model';

export type MesaForma = 'sq' | 'rect' | 'round' | 'communal';

export type MesaSpot = {
  x: number;
  y: number;
  w: number;
  h: number;
  forma: MesaForma;
};

export const FLOOR_VIEWBOX = { w: 1100, h: 720 };

/** Grilla del salón principal (columnas 1–4, filas 1–3) */
export const GRID_LAYOUT = {
  originX: 296,
  originY: 88,
  cellW: 168,
  cellH: 178,
  cols: 4,
  rows: 3,
  tableYOffset: 28,
  cardAbove: 56,
};

/** Desplazamiento extra por fila (M5–7 fila 2, M8–10 fila 3) */
const ROW_Y_EXTRA: Record<number, number> = {
  2: 32,
  3: 64,
};

function rowTableY(posicionY: number): number {
  const { originY, cellH, tableYOffset } = GRID_LAYOUT;
  const extra = ROW_Y_EXTRA[posicionY] ?? 0;
  return originY + (posicionY - 1) * cellH + tableYOffset + extra;
}

const SALON_PAD = { x: 22, top: 14, bottom: 32 };

/** Borde punteado, jardineras y árboles derivados de la misma grilla que las mesas */
export function getSalonZone() {
  const { originX, cellW, cols, cardAbove } = GRID_LAYOUT;
  const x = originX - SALON_PAD.x;
  const y = rowTableY(1) - cardAbove - SALON_PAD.top;
  const w = cols * cellW + SALON_PAD.x * 2;
  const bottom = rowTableY(3) + 56 + 38 + SALON_PAD.bottom;
  return { x, y, w, h: bottom - y, labelX: originX - 2, labelY: y + 22 };
}

export function getRowDividers() {
  const dividers: { y: number }[] = [];
  for (let row = 1; row < GRID_LAYOUT.rows; row++) {
    dividers.push({ y: (rowTableY(row) + rowTableY(row + 1)) / 2 - 18 });
  }
  return dividers;
}

export const CORNER_TREES: { x: number; y: number; scale: number }[] = [
  { x: 948, y: 32, scale: 1.1 },
  { x: 948, y: 632, scale: 1.1 },
];

export const FORMA_FROM_DB: Record<string, MesaForma> = {
  cuadrada: 'sq',
  rectangular: 'rect',
  redonda: 'round',
  comunal: 'communal',
};

const FORMA_SIZES: Record<MesaForma, { w: number; h: number }> = {
  round: { w: 52, h: 52 },
  sq: { w: 56, h: 56 },
  rect: { w: 98, h: 52 },
  communal: { w: 112, h: 52 },
};

export function computeSpot(mesa: Pick<SalonMesa, 'posicionX' | 'posicionY' | 'forma'>): MesaSpot {
  const forma = (FORMA_FROM_DB[mesa.forma] ?? 'sq') as MesaForma;
  const size = FORMA_SIZES[forma];
  const x = GRID_LAYOUT.originX + (mesa.posicionX - 1) * GRID_LAYOUT.cellW + (GRID_LAYOUT.cellW - size.w) / 2;
  const y = rowTableY(mesa.posicionY);
  return { x, y, w: size.w, h: size.h, forma };
}

export const CHAIR_OFFSETS: Record<number, { dx: number; dy: number }[]> = {
  2: [
    { dx: 0, dy: -1 },
    { dx: 0, dy: 1 },
  ],
  4: [
    { dx: 0, dy: -1 },
    { dx: 0, dy: 1 },
    { dx: -1, dy: 0 },
    { dx: 1, dy: 0 },
  ],
  6: [
    { dx: -0.3, dy: -1 },
    { dx: 0.3, dy: -1 },
    { dx: -0.3, dy: 1 },
    { dx: 0.3, dy: 1 },
    { dx: -1, dy: 0 },
    { dx: 1, dy: 0 },
  ],
  10: [
    { dx: -0.35, dy: -1 },
    { dx: 0, dy: -1 },
    { dx: 0.35, dy: -1 },
    { dx: -0.35, dy: 1 },
    { dx: 0, dy: 1 },
    { dx: 0.35, dy: 1 },
  ],
};

export const DECOR_TREES = CORNER_TREES;

export const ESTADO_COLORS: Record<EstadoMesaSalon, { stroke: string; pill: string; dot: string; text: string }> = {
  disponible: { stroke: '#22c55e', pill: '#dcfce7', dot: '#22c55e', text: '#15803d' },
  reservada: { stroke: '#f59e0b', pill: '#fef3c7', dot: '#f59e0b', text: '#b45309' },
  ocupada: { stroke: '#ef4444', pill: '#fee2e2', dot: '#ef4444', text: '#b91c1c' },
  cuenta: { stroke: '#3b82f6', pill: '#dbeafe', dot: '#3b82f6', text: '#1d4ed8' },
  bloqueada: { stroke: '#94a3b8', pill: '#e2e8f0', dot: '#94a3b8', text: '#64748b' },
};
