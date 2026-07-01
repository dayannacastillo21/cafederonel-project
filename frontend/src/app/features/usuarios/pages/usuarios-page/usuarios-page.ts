import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { AuthService } from '../../../../core/auth/auth.service';
import { PageHeader } from '../../../../shared/components/page-header/page-header';
import { CafederonelApiService } from '../../../../services/cafederonel-api.service';
import { Usuario, UsuarioCreatePayload, UsuarioUpdatePayload } from '../../../../models/usuario.model';

type UsuarioModalMode = 'create' | 'edit' | null;

const ROLES: Usuario['rol'][] = ['ADMIN', 'CAJERO', 'INVENTARIO', 'CONTADOR'];

const ROLE_LABELS: Record<Usuario['rol'], string> = {
  ADMIN: 'Administrador',
  CAJERO: 'Cajero',
  INVENTARIO: 'Inventario',
  CONTADOR: 'Contador',
};

@Component({
  selector: 'app-usuarios-page',
  imports: [PageHeader, ReactiveFormsModule],
  templateUrl: './usuarios-page.html',
  styleUrl: './usuarios-page.scss',
})
export class UsuariosPage {
  private readonly api = inject(CafederonelApiService);
  private readonly auth = inject(AuthService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly fb = inject(FormBuilder);

  protected readonly roles = ROLES;
  protected readonly roleLabels = ROLE_LABELS;
  protected readonly columns = ['Nombre', 'Correo', 'Rol', 'Estado', 'Acciones'];
  protected readonly rows = signal<Usuario[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly actionId = signal<number | null>(null);
  protected readonly errorMessage = signal('');
  protected readonly actionMessage = signal('');
  protected readonly formMessage = signal('');
  protected readonly modalMode = signal<UsuarioModalMode>(null);
  protected readonly editingUser = signal<Usuario | null>(null);

  protected readonly userForm = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(120)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
    password: ['', [Validators.minLength(4), Validators.maxLength(100)]],
    rol: ['CAJERO' as Usuario['rol'], [Validators.required]],
    activo: [true],
  });

  protected readonly activeCount = computed(() => this.rows().filter((row) => row.activo).length);
  protected readonly isCreateMode = computed(() => this.modalMode() === 'create');

  constructor() {
    this.loadUsuarios();
  }

  protected roleLabel(rol: Usuario['rol']): string {
    return this.roleLabels[rol] ?? rol;
  }

  protected isCurrentUser(usuario: Usuario): boolean {
    return this.auth.session()?.userId === usuario.id;
  }

  protected openCreate(): void {
    this.editingUser.set(null);
    this.formMessage.set('');
    this.userForm.reset({
      nombre: '',
      email: '',
      password: '',
      rol: 'CAJERO',
      activo: true,
    });
    this.userForm.controls.password.setValidators([Validators.required, Validators.minLength(4), Validators.maxLength(100)]);
    this.userForm.controls.password.updateValueAndValidity();
    this.modalMode.set('create');
  }

  protected openEdit(usuario: Usuario): void {
    this.editingUser.set(usuario);
    this.formMessage.set('');
    this.userForm.reset({
      nombre: usuario.nombre,
      email: usuario.email,
      password: '',
      rol: usuario.rol,
      activo: usuario.activo,
    });
    this.userForm.controls.password.setValidators([Validators.minLength(4), Validators.maxLength(100)]);
    this.userForm.controls.password.updateValueAndValidity();
    this.modalMode.set('edit');
  }

  protected closeModal(): void {
    this.modalMode.set(null);
    this.editingUser.set(null);
    this.formMessage.set('');
  }

  protected saveUsuario(): void {
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      this.formMessage.set('Revisa los campos obligatorios antes de guardar.');
      return;
    }

    const mode = this.modalMode();
    const raw = this.userForm.getRawValue();
    this.saving.set(true);
    this.formMessage.set('');

    if (mode === 'create') {
      const payload: UsuarioCreatePayload = {
        nombre: raw.nombre.trim(),
        email: raw.email.trim(),
        password: raw.password,
        rol: raw.rol,
      };

      this.api
        .crearUsuario(payload)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (created) => {
            this.rows.set([...this.rows(), created].sort((a, b) => a.nombre.localeCompare(b.nombre)));
            this.saving.set(false);
            this.closeModal();
            this.actionMessage.set(`Usuario "${created.nombre}" creado correctamente.`);
          },
          error: (error) => {
            this.formMessage.set(this.errorFromApi(error));
            this.saving.set(false);
          },
        });
      return;
    }

    const selected = this.editingUser();
    if (!selected) {
      this.saving.set(false);
      return;
    }

    const payload: UsuarioUpdatePayload = {
      nombre: raw.nombre.trim(),
      email: raw.email.trim(),
      rol: raw.rol,
      activo: raw.activo,
    };
    const password = raw.password.trim();
    if (password) {
      payload.password = password;
    }

    this.api
      .actualizarUsuario(selected.id, payload)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (updated) => {
          this.upsertUsuario(updated);
          this.saving.set(false);
          this.closeModal();
          this.actionMessage.set(`Usuario "${updated.nombre}" actualizado.`);
        },
        error: (error) => {
          this.formMessage.set(this.errorFromApi(error));
          this.saving.set(false);
        },
      });
  }

  protected toggleActivo(usuario: Usuario): void {
    if (this.isCurrentUser(usuario) && usuario.activo) {
      this.actionMessage.set('No puedes inactivar tu propia cuenta mientras estas conectado.');
      return;
    }

    const nextActivo = !usuario.activo;
    const label = nextActivo ? 'reactivar' : 'inactivar';
    if (!confirm(`¿${nextActivo ? 'Reactivar' : 'Inactivar'} la cuenta de "${usuario.nombre}"?`)) {
      return;
    }

    this.actionId.set(usuario.id);
    this.actionMessage.set('');

    const payload: UsuarioUpdatePayload = {
      nombre: usuario.nombre,
      email: usuario.email,
      rol: usuario.rol,
      activo: nextActivo,
    };

    this.api
      .actualizarUsuario(usuario.id, payload)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (updated) => {
          this.upsertUsuario(updated);
          this.actionId.set(null);
          this.actionMessage.set(`Cuenta de "${updated.nombre}" ${nextActivo ? 'reactivada' : 'inactivada'}.`);
        },
        error: (error) => {
          this.actionId.set(null);
          this.actionMessage.set(this.errorFromApi(error));
        },
      });
  }

  protected deleteUsuario(usuario: Usuario): void {
    if (this.isCurrentUser(usuario)) {
      this.actionMessage.set('No puedes eliminar tu propia cuenta mientras estas conectado.');
      return;
    }

    if (
      !confirm(
        `¿Eliminar permanentemente a "${usuario.nombre}"?\n\nEsta accion no se puede deshacer. Si tiene historial de ventas, usa Inactivar.`,
      )
    ) {
      return;
    }

    this.actionId.set(usuario.id);
    this.actionMessage.set('');

    this.api
      .eliminarUsuario(usuario.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.rows.set(this.rows().filter((row) => row.id !== usuario.id));
          this.actionId.set(null);
          this.actionMessage.set(`Usuario "${usuario.nombre}" eliminado.`);
        },
        error: (error) => {
          this.actionId.set(null);
          this.actionMessage.set(this.errorFromApi(error));
        },
      });
  }

  protected isActionLoading(id: number): boolean {
    return this.actionId() === id;
  }

  protected fieldInvalid(controlName: string): boolean {
    const control = this.userForm.get(controlName);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  private loadUsuarios(): void {
    this.api
      .usuarios()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (usuarios) => {
          this.rows.set([...usuarios].sort((a, b) => a.nombre.localeCompare(b.nombre)));
          this.loading.set(false);
        },
        error: () => {
          this.errorMessage.set('No se pudieron cargar usuarios desde la base de datos.');
          this.loading.set(false);
        },
      });
  }

  private upsertUsuario(usuario: Usuario): void {
    this.rows.set(
      this.rows()
        .map((row) => (row.id === usuario.id ? usuario : row))
        .sort((a, b) => a.nombre.localeCompare(b.nombre)),
    );
  }

  private errorFromApi(error: unknown): string {
    const response = error as { status?: number; error?: { message?: string } };
    if (response.status === 0) {
      return 'El backend no responde. Reinicia Spring Boot en el puerto 8081.';
    }
    return response.error?.message || 'No se pudo completar la operacion.';
  }
}
