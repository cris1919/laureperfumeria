import { DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AuthService, LoginResponse } from '../../core/services/auth.service';
import { AdminSidebar } from '../../shared/components/admin-sidebar/admin-sidebar';

interface AdminUser { idUsuario: number; nombres: string; apellidos: string; correo: string; rol: string; estado: string; fechaRegistro: string; tipo: 'USUARIO' | 'CLIENTE'; }
interface ClientResponse { idCliente: number; nombres: string; apellidos: string; correo: string; estado: string; fechaRegistro: string; }

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [DatePipe, FormsModule, AdminSidebar],
  templateUrl: './admin-users.html',
  styleUrl: './admin-users.css',
})
export class AdminUsers implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  readonly user = signal<LoginResponse | null>(null);
  readonly users = signal<AdminUser[]>([]);
  readonly search = signal('');
  readonly status = signal('TODOS');
  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly error = signal('');
  readonly success = signal('');
  readonly editing = signal<AdminUser | null>(null);
  readonly form = { nombres: '', apellidos: '', correo: '', rol: 'ADMIN' };
  readonly filteredUsers = computed(() => {
    const term = this.search().trim().toLowerCase();
    return this.users().filter((item) => {
      const matchesTerm = !term || `${item.nombres} ${item.apellidos} ${item.correo} ${item.rol}`.toLowerCase().includes(term);
      return matchesTerm && (this.status() === 'TODOS' || item.estado.toUpperCase() === this.status());
    });
  });
  readonly activeUsers = computed(() => this.users().filter((item) => item.estado.toUpperCase() === 'ACTIVO').length);
  readonly adminUsers = computed(() => this.users().filter((item) => item.rol.toUpperCase() === 'ADMIN').length);

  ngOnInit(): void {
    this.auth.loadSession();
    const current = this.auth.currentUser();
    if (!current || current.rol?.toUpperCase() !== 'ADMIN') { void this.router.navigateByUrl('/login'); return; }
    this.user.set(current);
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading.set(true); this.error.set('');
    forkJoin({
      users: this.auth.getAuthenticated<AdminUser[]>('/api/usuarios'),
      clients: this.auth.getAuthenticated<ClientResponse[]>('/api/clientes'),
    }).subscribe({
      next: ({ users, clients }) => {
        const clientEntries: AdminUser[] = clients.map((client) => ({ idUsuario: client.idCliente, nombres: client.nombres, apellidos: client.apellidos, correo: client.correo, rol: 'CLIENTE', estado: client.estado, fechaRegistro: client.fechaRegistro, tipo: 'CLIENTE' }));
        this.users.set([...users.map((user) => ({ ...user, tipo: 'USUARIO' as const })), ...clientEntries]);
        this.loading.set(false);
      },
      error: () => { this.error.set('No se pudo cargar la lista de usuarios y clientes. Verifica tu sesión y el backend.'); this.loading.set(false); },
    });
  }

  openEdit(item: AdminUser): void {
    this.editing.set(item);
    this.form.nombres = item.nombres; this.form.apellidos = item.apellidos; this.form.correo = item.correo; this.form.rol = item.rol;
    this.error.set(''); this.success.set('');
  }

  closeEdit(): void { if (!this.saving()) this.editing.set(null); }

  saveUser(): void {
    const item = this.editing();
    if (!item || !this.form.nombres.trim() || !this.form.apellidos.trim() || !this.form.correo.trim()) { this.error.set('Completa nombres, apellidos y correo.'); return; }
    this.saving.set(true); this.error.set('');
    this.auth.putAuthenticated<AdminUser>('/api/usuarios/' + item.idUsuario, { ...this.form, nombres: this.form.nombres.trim(), apellidos: this.form.apellidos.trim(), correo: this.form.correo.trim() }).subscribe({
      next: (updated) => { this.users.update((items) => items.map((current) => current.tipo === 'USUARIO' && current.idUsuario === updated.idUsuario ? { ...updated, tipo: 'USUARIO' } : current)); this.success.set('Usuario actualizado correctamente.'); this.saving.set(false); this.editing.set(null); },
      error: (error) => { this.error.set(error?.error?.message || 'No se pudo actualizar el usuario.'); this.saving.set(false); },
    });
  }

  toggleStatus(item: AdminUser): void {
    const nextStatus = item.estado.toUpperCase() === 'ACTIVO' ? 'INACTIVO' : 'ACTIVO';
    const endpoint = item.tipo === 'CLIENTE' ? '/api/clientes/' + item.idUsuario + '/estado' : '/api/usuarios/' + item.idUsuario + '/estado';
    this.auth.patchAuthenticated<{ estado: string }>(endpoint, { estado: nextStatus }).subscribe({
      next: (updated) => { this.users.update((items) => items.map((current) => current.tipo === item.tipo && current.idUsuario === item.idUsuario ? { ...current, estado: updated.estado } : current)); this.success.set(`${item.tipo === 'CLIENTE' ? 'Cliente' : 'Usuario'} ${nextStatus === 'ACTIVO' ? 'activado' : 'desactivado'} correctamente.`); },
      error: (error) => this.error.set(error?.error?.message || `No se pudo cambiar el estado del ${item.tipo === 'CLIENTE' ? 'cliente' : 'usuario'}.`),
    });
  }

  selectSection(section: string): void { if (section === 'Dashboard') void this.router.navigateByUrl('/admin'); else if (section === 'Catálogo') void this.router.navigateByUrl('/admin/catalogo'); else if (section === 'Pedidos') void this.router.navigateByUrl('/admin/pedidos'); }
  statusLabel(status: string): string { return status.replaceAll('_', ' ').toLowerCase().replace(/(^|\s)\S/g, (letter) => letter.toUpperCase()); }
}
