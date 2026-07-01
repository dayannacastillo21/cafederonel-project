import { Component, computed, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

import { AuthService } from '../../../core/auth/auth.service';
import { NAVIGATION_ITEMS } from '../../../core/navigation/app-navigation';

@Component({
  selector: 'app-sidebar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss',
})
export class Sidebar {
  private readonly auth = inject(AuthService);

  protected readonly session = this.auth.session;

  protected readonly navigationItems = computed(() => {
    const role = this.auth.session()?.role;
    return NAVIGATION_ITEMS.filter((item) => role && item.roles.includes(role));
  });

  protected readonly roleAbbr = computed(() => {
    const role = this.auth.session()?.role;
    const labels = {
      ADMIN: 'ADM',
      CAJERO: 'CAJ',
      INVENTARIO: 'INV',
      CONTADOR: 'CON',
    } as const;
    return role ? labels[role] : 'APP';
  });

  protected readonly roleLabel = computed(() => {
    const role = this.auth.session()?.role;
    const labels = {
      ADMIN: 'Administrador',
      CAJERO: 'Caja',
      INVENTARIO: 'Inventario',
      CONTADOR: 'Contabilidad',
    } as const;
    return role ? labels[role] : 'Sistema';
  });
}
