const PLACEHOLDER =
  'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?auto=format&fit=crop&w=400&h=400&q=80';

export function productImageUrl(url?: string | null): string {
  const trimmed = url?.trim();
  return trimmed || PLACEHOLDER;
}

export function onProductImageError(event: Event): void {
  const img = event.target as HTMLImageElement;
  if (img.src !== PLACEHOLDER) {
    img.src = PLACEHOLDER;
  }
}
