import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService, ClientProfile, LoginResponse } from '../../core/services/auth.service';
import { Header } from '../../shared/components/header/header';
import { Footer } from '../../shared/components/footer/footer';

interface HistoryOrder { idPedido: number; fechaPedido: string; estadoPedido: string; total: number; }

@Component({ selector: 'app-profile', standalone: true, imports: [Header, Footer, FormsModule, RouterLink, CurrencyPipe, DatePipe], templateUrl: './profile.html', styleUrl: './profile.css' })
export class Profile implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  readonly profile = signal<ClientProfile | null>(null);
  readonly orders = signal<HistoryOrder[]>([]);
  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly error = signal('');
  readonly success = signal('');
  form = { nombres: '', apellidos: '', correo: '', telefono: '', direccion: '' };

  ngOnInit(): void {
    this.auth.loadSession();
    const current = this.auth.currentUser();
    if (!current || current.rol?.toUpperCase() !== 'CLIENTE') { void this.router.navigateByUrl('/login'); return; }
    this.auth.getClientProfile(current.idUsuario).subscribe({
      next: (profile) => { this.profile.set(profile); this.form = { nombres: profile.nombres, apellidos: profile.apellidos, correo: profile.correo, telefono: profile.telefono || '', direccion: profile.direccion || '' }; this.loading.set(false); },
      error: () => { this.error.set('No se pudo cargar tu perfil.'); this.loading.set(false); },
    });
    this.auth.getAuthenticated<HistoryOrder[]>('/api/pedidos/cliente/' + current.idUsuario + '/historial').subscribe({ next: (orders) => this.orders.set(orders) });
  }

  saveProfile(): void {
    const current = this.profile();
    if (!current) return;
    if (!this.form.nombres.trim() || !this.form.apellidos.trim() || !this.form.correo.trim()) { this.error.set('Nombres, apellidos y correo son obligatorios.'); return; }
    this.saving.set(true); this.error.set(''); this.success.set('');
    this.auth.putAuthenticated<ClientProfile>('/api/clientes/' + current.idCliente, { ...this.form, nombres: this.form.nombres.trim(), apellidos: this.form.apellidos.trim(), correo: this.form.correo.trim() }).subscribe({
      next: (profile) => { this.profile.set(profile); this.success.set('Perfil actualizado correctamente.'); this.saving.set(false); },
      error: (error) => { this.error.set(error?.error?.message || 'No se pudo actualizar tu perfil.'); this.saving.set(false); },
    });
  }

  orderLabel(order: HistoryOrder): string { return `#LP-${new Date(order.fechaPedido).getFullYear()}-${String(order.idPedido).padStart(6, '0')}`; }
  statusLabel(status: string): string { return status.replaceAll('_', ' ').toLowerCase().replace(/(^|\s)\S/g, (letter) => letter.toUpperCase()); }
}
