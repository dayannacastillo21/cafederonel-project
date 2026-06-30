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

  protected readonly navigationItems = computed(() => {
    const role = this.auth.session()?.role;
    return NAVIGATION_ITEMS.filter((item) => !item.roles || (role && item.roles.includes(role)));
  });
}
