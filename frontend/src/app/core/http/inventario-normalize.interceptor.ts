import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { map } from 'rxjs';

import { InventarioItem } from '../../models/inventario.model';
import { normalizeUnidad } from '../utils/unidad.util';

function normalizeInventarioPayload(body: unknown): unknown {
  if (Array.isArray(body)) {
    return body.map((item) => normalizeInventarioRecord(item));
  }

  if (body && typeof body === 'object' && 'unidad' in body) {
    return normalizeInventarioRecord(body);
  }

  return body;
}

function normalizeInventarioRecord(item: unknown): InventarioItem {
  const record = { ...(item as InventarioItem) };
  record.unidad = normalizeUnidad(record.unidad);
  return record;
}

export const inventarioNormalizeInterceptor: HttpInterceptorFn = (request, next) => {
  const shouldNormalize =
    request.url.includes('/inventario') && !request.url.includes('/movimientos');

  if (!shouldNormalize) {
    return next(request);
  }

  return next(request).pipe(
    map((event) => {
      if (!(event instanceof HttpResponse) || event.body == null) {
        return event;
      }

      return event.clone({
        body: normalizeInventarioPayload(event.body),
      });
    }),
  );
};
