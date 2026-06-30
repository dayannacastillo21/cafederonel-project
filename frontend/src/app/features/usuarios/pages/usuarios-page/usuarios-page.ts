import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { PageHeader } from '../../../../shared/components/page-header/page-header';
import { CafederonelApiService } from '../../../../services/cafederonel-api.service';
import { Usuario } from '../../../../models/usuario.model';

@Component({
  selector: 'app-usuarios-page',
  imports: [PageHeader],
  templateUrl: './usuarios-page.html',
  styleUrl: './usuarios-page.scss',
})
export class UsuariosPage {
  private readonly api = inject(CafederonelApiService);

  protected readonly columns = ['Nombre', 'Correo', 'Rol', 'Estado'];
  protected readonly rows = signal<Usuario[]>([]);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal('');

  constructor() {
    this.api
      .usuarios()
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: (usuarios) => {
          this.rows.set(usuarios);
          this.loading.set(false);
        },
        error: () => {
          this.errorMessage.set('No se pudieron cargar usuarios desde la base de datos.');
          this.loading.set(false);
        },
      });
  }
}
