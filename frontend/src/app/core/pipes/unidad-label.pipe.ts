import { Pipe, PipeTransform } from '@angular/core';

import { unidadLabel } from '../utils/unidad.util';

@Pipe({
  name: 'unidadLabel',
  standalone: true,
  pure: true,
})
export class UnidadLabelPipe implements PipeTransform {
  transform(value?: string | null): string {
    return unidadLabel(value);
  }
}
