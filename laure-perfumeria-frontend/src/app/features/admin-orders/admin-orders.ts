import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AuthService, LoginResponse } from '../../core/services/auth.service';
import { AdminSidebar } from '../../shared/components/admin-sidebar/admin-sidebar';

interface OrderDetail { idDetalle: number; idProducto: number; nombreProducto: string; cantidad: number; precioUnitario: number; subtotal: number; }
interface AdminOrder { idPedido: number; idCliente: number; fechaPedido: string; estadoPedido: string; subtotal: number; costoEnvio: number; total: number; observaciones?: string; detalles: OrderDetail[]; }
interface Client { idCliente: number; nombres: string; apellidos: string; correo: string; }

@Component({ selector: 'app-admin-orders', standalone: true, imports: [CurrencyPipe, DatePipe, FormsModule, AdminSidebar], templateUrl: './admin-orders.html', styleUrl: './admin-orders.css' })
export class AdminOrders implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  readonly user = signal<LoginResponse | null>(null);
  readonly orders = signal<AdminOrder[]>([]);
  readonly clients = signal<Client[]>([]);
  readonly search = signal('');
  readonly status = signal('TODOS');
  readonly loading = signal(true);
  readonly error = signal('');
  readonly success = signal('');
  readonly selected = signal<AdminOrder | null>(null);
  readonly statuses = ['TODOS', 'PENDIENTE', 'CONFIRMADO', 'EN_PREPARACION', 'ENVIADO', 'ENTREGADO', 'CANCELADO'];
  readonly filteredOrders = computed(() => {
    const term = this.search().trim().toLowerCase();
    return this.orders().filter((order) => {
      const client = this.clientName(order.idCliente).toLowerCase();
      const matchesTerm = !term || `${order.idPedido} ${client} ${order.estadoPedido}`.toLowerCase().includes(term);
      return matchesTerm && (this.status() === 'TODOS' || order.estadoPedido.toUpperCase() === this.status());
    });
  });
  readonly activeOrders = computed(() => this.orders().filter((order) => !['ENTREGADO', 'CANCELADO'].includes(order.estadoPedido.toUpperCase())).length);
  readonly pendingOrders = computed(() => this.orders().filter((order) => order.estadoPedido.toUpperCase() === 'PENDIENTE').length);
  readonly todayTotal = computed(() => this.orders().reduce((sum, order) => sum + Number(order.total || 0), 0));

  ngOnInit(): void {
    this.auth.loadSession();
    const current = this.auth.currentUser();
    if (!current || current.rol?.toUpperCase() !== 'ADMIN') { void this.router.navigateByUrl('/login'); return; }
    this.user.set(current); this.loadOrders();
  }

  loadOrders(): void {
    this.loading.set(true); this.error.set('');
    forkJoin({ orders: this.auth.getAuthenticated<AdminOrder[]>('/api/pedidos/admin'), clients: this.auth.getAuthenticated<Client[]>('/api/clientes') }).subscribe({
      next: ({ orders, clients }) => { this.orders.set([...orders].sort((a, b) => new Date(b.fechaPedido).getTime() - new Date(a.fechaPedido).getTime())); this.clients.set(clients); this.loading.set(false); },
      error: () => { this.error.set('No se pudieron cargar los pedidos. Verifica tu sesión y el backend.'); this.loading.set(false); },
    });
  }

  clientName(id: number): string { const client = this.clients().find((item) => item.idCliente === id); return client ? `${client.nombres} ${client.apellidos}` : `Cliente #${id}`; }
  orderLabel(order: AdminOrder): string { return `#LP-${new Date(order.fechaPedido).getFullYear()}-${String(order.idPedido).padStart(6, '0')}`; }
  statusLabel(status: string): string { return status.replaceAll('_', ' ').toLowerCase().replace(/(^|\s)\S/g, (letter) => letter.toUpperCase()); }
  statusOptions(order: AdminOrder): string[] {
    const transitions: Record<string, string[]> = {
      PENDIENTE: ['PENDIENTE', 'CONFIRMADO', 'CANCELADO'],
      CONFIRMADO: ['CONFIRMADO', 'EN_PREPARACION', 'CANCELADO'],
      EN_PREPARACION: ['EN_PREPARACION', 'ENVIADO'],
      ENVIADO: ['ENVIADO', 'ENTREGADO'],
      ENTREGADO: ['ENTREGADO'],
      CANCELADO: ['CANCELADO'],
    };
    const current = order.estadoPedido.toUpperCase();
    return transitions[current] || [current];
  }
  openOrder(order: AdminOrder): void { this.selected.set(order); this.success.set(''); }
  closeOrder(): void { this.selected.set(null); }
  updateStatus(order: AdminOrder, status: string): void {
    if (status === order.estadoPedido) return;
    this.auth.patchAuthenticated<AdminOrder>('/api/pedidos/' + order.idPedido + '/estado', { estado: status }).subscribe({
      next: (updated) => { this.orders.update((items) => items.map((item) => item.idPedido === updated.idPedido ? updated : item)); this.selected.set(updated); this.success.set('Estado del pedido actualizado correctamente.'); },
      error: (error) => { this.selected.set({ ...order }); this.error.set(error?.error?.message || 'No se pudo actualizar el estado del pedido.'); },
    });
  }
  selectSection(section: string): void { if (section === 'Dashboard') void this.router.navigateByUrl('/admin'); else if (section === 'Catálogo') void this.router.navigateByUrl('/admin/catalogo'); else if (section === 'Usuarios') void this.router.navigateByUrl('/admin/usuarios'); }
}
