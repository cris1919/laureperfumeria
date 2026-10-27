import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService, LoginResponse } from '../../core/services/auth.service';
import { Footer } from '../../shared/components/footer/footer';
import { Header } from '../../shared/components/header/header';

interface Purchase { idPedido: number; fechaPedido: string; estadoPedido: string; subtotal: number; costoEnvio: number; total: number; detalles: Array<{ idDetalle: number; nombreProducto: string; cantidad: number; subtotal: number }>; }

@Component({ selector: 'app-purchase-history', standalone: true, imports: [Header, Footer, RouterLink, CurrencyPipe, DatePipe], templateUrl: './purchase-history.html', styleUrl: './purchase-history.css' })
export class PurchaseHistory implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  readonly user = signal<LoginResponse | null>(null);
  readonly purchases = signal<Purchase[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly expanded = signal<number | null>(null);
  readonly totalSpent = computed(() => this.purchases().reduce((total, order) => total + Number(order.total || 0), 0));

  ngOnInit(): void {
    this.auth.loadSession();
    const current = this.auth.currentUser();
    if (!current || current.rol?.toUpperCase() !== 'CLIENTE') { void this.router.navigateByUrl('/login'); return; }
    this.user.set(current);
    this.auth.getAuthenticated<Purchase[]>('/api/pedidos/cliente/' + current.idUsuario + '/historial').subscribe({
      next: (purchases) => { this.purchases.set(purchases); this.loading.set(false); },
      error: () => { this.error.set('No se pudo cargar tu historial de compras.'); this.loading.set(false); },
    });
  }

  toggle(orderId: number): void { this.expanded.update((current) => current === orderId ? null : orderId); }
  orderLabel(order: Purchase): string { return `#LP-${new Date(order.fechaPedido).getFullYear()}-${String(order.idPedido).padStart(6, '0')}`; }
  statusLabel(status: string): string { return status.replaceAll('_', ' ').toLowerCase().replace(/(^|\s)\S/g, (letter) => letter.toUpperCase()); }
}
