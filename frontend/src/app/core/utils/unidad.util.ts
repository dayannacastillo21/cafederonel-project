const UNIDADES_PIEZA = new Set([
  'individuos',
  'individuo',
  'unidad',
  'unidades',
  'und',
  'und.',
  'pieza',
  'piezas',
  'u',
  'uds',
  'uds.',
]);

const UNIDADES_CONOCIDAS = new Set(['unid.', 'kg', 'litros', 'latas', 'botellas', 'paquetes', 'millares']);

function limpiarUnidad(value: string): string {
  return value
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .trim()
    .toLowerCase()
    .replace(/\.+$/, '');
}

function esUnidadPieza(normalized: string): boolean {
  return UNIDADES_PIEZA.has(normalized) || normalized.includes('individuo');
}

function esUnidadInvalida(normalized: string): boolean {
  return (
    !normalized ||
    normalized.includes('identificad') ||
    normalized === 'sin unidad' ||
    normalized === 'sin definir' ||
    normalized === 'n/a' ||
    normalized === 'na'
  );
}

export function normalizeUnidad(value?: string | null): string {
  const raw = (value ?? '').trim();
  if (!raw) {
    return 'unid.';
  }

  const normalized = limpiarUnidad(raw);

  if (esUnidadInvalida(normalized)) {
    return 'unid.';
  }

  if (esUnidadPieza(normalized)) {
    return 'unid.';
  }

  if (normalized === 'kilogramo' || normalized === 'kilogramos' || normalized === 'kilo' || normalized === 'kilos') {
    return 'kg';
  }

  if (normalized === 'litro' || normalized === 'l') {
    return 'litros';
  }

  if (normalized === 'lata') {
    return 'latas';
  }

  if (normalized === 'botella') {
    return 'botellas';
  }

  if (normalized === 'paquete') {
    return 'paquetes';
  }

  if (normalized === 'millar') {
    return 'millares';
  }

  if (UNIDADES_CONOCIDAS.has(normalized) || UNIDADES_CONOCIDAS.has(raw)) {
    return UNIDADES_CONOCIDAS.has(raw) ? raw : normalized === 'unid' ? 'unid.' : normalized;
  }

  return raw;
}

export function unidadLabel(value?: string | null): string {
  return normalizeUnidad(value);
}
