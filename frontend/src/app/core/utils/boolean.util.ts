export function coerceActivo(value: unknown, fallback = true): boolean {
  if (value === false || value === 'false' || value === 0) {
    return false;
  }

  if (value === true || value === 'true' || value === 1) {
    return true;
  }

  return fallback;
}
